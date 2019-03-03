package cn.nicerpc.consumer.handler;

import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.core.DefaultFuture;
import cn.nicerpc.registry.api.Registry;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if ("ping".equals(msg.toString())) {
            System.out.println("receive ping from server");
            ctx.channel().writeAndFlush("ping\r\n");
            return;
        }
        Response response = JSON.parseObject(msg.toString(),Response.class);
        DefaultFuture.receive(response);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
