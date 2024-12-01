package no.ntnu.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import no.ntnu.commands.ActuatorIdentifier;
import no.ntnu.commands.SensorIdentifier;
import no.ntnu.gui.greenhouse.GreenhouseApplication;
import no.ntnu.node.Node;
import no.ntnu.commands.Data;
import no.ntnu.tools.MessageHandler;
import no.ntnu.commands.SensorReadingMessage;

/**
 * The central logic of a control panel node. It uses a communication channel to send commands
 * and receive events. It supports listeners who will be notified on changes (for example, a new
 * node is added to the network, or a new sensor reading is received).
 * Note: this class may look like unnecessary forwarding of events to the GUI. In real projects
 * (read: "big projects") this logic class may do some "real processing" - such as storing events
 * in a database, doing some checks, sending emails, notifications, etc. Such things should never
 * be placed inside a GUI class (JavaFX classes). Therefore, we use proper structure here, even
 * though you may have no real control-panel logic in your projects.
 */
public class ControlPanel  {
  private GreenhouseApplication application;

  private Socket socket;

  private BufferedReader reader;
  private PrintWriter writer;

  boolean running;

  /**
   * Create a control panel.
   */
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
    while (this.running) {
      String rawMessage = readMessage();
      Data data = MessageHandler.getData(rawMessage);
      if (data instanceof SensorIdentifier sensorIdentifier) {
        if (!this.application.hasNodeTab(sensorIdentifier.getNodeId())) {
          this.application.addNodeTab(data.getNodeId());
        }
        this.application.getNodeTab(data.getNodeId()).addSensorPane(sensorIdentifier.getNodeId(), sensorIdentifier.getType());
      } else if (data instanceof ActuatorIdentifier actuatorIdentifier) {
        if (!this.application.hasNodeTab(actuatorIdentifier.getNodeId())) {
          this.application.addNodeTab(data.getNodeId());
        }
        this.application.getNodeTab(data.getNodeId()).addActuatorPane(actuatorIdentifier.getNodeId(), actuatorIdentifier.getType());
      } else if (data instanceof SensorReadingMessage sensorReadingMessage) {
        // Create noteTab if it does not exist
        if (!this.application.hasNodeTab(sensorReadingMessage.getNodeId())) {
          this.application.addNodeTab(sensorReadingMessage.getNodeId());
        }
        this.application.getNodeTab(sensorReadingMessage.getNodeId()).updateSensorReading(sensorReadingMessage.getSensorId(), sensorReadingMessage.getReading());
      } else {
        System.out.println("Unknown message type received.");
      }
    }
  }

  /**
   * Establishes a connection to the server.
   */
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

  /**
   * Returns the message from the server.
   *
   * @return The message from the server.
   */
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

  /**
   * Request the server to close the application.
   */
  public void closeApplication() {
    // TODO: Implement this method
  }

  /**
   * Request the server to add a new node.
   */
  public Node requestNode(int nodeId) {
    throw new UnsupportedOperationException("Not implemented yet");
    // TODO: Implement this method
  }
}