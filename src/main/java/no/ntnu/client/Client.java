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
import javax.crypto.SecretKey;
import no.ntnu.security.EncryptionUtils;

public class Client {
  private final String host;
  private final int port;
  private PrintWriter out;
  private Socket socket;
  private final Queue<String> commandBuffer = new ConcurrentLinkedQueue<>();
  private volatile boolean isConnected = false;
  private ClientListener listener;
  private SecretKey aesKey;

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

    // Exchange keys with server (assuming server sends public key)
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    String publicKeyString = in.readLine();
    try {
      aesKey = EncryptionUtils.generateAESKey();
      String encryptedAESKey = EncryptionUtils.encryptWithPublicKey(aesKey, EncryptionUtils.stringToPublicKey(publicKeyString));
      out.println(encryptedAESKey);
    } catch (Exception e) {
      System.err.println("Failed to generate or encrypt AES key: " + e.getMessage());
      return;
    }

    // Start a thread to listen for server updates
    new Thread(() -> {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
        String message;
        while ((message = reader.readLine()) != null) {
          try {
            String decryptedMessage = EncryptionUtils.decryptWithAES(message, aesKey);
            System.out.println("Decrypted server message: " + decryptedMessage);
            Platform.runLater(() -> listener.onUpdateReceived(decryptedMessage));
          } catch (Exception e) {
            System.err.println("Decryption error: " + e.getMessage());
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
      try {
        String encryptedCommand = EncryptionUtils.encryptWithAES(command, aesKey);
        out.println(encryptedCommand);
        System.out.println("Sent encrypted command: " + encryptedCommand); // Debug log
      } catch (Exception e) {
        System.err.println("Encryption error: " + e.getMessage());
      }
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
