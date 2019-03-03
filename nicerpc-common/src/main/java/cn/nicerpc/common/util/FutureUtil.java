package cn.nicerpc.common.util;

import io.netty.channel.ChannelFuture;

public class FutureUtil {

    public static void closeQuietly(ChannelFuture future) {
        if (future != null) {
            if (!future.isDone()) {
                future.cancel(true);
                return;
            }
            if (future.channel() != null && future.channel().isOpen()) {
                future.channel().close();
                return;
            }
        }
    }
}
