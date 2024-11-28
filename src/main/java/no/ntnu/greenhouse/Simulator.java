package no.ntnu.greenhouse;

import java.util.HashMap;
import java.util.Map;

import no.ntnu.greenhouse.tcp.Node;
import no.ntnu.greenhouse.tcp.Server;
import no.ntnu.listeners.greenhouse.NodeStateListener;

public class Simulator {
  private final Map<Integer, Node> nodes = new HashMap<>();

  public Simulator() {
  }

  public void initialize() {
    createNode(1, 2, 1, 0, 0);
    createNode(1, 0, 0, 2, 1);
    createNode(2, 0, 0, 0, 0);
  }

  private void createNode(int temperature, int humidity, int windows, int fans, int heaters) {
    Node node = DeviceFactory.createNode(
              temperature, humidity, windows, fans, heaters);
    if (!node.establishConnection()) {
      throw new IllegalStateException("Could not establish connection to the node");
    }
    nodes.put(node.getId(), node);
  }

  public void start() {
    initiateCommunication();
    for (Node node : nodes.values()) {
      node.start();
    }
  }

  private void initiateCommunication() {
    Server server = new Server();
    server.establishConnection();
  }

  public void stop() {
    stopCommunication();
    for (Node node : nodes.values()) {
      node.stop();
    }
  }

  private void stopCommunication() {
  }

  /**
   * Add a listener for notification of node staring and stopping.
   *
   * @param listener The listener which will receive notifications
   */
  public void subscribeToLifecycleUpdates(NodeStateListener listener) {
    for (Node node : nodes.values()) {
      node.addStateListener(listener);
    }
  }
}
