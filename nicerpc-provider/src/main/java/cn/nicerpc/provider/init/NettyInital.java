package cn.nicerpc.provider.init;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.util.NetUtil;
import cn.nicerpc.provider.handler.ServerHandler;
import cn.nicerpc.registry.api.Registry;
import cn.nicerpc.registry.impl.DefaultRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Component
public class NettyInital implements ApplicationListener<ContextRefreshedEvent> {

    public static int port;

    public static String host = NetUtil.getHostAddr();

    static {
        System.out.println("请输入服务器端口号");
        Scanner scanner = new Scanner(System.in);
        port = scanner.nextInt();
    }

    private void start() {

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
                            ch.pipeline().addLast(new ServerHandler());
                            ch.pipeline().addLast(new StringEncoder());
                        }
                    });
            ChannelFuture sync = bootstrap.bind(port).sync();
            System.out.println("服务器启动成功。。。");
            System.out.println("服务器ip：" + host + " 端口号 " + port);
            sync.channel().closeFuture().sync();
            System.out.println("服务器已优雅关闭。。。");
        } catch (Exception e) {
            e.printStackTrace();
            parent.shutdownGracefully();
            child.shutdownGracefully();
        }

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.start();
    }
}
