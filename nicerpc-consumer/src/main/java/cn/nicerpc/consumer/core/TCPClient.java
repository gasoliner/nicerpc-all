package cn.nicerpc.consumer.core;

import cn.nicerpc.common.constant.Constants;
import cn.nicerpc.common.zk.ZookeeperFactory;
import cn.nicerpc.consumer.handler.SimpleClientHandler;
import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TCPClient {

    public static TCPClient getInstance(String host, int port, int timeout) throws Exception {
        String key = host + "#" + port;
        TCPClient client = clientMap.get(key);
        if (client != null && client.channel.isActive()) {
            return client;
        }
        if (client != null) {
//            断线
            client.channel.close();
            client.channel = null;
            clientMap.remove(key);
        }
        client = new TCPClient(host, port, timeout);
        clientMap.put(key, client);
        return client;
    }

    private static final ConcurrentHashMap<String, TCPClient> clientMap =
            new ConcurrentHashMap<>();

    private Bootstrap bootstrap = new Bootstrap();
    private volatile Channel channel;

    private TCPClient(String host, int port, int timeout) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup); // (2)
        bootstrap.channel(NioSocketChannel.class); // (3)
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new SimpleClientHandler());
                ch.pipeline().addLast(new StringEncoder());
            }
        });
        doConnect(host, port, timeout);
    }

    private void doConnect(String host, int port, int timeout) throws Exception {
        ChannelFuture connect = bootstrap.connect(host, port);

        boolean ret = connect.awaitUninterruptibly(timeout, TimeUnit.MILLISECONDS);
        if (ret && connect.isSuccess()) {
//                关闭老连接
            Channel oldChannel = TCPClient.this.channel;
            if (oldChannel != null) {
                try {
                    oldChannel.close();
                } finally {
                    //doSomething
                }
            }
            TCPClient.this.channel = connect.channel();
        } else {
            throw new Exception("remote connect failed host = " + host + " port = " + port);
        }
    }

    /**
     * 发送数据
     * 1、每一个请求都是同一个连接，会有并发问题，所以有以下解决方式：
     * 使用请求id、响应id来区分
     * 请求：
     * 1.请求id
     * 2.请求内容
     * 响应：
     * 1.响应id
     * 2.响应内容
     *
     * @param request
     * @return
     */
    public Response send(ClientRequest request) {
//        todo 扩展多种编码方式
        channel.writeAndFlush(JSON.toJSONString(request));
        channel.writeAndFlush("\r\n");

        DefaultFuture future = new DefaultFuture(request);
        return future.get();
    }

}
