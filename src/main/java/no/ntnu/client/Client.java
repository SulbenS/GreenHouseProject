package no.ntnu.client;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
      // Establish connection to the server
      Socket socket = new Socket(host, port);
      out = new PrintWriter(socket.getOutputStream(), true);

      // Start a new thread to listen for incoming updates from the server
      new Thread(() -> {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
          String message;
          while ((message = in.readLine()) != null) {
            // Pass received messages to the listener for processing
            if (listener != null) {
              final String finalMessage = message;
              Platform.runLater(() -> listener.onUpdateReceived(finalMessage));
            }
          }
        } catch (IOException e) {
          System.err.println("Error reading from server: " + e.getMessage());
        }
      }).start();

    } catch (IOException e) {
      System.err.println("Failed to connect to server: " + e.getMessage());
    }
  }


  public void sendCommand(String command) {
    if (out != null) {
      System.out.println("Sending command: " + command); // Debug statement
      out.println(command);
    } else {
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
