package cn.nicerpc.consumer.core;

import cn.nicerpc.common.constant.Constants;
import cn.nicerpc.common.zk.ZookeeperFactory;
import cn.nicerpc.consumer.handler.SimpleClientHandler;
import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;
import java.util.Set;

public class TCPClient {

    static final Bootstrap b = new Bootstrap();
    static ChannelFuture f;
    static {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        b.group(workerGroup); // (2)
        b.channel(NioSocketChannel.class); // (3)
        b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new SimpleClientHandler());
                ch.pipeline().addLast(new StringEncoder());
            }
        });

//        host-port设置默认值
//        String host = "localhost";
//        int port = 8081;

//        CuratorFramework client = ZookeeperFactory.create();
//        try {
//            List<String> serverPaths = client.getChildren().forPath(Constants.SERVER_PATH);

//            ServerWatcher watcher = new ServerWatcher();
//            client.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_PATH);

//            Set<String> realServerPath = ServerManager.realServerPathSet;
//            for (String seq :
//                    serverPaths) {
//                System.out.println("TCPClient static serverPath seq == " + seq);
//                String [] serverInfoArray = seq.split("#");
//                realServerPath.add(serverInfoArray[0]+ "#" + serverInfoArray[1]);
//            }
//            if (realServerPath.size() > 0) {
////                选择一台服务器，这里可以扩展，采用轮训，加权轮训等负载均衡算法
//
//                ChannelFuture channelFuture = ServerManager.get();
//
//                String[] serverInfo = realServer.toArray()[0].toString().split("#");
//                host = serverInfo[0];
//                port = Integer.parseInt(serverInfo[1]);
//                System.out.println("新的服务器确认，host : " + host + " port : " + port);
//            }

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            f = b.connect(host, port).sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 发送数据
     * 1、每一个请求都是同一个连接，会有并发问题，所以有以下解决方式：
     *      使用请求id、响应id来区分
     *      请求：
     *      1.请求id
     *      2.请求内容
     *      响应：
     *      1.响应id
     *      2.响应内容
     * @param request
     * @return
     */
    public static Response send(ClientRequest request) {
        try {
            f = ServerManager.get(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response();
        }

//        todo 扩展多种编码方式
        f.channel().writeAndFlush(JSON.toJSONString(request));
        f.channel().writeAndFlush("\r\n");

        DefaultFuture future = new DefaultFuture(request);
        return future.get();
    }

}
