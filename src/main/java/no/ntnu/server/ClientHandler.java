package no.ntnu.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import no.ntnu.tools.ActuatorCommand;
import no.ntnu.tools.MessageSerializer;
import no.ntnu.tools.NodeCommand;

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
        NodeCommand message = receive();
        if (!(message instanceof ActuatorCommand) && message.getAction().equals("off")) {
          this.socket.close();
          return;
        }
        this.transmit(message);
      }
    } catch (IOException e) {
      System.out.println("Could not read the message.");
      System.out.println(e.getMessage());
    }
  }

  /**
   * Returns the message from the client as a command.
   *
   * @return The message from the client as a command.
   * @throws IOException If an I/O error occurs.
   */
  public NodeCommand receive() throws IOException {
    return MessageSerializer.deserialize(this.reader.readLine());
  }

  /**
   * Transmits the message to the client.
   *
   * @param message The message to transmit.
   */
  public void transmit(NodeCommand message) {
    this.writer.println(MessageSerializer.serialize(message));
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