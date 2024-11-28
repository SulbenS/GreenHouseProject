package no.ntnu.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A communication channel for disseminating control commands to the sensor nodes
 * (sending commands to the server) and receiving notifications about events.
 */
public class CommunicationChannel {
  private Socket socket;

  private BufferedReader reader;
  private PrintWriter writer;

  /**
   * Constructor for client-side communication (pass the host and port)
   *
   * @param host The host to connect to
   * @param port The port to connect to
   */
  public CommunicationChannel(String host, int port) {
    try {
      this.socket = new Socket(host, port);
      this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      this.writer = new PrintWriter(this.socket.getOutputStream(), true);
    } catch (IOException e) {
      System.out.println("Could not create the communication channel");
      System.out.println(e.getMessage());
    }
  }

  /**
   * Constructor for server-side communication (pass the socket)
   *
   * @param socket The socket to communicate over
   */
  public CommunicationChannel(Socket socket)  {
    try {
      this.socket = socket;
      this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.writer = new PrintWriter(socket.getOutputStream(), true);
    } catch (IOException e) {
      System.out.println("Could not create the communication channel");
      System.out.println(e.getMessage());
    }
  }

  /**
   * Send a message
   *
   * @param message The message to send
   */
  public void sendMessage(String message) {
    this.writer.println(message);
  }

  /**
   * Receive a message
   *
   * @return The message received
   * @throws IOException If an I/O error occurs when reading the message
   */
  public String receiveMessage() throws IOException {
    return this.reader.readLine();
  }

  /**
   * Close the reader, writer, and socket.
   *
   * @throws IOException If an I/O error occurs when closing the channel
   */
  public void closeAll() throws IOException {
    this.socket.close();
    this.reader.close();
    this.writer.close();
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
    throw new UnsupportedOperationException("Not implemented yet");
  }
}