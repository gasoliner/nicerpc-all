package cn.nicerpc.consumer.core;

import io.netty.channel.ChannelFuture;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class ChannelManager {

    private static int pollingIndex = 0;

    static CopyOnWriteArraySet<String> realServerPathSet = new CopyOnWriteArraySet<>();

    public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();

    public static void removeChannel(ChannelFuture channelFuture) {
        channelFutures.remove(channelFuture);
    }

    public static void addChannel(ChannelFuture channelFuture) {
        channelFutures.add(channelFuture);
    }

    public static void clear() {
        channelFutures.clear();
    }

    public static ChannelFuture get() {
        return channelFutures.get(pollingIndex);
    }
}
