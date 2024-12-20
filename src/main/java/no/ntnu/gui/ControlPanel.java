package no.ntnu.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Platform;
import no.ntnu.commands.*;
import no.ntnu.gui.greenhouse.GreenhouseApplication;
import no.ntnu.listeners.NodeTabObserver;
import no.ntnu.listeners.node.ActuatorListener;
import no.ntnu.node.Node;
import no.ntnu.tools.MessageHandler;

/**
 * The control panel of the application.
 */
public class ControlPanel implements ActuatorListener, NodeTabObserver {
  private GreenhouseApplication application;

  private Socket socket;

  private BufferedReader reader;
  private PrintWriter writer;

  private boolean running;

  public ControlPanel(GreenhouseApplication application) {
    this.application = application;
    this.running = true;
  }

  /**
   * Starts the control panel.
   */
  public void start() {
    try {
      establishConnection();
    } catch (IllegalArgumentException e) {
      System.out.println("Could not establish connection to node.");
      System.out.println(e.getMessage());
    }
    new Thread(() -> {
      System.out.println("Starting to read messages.");
      while (this.running) {
        String rawMessage = readMessage();
        Data data = MessageHandler.getData(rawMessage);
        Platform.runLater(() -> executeCommand(data));
      }
    }).start();
  }

  private void executeCommand(Data data) {
    if (data instanceof NodeIdentifier nodeIdentifier) {
      if (!this.application.hasNodeTab(nodeIdentifier.getNodeId())) {
        this.application.addNodeTab(nodeIdentifier.getNodeId());
        System.out.println("Node added in GUI: " + nodeIdentifier.getNodeId());
        System.out.println("ARE WE REACHING THIS?");
      }
    } else if (data instanceof SensorIdentifier sensorIdentifier) {
      if (!this.application.hasNodeTab(sensorIdentifier.getNodeId())) {
        this.application.addNodeTab(sensorIdentifier.getNodeId());
      }
      if (!this.application
              .getNodeTab(sensorIdentifier.getNodeId())
              .hasSensorPane(sensorIdentifier.getSensorId())) {
        this.application
                .getNodeTab(sensorIdentifier.getNodeId())
                .addSensorPane(
                        sensorIdentifier.getSensorId(),
                        sensorIdentifier.getType());
      }
    } else if (data instanceof ActuatorIdentifier actuatorIdentifier) {
      if (!this.application.hasNodeTab(actuatorIdentifier.getNodeId())) {
        this.application.addNodeTab(actuatorIdentifier.getNodeId());
      }
      if (!this.application
              .getNodeTab(actuatorIdentifier.getNodeId())
              .hasActuatorPane(actuatorIdentifier.getActuatorId())) {
        this.application
            .getNodeTab(actuatorIdentifier.getNodeId())
            .addActuatorPane(
                actuatorIdentifier.getNodeId(),
                actuatorIdentifier.getActuatorId(),
                actuatorIdentifier.getType(),
                actuatorIdentifier.getState()
            );
      }
    } else if (data instanceof SensorReadingMessage sensorReadingMessage) {
      if (!this.application.hasNodeTab(sensorReadingMessage.getNodeId())) {
        this.application.addNodeTab(sensorReadingMessage.getNodeId());
      }
      if (!this.application
              .getNodeTab(sensorReadingMessage.getNodeId())
              .hasSensorPane(sensorReadingMessage.getSensorId())) {
        this.application
                .getNodeTab(sensorReadingMessage.getNodeId())
                .addSensorPane(
                        sensorReadingMessage.getSensorId(),
                        sensorReadingMessage.getType());
      }
      this.application
              .getNodeTab(sensorReadingMessage.getNodeId())
              .updateSensorReading(
                      sensorReadingMessage.getSensorId(),
                      sensorReadingMessage.getValue());
    } else {
      System.out.println("Unknown message type received: " + data.getData());
    }
  }

  /**
   * Establishes a connection to the server.
   */
  public void establishConnection() {
    try {
      System.out.println("Attempting to establish connection to the server.");
      this.socket = new Socket("localhost", 1238);
      System.out.println("Connection established.");
    } catch (IOException e) {
      System.out.println("Could not connect to the server.");
      System.out.println(e.getMessage());
      throw new IllegalArgumentException("Could not connect to the server");
    }
    try {
      this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      this.writer = new PrintWriter(this.socket.getOutputStream(), true);
      this.writer.println("Data=Identifier;Node=-1");
    } catch (IOException e) {
      System.out.println("Could not create the reader/writer.");
      System.out.println(e.getMessage());
      throw new IllegalArgumentException("Could not create the reader/writer");
    }
  }

  /**
   * Reads a message from the server.
   *
   * @return The message read from the server.
   */
  public String readMessage() {
    String rawMessage = "";
    try {
      System.out.println("Waiting for message.");
      rawMessage = this.reader.readLine();
      System.out.println("Received message: " + rawMessage);
    } catch (IOException e) {
      System.out.println("Could not read the message.");
      System.out.println(e.getMessage());
    }
    return rawMessage;
  }

  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, boolean newState) {
    System.out.println("Actuator " + actuatorId
            + " NodeId " + nodeId
            + " state changed to "
            + (newState ? "On" : "Off"));
    writeMessage("Data=ActuatorCommand;Node=" + nodeId
            + ";Actuator=" + actuatorId + ";Action="
            + (newState ? "On" : "Off"));
  }

  public void writeMessage(String message) {
    this.writer.println(message);
  }

  public void closeApplication() {
    this.writeMessage("Data=Stop;Node=0");
  }

  @Override
  public void onActuatorAddedInGui(int nodeId, String actuatorType) {
    System.out.println("Controlpanel notified actuatorpane is added in GUI: " + actuatorType);
    writeMessage("Data=ActuatorAddedInGui;Node=" + nodeId + ";ActuatorType=" + actuatorType);
  }

  /**
   * Stops the control panel.
   */
  @Override
  public void onSensorAddedInGui(int nodeId, String sensorType) {
    System.out.println("Controlpanel notified sensorpane is added in GUI: " + sensorType);
    writeMessage("Data=SensorAddedInGui;Node=" + nodeId + ";SensorType=" + sensorType);
  }

  @Override
  public void onNodeAddedInGui(int nodeId) {
    System.out.println("Controlpanel notified node is added in GUI: " + nodeId);
    writeMessage("Data=NodeAddedInGui;Node=" + nodeId);
  }

  /**
   * Adds a node tab to the application.
   *
   * @param nodeId The ID of the node to add.
   */
  public void addNodeTab(int nodeId) {
    Platform.runLater(() -> {
      this.application.addNodeTab(nodeId);
    });
  }
}