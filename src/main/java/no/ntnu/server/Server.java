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
    sensorStates.put(1, new SensorState(1));
    sensorStates.put(2, new SensorState(2));
    sensorStates.put(3, new SensorState(3));

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
          broadcastUpdate(updateMessage); // Broadcast updates to all clients
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

    public ClientHandler(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
        out = new PrintWriter(socket.getOutputStream(), true);

        // Send initial sensor states to the client
        synchronized (sensorStates) {
          for (SensorState state : sensorStates.values()) {
            out.println(state.toUpdateMessage());
          }
        }

        String message;
        while ((message = in.readLine()) != null) {
          System.out.println("Received: " + message);
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

    private void send(String message) {
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

    public void sendMessage(String message) {
      send(message);
    }
  }

  private static class SensorState {
    private final int nodeId;
    private double temperature;
    private double humidity;
    private final Map<String, Boolean> actuators;

    public SensorState(int nodeId) {
      this.nodeId = nodeId;
      this.temperature = 25.0; // Default temperature
      this.humidity = 50.0; // Default humidity
      this.actuators = new ConcurrentHashMap<>(); // Use ConcurrentHashMap for thread safety
      actuators.put("heater", false);
      actuators.put("window", false);
      actuators.put("fan", false); // Ensure 'fan' is included
    }

    public synchronized void setActuatorState(String actuator, boolean state) {
      actuators.put(actuator, state);
    }

    public synchronized boolean getActuatorState(String actuator) {
      return actuators.getOrDefault(actuator, false);
    }

    public synchronized void updateRandomly() {
      // Indoor baseline conditions
      double indoorBaselineTemp = 22.0; // Comfortable indoor temperature
      double indoorBaselineHumidity = 50.0; // Comfortable indoor humidity

      // Outdoor influence (weaker indoors due to insulation)
      double outdoorTemperature = 15.0; // Example outdoor temperature
      double outdoorHumidity = 60.0; // Example outdoor humidity

      // Simulate temperature changes
      if (actuators.get("heater")) {
        // Heater gradually increases temperature
        temperature += 0.1 + Math.random() * 0.05; // Small, steady increments
      } else if (actuators.get("window")) {
        // Open windows cause temperature to drift toward outdoor temperature
        temperature += (outdoorTemperature - temperature) * 0.05; // Slow convergence
      } else {
        // Natural cooling toward indoor baseline
        temperature += (indoorBaselineTemp - temperature) * 0.02;
      }

      // Simulate humidity changes
      if (actuators.get("window")) {
        // Open windows lower humidity towards outdoor levels
        humidity += (outdoorHumidity - humidity) * 0.1; // Faster convergence
      } else if (actuators.get("fan")) {
        // Fan lowers humidity toward the indoor baseline
        humidity += (indoorBaselineHumidity - humidity) * 0.05;
      } else {
        // Gradual return to baseline
        humidity += (indoorBaselineHumidity - humidity) * 0.01;
      }

      // Add minor natural fluctuations
      temperature += (Math.random() - 0.5) * 0.05; // Very slight random noise
      humidity += (Math.random() - 0.5) * 0.1; // Slight random noise

      // Clamp values to realistic ranges
      temperature = Math.max(15, Math.min(30, temperature)); // Indoors: 15°C to 30°C
      humidity = Math.max(30, Math.min(70, humidity));       // Indoors: 30% to 70%
    }

    public synchronized String toUpdateMessage() {
      return "UPDATE|nodeId=" + nodeId +
          "|temperature=" + String.format("%.2f", temperature) +
          "|humidity=" + String.format("%.2f", humidity) +
          "|heater=" + (actuators.get("heater") ? "on" : "off") +
          "|window=" + (actuators.get("window") ? "on" : "off") +
          "|fan=" + (actuators.get("fan") ? "on" : "off");
    }
  }
}