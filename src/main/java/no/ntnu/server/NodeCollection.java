package no.ntnu.server;

import no.ntnu.listeners.GreenhouseEventListener;
import no.ntnu.listeners.node.NodeStateListener;
import no.ntnu.node.Node;
import no.ntnu.node.SensorReading;
import no.ntnu.tools.DeviceFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeCollection implements NodeStateListener, GreenhouseEventListener {
  private final Map<Integer, Node> nodes = new HashMap<>();

  private void createNode(int temperature, int humidity, int windows, int fans, int heaters) {
    Node node = DeviceFactory.createNode(
            temperature, humidity, windows, fans, heaters);
    node.addStateListener(this);
    node.start();
    this.nodes.put(node.getId(), node);
  }

  /**
   * Return a specific node by its ID.
   *
   * @param nodeId The ID of the node to get
   * @return The node with the given ID, or null if no such node exists
   */
  public Node getNode(int nodeId) {
    return this.nodes.get(nodeId);
  }

  /**
   * This event is fired when a sensor/actuator node has finished the starting procedure and
   * has entered the "ready" state.
   *
   * @param nodeId the nodeId of the node which is ready now.
   */
  @Override
  public void onNodeReady(int nodeId) {

  }

  /**
   * This event is fired when a sensor/actuator node has stopped (shut down).
   *
   * @param nodeId The nodeId of the node which is stopped.
   */
  @Override
  public void onNodeStopped(int nodeId) {

  }

  /**
   * This event is fired when a new node is added to the greenhouse.
   *
   * @param node the added node
   */
  @Override
  public void onNodeAdded(Node node) {

  }

  /**
   * This event is fired when a node is removed from the greenhouse.
   *
   * @param nodeId ID of the node which has disappeared (removed)
   */
  @Override
  public void onNodeRemoved(int nodeId) {

  }

  /**
   * This event is fired when new sensor data is received from a node.
   *
   * @param nodeId  ID of the node
   * @param sensors List of all current sensor values
   */
  @Override
  public void onSensorData(int nodeId, List<SensorReading> sensors) {

  }

  /**
   * This event is fired when an actuator changes state.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator
   * @param isOn       When true, actuator is on; off when false.
   */
  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn) {

  }
}