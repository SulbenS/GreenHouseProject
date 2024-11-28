package no.ntnu.controlpanel;

import java.io.*;
import java.net.*;

/**
 * A communication channel for disseminating control commands to the sensor nodes
 * (sending commands to the server) and receiving notifications about events.
 */
public class CommunicationChannel {
  private ControlPanelLogic logic;

  /**
   * Create a new real communication channel.
   *
   * @param logic The application logic of the control panel node.
   */
  public CommunicationChannel(ControlPanelLogic logic) {
    this.logic = logic;
  }

  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;

  // Constructor for client-side communication
  public CommunicationChannel(String host, int port) throws IOException {
    this.socket = new Socket(host, port);
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.out = new PrintWriter(socket.getOutputStream(), true);
  }

  // Constructor for server-side communication (pass the accepted socket)
  public CommunicationChannel(Socket socket) throws IOException {
    this.socket = socket;
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.out = new PrintWriter(socket.getOutputStream(), true);
  }

  // Send a message
  public void sendMessage(String message) {
    out.println(message);
  }

  // Receive a message
  public String receiveMessage() throws IOException {
    return in.readLine();
  }

  // Close the channel
  public void close() throws IOException {
    in.close();
    out.close();
    socket.close();
  }

  /**
   * Request that state of an actuator is changed.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId Node-wide unique ID of the actuator
   * @param isOn       When true, actuator must be turned on; off when false.
   */
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    String state = isOn ? "ON" : "OFF";
    System.out.println("Sending command to greenhouse: turn " + state + " actuator"
            + "[" + actuatorId + "] on node " + nodeId);
  }

  /**
   * Open the communication channel.
   *
   * @return true when the communication channel is successfully opened, false on error
   */
  public boolean open() {
    return false;
  }
}