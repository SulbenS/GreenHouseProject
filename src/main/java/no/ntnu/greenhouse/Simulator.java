package no.ntnu.greenhouse;

import java.util.HashMap;
import java.util.Map;
import no.ntnu.greenhouse.tcp.Node;
import no.ntnu.listeners.greenhouse.NodeStateListener;

public class Simulator {
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
   * Add a listener for notification of node staring and stopping.
   *
   * @param listener The listener which will receive notifications
   */
  public void subscribeToLifecycleUpdates(NodeStateListener listener) {
    for (Node node : this.nodes.values()) {
      node.addStateListener(listener);
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
}
