package com.vogonsoft.rxjava;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.buffer.*;
import java.io.UnsupportedEncodingException;

@ChannelHandler.Sharable
class HttpHandler extends ChannelInboundHandlerAdapter
{
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx)
  {
    ctx.flush();
  }
  
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg)
  {
    if (msg instanceof HttpRequest) {
      sendResponse(ctx);
    }
  }
  
  private void sendResponse(ChannelHandlerContext ctx)
  {
    try {
      final DefaultFullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK,
        Unpooled.wrappedBuffer("OK".getBytes("UTF8"))
      );
      response.headers().add("Content-length", 2);
      ctx.writeAndFlush(response);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
  {
    // log.error("Error", cause);
    System.out.println("Error: " + cause.toString());
    ctx.close();
  }
}
