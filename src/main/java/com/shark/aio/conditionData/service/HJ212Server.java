package com.shark.aio.conditionData.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author DaHuaJia
 * @Describe 环保设备对接程序启动类，Netty服务器端启动类，接收设备上传的数据。
 * @Date 2022-10-09 11:08:32
 */
@Slf4j
@Component
@Order(1)
public class HJ212Server implements ApplicationRunner {

    /**
     * Netty服务端监听的端口号
     */
    public static final int PORT = 9999;

    /**
     * 分发线程组,用于处理客户端的连接请求
     */
    //表示一个 NIO 的EventLoopGroup
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(2);

    /**
     * 工作线程组, 用于处理与各个客户端连接的 IO 操作
     */
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4);


    /**
     * 启动服务
     */
    public static void runEPServer(){
        log.info("===== EP Netty Server start =====");
        try{
            //ServerBootstrap是一个用来创建服务端Channel的工具类，创建出来的Channel用来接收进来的请求；只用来做面向连接的传输，像TCP/IP。
            ServerBootstrap b = new ServerBootstrap();
            //创建事件循环组
            b.group(bossGroup, workerGroup);
            //指定 Channel 的类型. 因为是服务器端, 因此使用了 NioServerSocketChannel.
            b.channel(NioServerSocketChannel.class);
            //Handler: 设置数据的处理器,指定client处理Handler
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    //接收课客户端请求的处理流程
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("serverDecoder", new StringDecoder(CharsetUtil.UTF_8));
                    pipeline.addLast("serverEncoder", new StringEncoder(CharsetUtil.UTF_8));
                    // netty提供了空闲状态监测处理器 0表示禁用事件
                    pipeline.addLast(new IdleStateHandler(65,0,0, TimeUnit.MINUTES));
                    pipeline.addLast(new HJ212ServerHandler());
                }
            });
            log.info("环保 Netty Server PORT = " + PORT);
            b.bind(PORT).sync();

        }catch (Exception e){
            e.printStackTrace();
            shutdown();
        }
    }

    /**
     * 关闭服务
     */
    public static void shutdown(){
        // 优雅关闭
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    @Override
    public void run(ApplicationArguments args) {
        // 启动环保监测Netty服务端
        runEPServer();
    }

}
