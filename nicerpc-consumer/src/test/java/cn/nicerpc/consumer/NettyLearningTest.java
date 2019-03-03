package cn.nicerpc.consumer;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.consumer.handler.SimpleClientHandler;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class NettyLearningTest {


    @Test
    public void test1() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup); // (2)
        bootstrap.channel(NioSocketChannel.class); // (3)
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,3000); // (4)
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new SimpleClientHandler());
                ch.pipeline().addLast(new StringEncoder());
            }
        });

        ChannelFuture future = bootstrap
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,1000*10)
                .connect("localhost", 8089)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                        channelFuture.
                        System.out.println("完成了");
                        if (channelFuture.isDone()) {
                            System.out.println("真完成了啊！");
                            if (channelFuture.isSuccess()) {
                                System.out.println("而且还成功了！");
                            } else {
                                System.out.println("不过失败了！");
                            }
                        } else {
                            System.out.println("不对不对没有完成！");
                        }
                        int i = 20;
                        while (i-- != 0) {
                            channelFuture.channel().writeAndFlush("from 111 echo \r\n");
                        }
                    }
                });
//        ;


        System.out.println("connect ...");

//        future.await(12,TimeUnit.MILLISECONDS);
//
//        if (future.isDone()) {
//            System.out.println("future is Done");
//            if (future.isSuccess()) {
//                System.out.println("future is success");
//            } else {
//                System.out.println("future is not success");
//                System.out.println("cause: " + future.cause().toString());
//            }
//        } else {
//            System.out.println("future is not done");
//        }

//        System.out.println("awi after");

//        Thread.sleep(1000*5);
//
//        future.channel().writeAndFlush("hello,kugou !\r\n")
//        .addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                System.out.println("发送完成了");
//                if (channelFuture.isDone()) {
//                    System.out.println("发送真完成了啊！");
//                    if (channelFuture.isSuccess()) {
//                        System.out.println("发送而且还成功了！");
//                    } else {
//                        System.out.println("发送不过失败了！");
//                    }
//                } else {
//                    System.out.println("不对不对发送没有完成！");
//                }
//            }
//        });
//
//        Thread.sleep(1000*5);
//
//        future.channel().writeAndFlush("bye bye kugou !\r\n");

        ChannelFuture future2 = bootstrap
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,1000*10)
                .connect("localhost", 8089)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        System.out.println("2完成了");
                        if (channelFuture.isDone()) {
                            System.out.println("2真完成了啊！");
                            if (channelFuture.isSuccess()) {
                                System.out.println("2而且还成功了！");
                            } else {
                                System.out.println("2不过失败了！");
                            }
                        } else {
                            System.out.println("2不对不对没有完成！");
                        }
                        int i = 20;
                        while (i-- != 0) {
                            channelFuture.channel().writeAndFlush("from 222 echo \r\n");
                        }
                    }
                });

        Thread.sleep(1000*600);
    }

    /**
     * echo服务器
     */
    @Test
    public void test2() {
        EventLoopGroup parent = new NioEventLoopGroup();
        EventLoopGroup child = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parent, child);
            bootstrap.option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, false)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new IdleStateHandler(60, 45, 20, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new SimpleChannelInboundHandler() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
                                    System.out.println("服务器接到消息： " + msg.toString());
                                }
                            });
                            ch.pipeline().addLast(new StringEncoder());
                        }
                    });
            ChannelFuture sync = bootstrap.bind(8089).sync();
            System.out.println("服务器启动成功。。。");

//            register2Registry();

//            CuratorFramework client = ZookeeperFactory.create();

//            InetAddress inetAddress = InetAddress.getLocalHost();
//            System.out.println("服务器ip：" + host + " 端口号 " + 8081);
//            client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
//                    .forPath(Constants.SERVER_PATH + inetAddress.getHostAddress() + "#" + port + "#");
//            System.out.println("服务器注册Zookeeper节点成功。。。");

            sync.channel().closeFuture().sync();
            System.out.println("服务器已优雅关闭。。。");
        } catch (Exception e) {
            e.printStackTrace();
            parent.shutdownGracefully();
            child.shutdownGracefully();
        }
    }
}
