package com.vogonsoft.rxjava;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

class HttpTcpNettyServer
{
  public static void testMain() throws Exception
  {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      new ServerBootstrap()
        .option(ChannelOption.SO_BACKLOG, 50_000)
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new HttpInitializer())
        .bind(8080)
        .sync()
        .channel()
        .closeFuture()
        .sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
