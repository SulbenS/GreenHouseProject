package no.ntnu.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import no.ntnu.commands.ActuatorCommand;
import no.ntnu.commands.Data;
import no.ntnu.tools.MessageSerializer;
import no.ntnu.commands.NodeCommand;
import no.ntnu.commands.SensorReadingMessage;

/**
 * The client handler of the application.
 */
public class ClientHandler extends Thread {
  private Server server;
  private Socket socket;

  private BufferedReader reader;
  private PrintWriter writer;

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
        if (message instanceof SensorReadingMessage) {
          sendDataToServer(message);
        } else if (message instanceof ActuatorCommand) {
          sendActuatorCommandToServer(message);
        } else if (message instanceof NodeCommand) {
          sendNodeCommand(message);
        }
      }
    } catch (IOException e) {
      System.out.println("Could not read the message.");
      System.out.println(e.getMessage());
    }
  }

  private void sendDataToServer(Data message) {
    this.server.broadcast(message);
  }

  private void sendActuatorCommandToServer(Data message) {
    throw new UnsupportedOperationException("Not implemented yet.");
    // TODO: Implement this method
  }

  private void sendNodeCommand(Data message) {
    throw new UnsupportedOperationException("Not implemented yet.");
    // TODO: Implement this method
  }

  /**
   * Returns the message from the client as a command.
   *
   * @return The message from the client as a command.
   * @throws IOException If an I/O error occurs.
   */
  public Data receive() throws IOException {
    return MessageSerializer.getDataType(this.reader.readLine());
  }

  /**
   * Transmits the message to the client.
   *
   * @param message The message to transmit.
   */
  public void transmitToClient(Data message) {
    if (message instanceof SensorReadingMessage) {
      this.writer.println(((SensorReadingMessage) message).getReading());
    } else if (message instanceof ActuatorCommand) {
      this.writer.println(MessageSerializer.serializeActuatorCommand((ActuatorCommand) message));
    } else if (message instanceof NodeCommand) {
      this.writer.println(MessageSerializer.serializeNodeCommand((NodeCommand) message));
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


  public Server getServer() {
    return this.server;
  }

  public Socket getSocket() {
    return this.socket;
  }
}