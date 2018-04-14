package com.vogonsoft.rxjava;

import java.io.*;
import java.net.*;
import org.apache.commons.io.*;

class SingleThreadHttpd
{
  public static final byte[] RESPONSE = (
    "HTTP/1.1 200 OK\r\n" +
      "Content-length: 2\r\n" +
      "\r\n" +
      "OK"
    ).getBytes();
    
  public static void testMain() throws IOException
  {
    final ServerSocket serverSocket = new ServerSocket(8080, 100);
    while (!Thread.currentThread().isInterrupted()) {
      try (Socket client = serverSocket.accept()) {
        handle(client);
      } catch (Exception e) {
        e.printStackTrace();
        break;
      }
    }
  }
  
  private static void handle(Socket client) throws IOException
  {
    while (!Thread.currentThread().isInterrupted()) {
      readFullRequest(client);
      client.getOutputStream().write(RESPONSE);
    }
  }
  
  private static void readFullRequest(Socket client) throws IOException
  {
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(client.getInputStream()));
    String line = reader.readLine();
    while (line != null && !line.isEmpty()) {
      line = reader.readLine();
    }
  }
}
