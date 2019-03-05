package cn.nicerpc.provider.handler;

import cn.nicerpc.common.handler.CommonHeartbeatHandler;
import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.ServerRequest;
import cn.nicerpc.provider.medium.Media;
import cn.nicerpc.provider.util.Response;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends CommonHeartbeatHandler {


    public ServerHandler() {
        super("server-handler");
    }

    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, String msg) {
        ClientRequest request = JSON.parseObject(msg, ClientRequest.class);

        Media media = Media.newInstance();
        Object o = media.process(request);

        Response response = new Response();
        response.setId(request.getId());
        response.setResult(o);

        channelHandlerContext.channel().writeAndFlush(JSON.toJSONString(response));
        channelHandlerContext.channel().writeAndFlush("\r\n");
    }

    /**
     * 服务端关注READER_IDLE
     * @param ctx
     */
    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        System.err.println("---client " + ctx.channel().remoteAddress().toString() + " reader timeout, close it---");
        ctx.close();
    }
}
