package cn.nicerpc.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import static org.jboss.netty.handler.timeout.IdleState.ALL_IDLE;

public abstract class CommonHeartbeatHandler extends SimpleChannelInboundHandler {
    public static final String PING_MSG = "ping";
    public static final String PONG_MSG = "pong";
//    public static final byte CUSTOM_MSG = 3;
    protected String name;
    private int heartbeatCount = 0;

    public CommonHeartbeatHandler(String name) {
        this.name = name;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Object msg) throws Exception {
        String msgStr = msg.toString();
        if (msgStr.equals(PING_MSG)) {
            sendPongMsg(context);
        } else if (msgStr.equals(PONG_MSG)){
            System.out.println(name + " get pong msg from " + context.channel().remoteAddress());
        } else {
            handleData(context, msgStr);
        }
    }

    protected void sendPingMsg(ChannelHandlerContext context) {
        context.writeAndFlush(PING_MSG + "\r\n");
        heartbeatCount++;
        System.out.println(name + " sent ping msg to " + context.channel().remoteAddress() + ", count: " + heartbeatCount);
    }

    private void sendPongMsg(ChannelHandlerContext context) {
        context.writeAndFlush(PONG_MSG + "\r\n");
        heartbeatCount++;
        System.out.println(name + " sent pong msg to " + context.channel().remoteAddress() + ", count: " + heartbeatCount);
    }

    protected abstract void handleData(ChannelHandlerContext channelHandlerContext, String msg);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("---" + ctx.channel().remoteAddress() + " is active---");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("---" + ctx.channel().remoteAddress() + " is inactive---");
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        System.err.println("---READER_IDLE---");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        System.err.println("---WRITER_IDLE---");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        System.err.println("---ALL_IDLE---");
    }

}
