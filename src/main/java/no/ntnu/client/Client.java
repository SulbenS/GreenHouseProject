package no.ntnu.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.LinkedList;

public class Client {
  private final String host;
  private final int port;
  private PrintWriter out;
  private Queue<String> commandBuffer = new LinkedList<>();
  private boolean isConnected = false;
  private ClientListener listener;

  public Client(String host, int port) {
    this.host = host;
    this.port = port;
    connect();
  }

  private void connect() {
    try {
      Socket socket = new Socket(host, port);
      out = new PrintWriter(socket.getOutputStream(), true);
      isConnected = true;

      // Immediately flush the buffer upon connection
      flushBuffer();
    } catch (IOException e) {
      isConnected = false;
      System.err.println("Failed to connect to server: " + e.getMessage());
    }
  }

  public void sendCommand(String command) {
    if (isConnected && out != null) {
      out.println(command);
    } else {
      // If not connected, buffer the command
      commandBuffer.add(command);
      System.err.println("Connection lost. Buffering command: " + command);
    }
  }

  public void flushBuffer() {
    while (!commandBuffer.isEmpty() && isConnected) {
      String bufferedCommand = commandBuffer.poll();
      if (bufferedCommand != null) {
        out.println(bufferedCommand);
        System.out.println("Flushed buffered command: " + bufferedCommand);
      }
    }
  }

  public void setListener(ClientListener listener) {
    this.listener = listener;
  }
}
