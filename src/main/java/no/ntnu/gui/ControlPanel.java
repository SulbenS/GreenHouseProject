package no.ntnu.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Platform;
import no.ntnu.commands.ActuatorIdentifier;
import no.ntnu.commands.SensorIdentifier;
import no.ntnu.gui.greenhouse.GreenhouseApplication;
import no.ntnu.node.Node;
import no.ntnu.commands.Data;
import no.ntnu.tools.MessageHandler;
import no.ntnu.commands.SensorReadingMessage;

public class ControlPanel {
  private GreenhouseApplication application;

  private Socket socket;

  private BufferedReader reader;
  private PrintWriter writer;

  private boolean running;

  public ControlPanel(GreenhouseApplication application) {

    this.application = application;
    this.running = true;
  }

  public void start() {
    try {
      establishConnection();
    } catch (IllegalArgumentException e) {
      System.out.println("Could not establish connection to node.");
      System.out.println(e.getMessage());
    }
    new Thread(() -> {
      while (this.running) {
        String rawMessage = readMessage();
        Data data = MessageHandler.getData(rawMessage);
        Platform.runLater(() -> executeCommand(data));
      }
    }).start();
  }

  private void executeCommand(Data data) {
    if (data instanceof SensorIdentifier sensorIdentifier) {
      if (!this.application.hasNodeTab(sensorIdentifier.getNodeId())) {
        this.application.addNodeTab(sensorIdentifier.getNodeId());
      }
      this.application
              .getNodeTab(sensorIdentifier.getNodeId())
              .addSensorPane(
                      sensorIdentifier.getSensorId(),
                      sensorIdentifier.getType());
    } else if (data instanceof ActuatorIdentifier actuatorIdentifier) {
      if (!this.application.hasNodeTab(actuatorIdentifier.getNodeId())) {
        this.application.addNodeTab(actuatorIdentifier.getNodeId());
      }
      this.application
              .getNodeTab(actuatorIdentifier.getNodeId())
              .addActuatorPane(
                      actuatorIdentifier.getActuatorId(),
                      actuatorIdentifier.getType());
    } else if (data instanceof SensorReadingMessage sensorReadingMessage) {
      if (!this.application.hasNodeTab(sensorReadingMessage.getNodeId())) {
        this.application.addNodeTab(sensorReadingMessage.getNodeId());
      }
      this.application
              .getNodeTab(sensorReadingMessage.getNodeId())
              .addSensorPane(
                      sensorReadingMessage.getSensorId(),
                      sensorReadingMessage.getValue());
      this.application
              .getNodeTab(sensorReadingMessage.getNodeId())
              .updateSensorReading(
                      sensorReadingMessage.getSensorId(),
                      sensorReadingMessage.getValue());
    } else {
      System.out.println("Unknown message type received:");
      System.out.println(data.getData());
    }
  }

  public void establishConnection() {
    try {
      this.socket = new Socket("localhost", 1238);
    } catch (IOException e) {
      System.out.println("Could not connect to the server.");
      System.out.println(e.getMessage());
      throw new IllegalArgumentException("Could not connect to the server");
    } try {
      this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      this.writer = new PrintWriter(this.socket.getOutputStream(), true);
    } catch (IOException e) {
      System.out.println("Could not create the reader/writer.");
      System.out.println(e.getMessage());
      throw new IllegalArgumentException("Could not create the reader/writer");
    }
  }

  public String readMessage() {
    String rawMessage = "";
    try {
      rawMessage = this.reader.readLine();
    } catch (IOException e) {
      System.out.println("Could not read the message.");
      System.out.println(e.getMessage());
    }
    return rawMessage;
  }

  public void writeMessage(String message) {
    this.writer.println(message);
  }

  public void closeApplication() {
    // TODO: Implement this method
  }

  public Node requestNode(int nodeId) {
    throw new UnsupportedOperationException("Not implemented yet");
    // TODO: Implement this method
  }
}