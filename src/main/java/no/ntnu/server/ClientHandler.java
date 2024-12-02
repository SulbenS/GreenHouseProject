package no.ntnu.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import no.ntnu.commands.*;
import no.ntnu.tools.MessageHandler;

/**
 * The client handler of the application.
 */
public class ClientHandler extends Thread {
  private Server server;
  private Socket socket;

  private BufferedReader reader;
  private PrintWriter writer;

  private boolean hasNodeTab;
  private int nodeId;

  /**
   * Constructor for the class.
   *
   * @param server The server of the application.
   * @param socket The socket of the application.
   * @throws IOException If an I/O error occurs.
   */
  public ClientHandler(Server server, Socket socket) throws IOException {
    this.server = server;
    this.socket = socket;
    this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.writer = new PrintWriter(socket.getOutputStream(), true);
    this.hasNodeTab = false;
  }

  /**
   * Runs the client handler.
   */
  @Override
  public void run() {
    try {
      while (true) {
        Data message = receive();
        if (message instanceof NodeCommand nodeCommand && !(message instanceof ActuatorCommand)) {
          if (nodeCommand.getAction().equals("off")) {
            this.socket.close();
            return;
          }
        }
        if (message instanceof SensorReadingMessage
                || message instanceof SensorIdentifier
                || message instanceof ActuatorIdentifier) {
          sendDataToServer(message);
        } else if (message instanceof NodeCommand) {
          sendCommandToServer(message);
        } else if (message instanceof ActuatorAddedInGui) {
          sendCommandToServer(message);
        } else if (message instanceof NodeIdentifier && message.getNodeId() == -1) {
          requestNodeInformation();
        } else if (message.getData().equals("Stop")) {
          this.server.stop();
        }
      }
    } catch (IOException e) {
      System.out.println("Could not read the message.");
      System.out.println(e.getMessage());
    }
  }

  private void requestNodeInformation() {
    this.server.sendNodeInformation();
  }

  private void sendDataToServer(Data message) {
    this.server.broadcast(message);
  }

  private void sendCommandToServer(Data message) {
    this.server.sendToClient(message);
  }

  /**
   * Returns the message from the client as a command.
   *
   * @return The message from the client as a command.
   * @throws IOException If an I/O error occurs.
   */
  public Data receive() throws IOException {
    String message = this.reader.readLine();
    if (message == null) {
      throw new IllegalArgumentException("Could not read the message.");
    }
    Data data =  MessageHandler.getData(message);
    if (data instanceof NodeIdentifier) {
      this.nodeId = data.getNodeId();
    }
    return data;
  }

  /**
   * Transmits the message to the client.
   *
   * @param message The message to transmit.
   */
  public void transmitToClient(Data message) {
    if (message instanceof SensorReadingMessage) {
      if (this.hasNodeTab) {
        this.writer.println(((SensorReadingMessage) message).getReading());
      }
    } else if (message instanceof ActuatorCommand actuatorCommand) {
      System.out.println("Sending actuator command: "
              + "Data=" + actuatorCommand.getData()
              + ";NodeId=" + actuatorCommand.getNodeId()
              + ";ActuatorId=" + actuatorCommand.getActuatorId()
              + ";Action=" + actuatorCommand.getAction()
      );
      this.writer.println(MessageHandler.serializeActuatorCommand(actuatorCommand));
    } else if (message instanceof NodeCommand nodeCommand) {
      System.out.println("Sending node command: "
              + "Data=" + nodeCommand.getData()
              + "NodeId=" + nodeCommand.getNodeId()
              + "Action=" + nodeCommand.getAction()
      );
      this.writer.println(MessageHandler.serializeNodeCommand(nodeCommand));
    } else if (message instanceof SensorIdentifier) {
      this.writer.println(MessageHandler.serializeSensorInformation((SensorIdentifier) message));
      this.hasNodeTab = true;
    } else if (message instanceof ActuatorIdentifier) {
      this.writer.println(MessageHandler.serializeActuatorInformation((ActuatorIdentifier) message));
      this.hasNodeTab = true;
    } else if (message instanceof ActuatorAddedInGui) {
      this.writer.println(MessageHandler.serializeActuatorAddedInGui((ActuatorAddedInGui) message));
    } else if (message instanceof SensorAddedInGui) {
      this.writer.println(MessageHandler.serializeSensorAddedInGui((SensorAddedInGui) message));
    } else {
      throw new IllegalArgumentException("Could not transmit the message.");
    }
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

  public void setServer(Server server) {
    this.server = server;
  }

  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  public Server getServer() {
    return this.server;
  }

  public Socket getSocket() {
    return this.socket;
  }

  public int getNodeId() {
    return this.nodeId;
  }
}