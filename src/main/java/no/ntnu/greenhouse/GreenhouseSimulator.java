package no.ntnu.greenhouse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import no.ntnu.greenhouse.tcp.Node;
import no.ntnu.greenhouse.tcp.Server;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {
  private final Map<Integer, Node> nodes = new HashMap<>();

  /**
   * Create a greenhouse simulator.
   */
  public GreenhouseSimulator() {
  }

  /**
   * Initialise the greenhouse but don't start the simulation just yet.
   */
  public void initialize() {
    createNode(1, 2, 1, 0, 0);
    createNode(1, 0, 0, 2, 1);
    createNode(2, 0, 0, 0, 0);
    Logger.info("Greenhouse initialized");
  }

  private void createNode(int temperature, int humidity, int windows, int fans, int heaters) {
    Node node = DeviceFactory.createNode(
              temperature, humidity, windows, fans, heaters);
    if (!node.establishConnection()) {
      throw new IllegalStateException("Could not establish connection to the node");
    }
    nodes.put(node.getId(), node);
  }

  /**
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside it.
   */
  public void start() {
    initiateCommunication();
    for (Node node : nodes.values()) {
      node.start();
    }

    Logger.info("Simulator started");
  }

  private void initiateCommunication() {
    Server server = new Server();
    server.establishConnection();
  }

  /**
   * Stop the simulation of the greenhouse - all the nodes in it.
   */
  public void stop() {
    stopCommunication();
    for (Node node : nodes.values()) {
      node.stop();
    }
  }

  private void stopCommunication() {
    // TODO - here you stop the TCP/UDP communication
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
