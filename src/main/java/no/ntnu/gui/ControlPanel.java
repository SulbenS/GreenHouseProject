package no.ntnu.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import no.ntnu.node.Actuator;
import no.ntnu.node.SensorReading;
import no.ntnu.node.Node;
import no.ntnu.listeners.node.ActuatorListener;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;

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
public class ControlPanel implements GreenhouseEventListener, ActuatorListener,
    CommunicationChannelListener {
  private final List<GreenhouseEventListener> listeners = new LinkedList<>();

  private CommunicationChannelListener communicationChannelListener;

  private List<Node> nodes = new ArrayList<>();

  /**
   * Create a control panel.
   */
  public ControlPanel() {

  }

  /**
   * Set listener which will get notified when communication channel is closed.
   *
   * @param listener The listener
   */
  public void setCommunicationChannelListener(CommunicationChannelListener listener) {
    this.communicationChannelListener = listener;
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

  @Override
  public void onCommunicationChannelClosed() {
    if (communicationChannelListener != null) {
      communicationChannelListener.onCommunicationChannelClosed();
    }
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