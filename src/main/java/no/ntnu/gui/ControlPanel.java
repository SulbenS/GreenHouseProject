package no.ntnu.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import no.ntnu.commands.Data;
import no.ntnu.commands.SensorReadingMessage;
import no.ntnu.listeners.GreenhouseEventListener;
import no.ntnu.listeners.node.ActuatorListener;
import no.ntnu.node.Actuator;
import no.ntnu.node.Node;
import no.ntnu.node.SensorReading;
import no.ntnu.tools.MessageSerializer;

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
public class ControlPanel implements GreenhouseEventListener, ActuatorListener {
  private final List<GreenhouseEventListener> listeners = new LinkedList<>();

  private Socket socket;

  private BufferedReader reader;
  private PrintWriter writer;

  /**
   * Create a control panel.
   */
  public ControlPanel() {

  }

  /**
   * Start the control panel.
   */
  public void start() {
    try {
      establishConnection();
    } catch (IllegalArgumentException e) {
      System.out.println("Could not establish connection to node.");
      System.out.println(e.getMessage());
    }
    while (true) {
      String rawMessage = readMessage();
      Data dataType = MessageSerializer.getDataType(rawMessage);
      if (dataType instanceof SensorReadingMessage) {
        //:TODO: Implement this
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
    }
    try {
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
   * Add an event listener.
   *
   * @param listener The listener who will be notified on all events
   */
  public void addListener(GreenhouseEventListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  @Override
  public void onNodeAdded(Node node) {
    listeners.forEach(listener -> listener.onNodeAdded(node));
  }

  @Override
  public void onNodeRemoved(int nodeId) {
    listeners.forEach(listener -> listener.onNodeRemoved(nodeId));
  }

  @Override
  public void onSensorData(int nodeId, List<SensorReading> sensors) {
    listeners.forEach(listener -> listener.onSensorData(nodeId, sensors));
  }

  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn) {
    listeners.forEach(listener -> listener.onActuatorStateChanged(nodeId, actuatorId, isOn));
  }

  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    listeners.forEach(listener ->
        listener.onActuatorStateChanged(nodeId, actuator.getId(), actuator.isOn())
    );
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