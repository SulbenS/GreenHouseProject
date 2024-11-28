package no.ntnu.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
  private static final int PORT = 12345;
  private final ExecutorService threadPool = Executors.newCachedThreadPool();
  private final Map<Integer, SensorState> sensorStates = new ConcurrentHashMap<>();
  private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

  public static void main(String[] args) {
    new Server().start();
  }

  public void start() {
    // Initialize some sample sensor states
    sensorStates.put(1, new SensorState(1, 25.0, 50.0));
    sensorStates.put(2, new SensorState(2, 22.0, 60.0));
    sensorStates.put(3, new SensorState(3, 28.0, 40.0));

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

  private void broadcastUpdate(String message) {
    for (ClientHandler client : clients) {
      client.sendMessage(message);
    }
  }

  private class ClientHandler implements Runnable {
    private final Socket socket;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
        out = new PrintWriter(socket.getOutputStream(), true);

        // Send initial sensor states
        synchronized (sensorStates) {
          for (SensorState state : sensorStates.values()) {
            out.println(state.toUpdateMessage());
          }
        }

        // Handle incoming commands
        String message;
        while ((message = in.readLine()) != null) {
          if (message.startsWith("COMMAND|")) {
            processCommand(message);
          }
        }
      } catch (IOException e) {
        System.err.println("Client disconnected: " + e.getMessage());
      } finally {
        disconnect();
      }
    }

    private void processCommand(String command) {
      String[] parts = command.split("\\|");
      if (parts.length < 3) return;

      int nodeId = Integer.parseInt(parts[1]);
      String[] actuatorUpdate = parts[2].split("=");
      if (actuatorUpdate.length == 2) {
        String actuator = actuatorUpdate[0];
        boolean state = actuatorUpdate[1].equalsIgnoreCase("on");

        SensorState sensorState = sensorStates.get(nodeId);
        if (sensorState != null) {
          sensorState.setActuatorState(actuator, state);

          // Broadcast updated state to all clients
          String updateMessage = sensorState.toUpdateMessage();
          broadcastUpdate(updateMessage);
        }
      }
    }

    private void sendMessage(String message) {
      if (out != null) {
        out.println(message);
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

  private static class SensorState {
    private final int nodeId;
    private double temperature;
    private double humidity;
    private final Map<String, Boolean> actuators;

    public SensorState(int nodeId, double temperature, double humidity) {
      this.nodeId = nodeId;
      this.temperature = temperature;
      this.humidity = humidity;
      this.actuators = new HashMap<>();
      actuators.put("heater", false);
      actuators.put("window", false);
      actuators.put("fan", false);
    }

    public synchronized void setActuatorState(String actuator, boolean state) {
      actuators.put(actuator, state);
    }

    public String toUpdateMessage() {
      return "UPDATE|nodeId=" + nodeId +
          "|temperature=" + String.format("%.2f", temperature) +
          "|humidity=" + String.format("%.2f", humidity) +
          "|heater=" + (actuators.get("heater") ? "on" : "off") +
          "|window=" + (actuators.get("window") ? "on" : "off") +
          "|fan=" + (actuators.get("fan") ? "on" : "off");
    }
  }
}
