package cn.nicerpc.consumer.handler;

import cn.nicerpc.common.handler.CommonHeartbeatHandler;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.core.DefaultFuture;
import cn.nicerpc.registry.api.Registry;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandler extends CommonHeartbeatHandler {


    public SimpleClientHandler() {
        super("client-handler");
    }

    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, String msg) {
        Response response = JSON.parseObject(msg,Response.class);
        DefaultFuture.receive(response);
    }

    /**
     * 客户端关注ALL_IDLE
     * @param ctx
     */
    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        super.handleAllIdle(ctx);
        sendPingMsg(ctx);
    }
}
