package no.ntnu.controlpanel;
import java.io.*;
import java.net.*;

public class RealCommunicationChannel implements CommunicationChannel {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Constructor for client-side communication
    public RealCommunicationChannel(String host, int port) throws IOException {
      this.socket = new Socket(host, port);
      this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    // Constructor for server-side communication (pass the accepted socket)
    public RealCommunicationChannel(Socket socket) throws IOException {
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
  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {

  }

  /**
   * Open the communication channel.
   *
   * @return True when the communication channel is successfully opened, false on error
   */
  @Override
  public boolean open() {
    return false;
  }
}

