package cn.nicerpc.provider.handler;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.ServerRequest;
import cn.nicerpc.provider.medium.Media;
import cn.nicerpc.provider.util.Response;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ClientRequest request = JSON.parseObject(msg.toString(), ClientRequest.class);

        Media media = Media.newInstance();
        Object o = media.process(request);

        Response response = new Response();
        response.setId(request.getId());
        response.setResult(o);

        ctx.channel().writeAndFlush(JSON.toJSONString(response));
        ctx.channel().writeAndFlush("\r\n");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("读空闲==");
                ctx.channel().close();
            } else if (event.equals(IdleState.WRITER_IDLE)) {
                System.out.println("写空闲==");
            } else if (event.equals(IdleState.ALL_IDLE)) {
                System.out.println("读写空闲==");
                ctx.channel().writeAndFlush("ping\r\n");
            }
        }
    }
}
