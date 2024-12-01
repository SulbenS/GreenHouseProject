package no.ntnu.client;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client {
  private final String host;
  private final int port;
  private PrintWriter out;
  private Socket socket;
  private final Queue<String> commandBuffer = new ConcurrentLinkedQueue<>();
  private volatile boolean isConnected = false;
  private ClientListener listener;

  public Client(String host, int port) {
    this.host = host;
    this.port = port;
    startHeartbeat(); // Start heartbeat for connection health checks
    connectWithRetry(); // Try to connect immediately
  }

  private void connectWithRetry() {
    Executors.newSingleThreadExecutor().submit(() -> {
      while (!isConnected) {
        try {
          connect();
          isConnected = true;
          System.out.println("Connected to server");
          retransmitBufferedCommands(); // Send buffered commands after reconnection
        } catch (Exception e) {
          System.err.println("Failed to connect to server, retrying in 5 seconds...");
          try {
            Thread.sleep(5000); // Wait before retrying
          } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            break;
          }
        }
      }
    });
  }

  private void connect() throws IOException {
    socket = new Socket(host, port);
    out = new PrintWriter(socket.getOutputStream(), true);

    // Start a thread to listen for server updates
    new Thread(() -> {
      try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
        String message;
        while ((message = in.readLine()) != null) {
          if (listener != null) {
            final String finalMessage = message;
            Platform.runLater(() -> listener.onUpdateReceived(finalMessage));
          }
        }
      } catch (IOException e) {
        System.err.println("Connection lost. Attempting to reconnect...");
        isConnected = false;
        connectWithRetry(); // Trigger reconnection logic
      }
    }).start();
  }

  public void sendCommand(String command) {
    if (isConnected && out != null) {
      out.println(command);
      System.out.println("Sent command: " + command); // Debug log
    } else {
      commandBuffer.add(command);
      System.err.println("Connection lost. Buffered command: " + command);
    }
  }

  private void retransmitBufferedCommands() {
    while (!commandBuffer.isEmpty()) {
      String command = commandBuffer.poll();
      sendCommand(command); // Resend buffered commands
    }
  }

  private void startHeartbeat() {
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      if (isConnected) {
        sendCommand("PING"); // Heartbeat message
      }
    }, 0, 5, TimeUnit.SECONDS); // Send a PING every 5 seconds
  }

  public void setListener(ClientListener listener) {
    this.listener = listener;
  }
}
