package no.ntnu.greenhouse.node;

import no.ntnu.greenhouse.DeviceFactory;
import no.ntnu.listeners.node.NodeStateListener;

import java.util.HashMap;
import java.util.Map;

public class NodeCollection implements NodeStateListener {
  private final Map<Integer, Node> nodes = new HashMap<>();

  public void initialize() {
    createNode(1, 2, 1, 0, 0);
    createNode(1, 0, 0, 2, 1);
    createNode(2, 0, 0, 0, 0);
    System.out.println("Added nodes to the simulator");
  }

  private void createNode(int temperature, int humidity, int windows, int fans, int heaters) {
    Node node = DeviceFactory.createNode(
            temperature, humidity, windows, fans, heaters);
    node.addStateListener(this);
    if (!node.establishConnection()) {
      throw new IllegalStateException("Could not establish connection to the node");
    }
    this.nodes.put(node.getId(), node);
  }

  public void start() {
    for (Node node : this.nodes.values()) {
      node.start();
    }
  }

  public void stop() {
    for (Node node : this.nodes.values()) {
      node.stop();
    }
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
}