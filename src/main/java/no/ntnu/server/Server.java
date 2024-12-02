package no.ntnu.server;

import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.*;
import javax.crypto.SecretKey;

import no.ntnu.nodes.SensorState;
import no.ntnu.security.EncryptionUtils;

public class Server {
  private static final int PORT = 12345;
  private final ExecutorService threadPool = Executors.newCachedThreadPool();
  private final Map<Integer, SensorState> sensorStates = new ConcurrentHashMap<>();
  private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
  private PublicKey publicKey;
  private PrivateKey privateKey;

  public static void main(String[] args) {
    new Server().start();
  }

  public void start() {
    // Generate RSA key pair
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(2048);
      KeyPair keyPair = keyGen.generateKeyPair();
      publicKey = keyPair.getPublic();
      privateKey = keyPair.getPrivate();
    } catch (Exception e) {
      System.err.println("Failed to generate key pair: " + e.getMessage());
      return;
    }

    // Initialize some sample sensor states
    sensorStates.put(1, new SensorState(1));
    sensorStates.put(2, new SensorState(2));
    sensorStates.put(3, new SensorState(3));
    System.out.println("Initialized sensor states: " + sensorStates);

    // Start periodic sensor updates
    startSensorUpdates();

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("Server is running on port " + PORT);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getInetAddress());
        ClientHandler clientHandler = new ClientHandler(clientSocket);
        clients.add(clientHandler);
        threadPool.execute(clientHandler);
      }
    } catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }

  private void startSensorUpdates() {
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      synchronized (sensorStates) {
        for (SensorState state : sensorStates.values()) {
          state.updateRandomly(); // Simulate realistic updates
          String updateMessage = state.toUpdateMessage();
          System.out.println("Broadcasting: " + updateMessage);
          broadcastUpdate(updateMessage);
        }
      }
    }, 0, 1, TimeUnit.SECONDS);
  }

  private void broadcastUpdate(String message) {
    System.out.println("Broadcasting to all clients: " + message);
    for (ClientHandler client : clients) {
      client.sendMessage(message);
    }
  }

  private class ClientHandler implements Runnable {
    private final Socket socket;
    private PrintWriter out;
    private SecretKey aesKey;

    public ClientHandler(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
        out = new PrintWriter(socket.getOutputStream(), true);

        // Send public key to client
        out.println(Base64.getEncoder().encodeToString(publicKey.getEncoded()));

        // Receive encrypted AES key from client
        String encryptedAESKey = in.readLine();
        aesKey = EncryptionUtils.decryptAESKeyWithPrivateKey(encryptedAESKey, privateKey);

        // Send initial sensor states to the client
        synchronized (sensorStates) {
          for (SensorState state : sensorStates.values()) {
            sendMessage(state.toUpdateMessage());
          }
        }

        String message;
        while ((message = in.readLine()) != null) {
          try {
            String decryptedMessage = EncryptionUtils.decryptWithAES(message, aesKey);
            System.out.println("Received command: " + decryptedMessage);
            if (decryptedMessage.startsWith("COMMAND|")) {
              processCommand(decryptedMessage);
            }
          } catch (Exception e) {
            System.err.println("Decryption error: " + e.getMessage());
          }
        }
      } catch (IOException e) {
        System.err.println("Client disconnected: " + e.getMessage());
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        disconnect();
      }
    }

    private void processCommand(String command) {
      String[] parts = command.split("\\|");
      if (parts.length < 3) {
        System.err.println("Invalid command format: " + command);
        return;
      }

      int nodeId;
      try {
        nodeId = Integer.parseInt(parts[1]);
      } catch (NumberFormatException e) {
        System.err.println("Invalid node ID: " + parts[1]);
        return;
      }

      SensorState sensorState = sensorStates.get(nodeId);
      if (sensorState == null) {
        System.err.println("Sensor node not found: " + nodeId);
        return;
      }

      // Handle multiple actuator updates in one command
      for (int i = 2; i < parts.length; i++) {
        String[] actuatorUpdate = parts[i].split("=");
        if (actuatorUpdate.length == 2) {
          String actuator = actuatorUpdate[0];
          boolean state = actuatorUpdate[1].equalsIgnoreCase("on");
          sensorState.setActuatorState(actuator, state);
          System.out.println("Updated actuator state for node " + nodeId + ": " + actuator + " -> " + state);
        } else {
          System.err.println("Invalid actuator command: " + parts[i]);
        }
      }

      // Broadcast updated state to all clients
      String updateMessage = sensorState.toUpdateMessage();
      broadcastUpdate(updateMessage);
    }

    public void sendMessage(String message) {
      try {
        String encryptedMessage = EncryptionUtils.encryptWithAES(message, aesKey);
        out.println(encryptedMessage);
      } catch (Exception e) {
        System.err.println("Encryption error: " + e.getMessage());
      }
    }

    private void disconnect() {
      try {
        clients.remove(this);
        socket.close();
      } catch (IOException e) {
        System.err.println("Error closing client socket: " + e.getMessage());
      }
    }
  }
}