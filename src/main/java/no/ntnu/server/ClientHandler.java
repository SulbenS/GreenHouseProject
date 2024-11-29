package no.ntnu.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;

/**
 * The client handler of the application.
 */
public class ClientHandler extends Thread {
  private Server server;
  private Socket socket;

  private BufferedReader reader;
  private PrintWriter writer;

  private boolean shouldTransmit;
  private boolean shouldBroadcast;

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

    this.shouldTransmit = false;
    this.shouldBroadcast = false;
  }

  /**
   * Runs the client handler.
   */
  @Override
  public void run() {
    try {
      while (true) {
        String message = receive();
        if (message == null) {
          this.socket.close();
          return;
        }
        if (this.shouldTransmit) {
          this.transmit(message);
          this.shouldTransmit = false;
        }
        if (this.shouldBroadcast) {
          this.server.broadcast(message);
          this.shouldBroadcast = false;
        }
      }
    } catch (IOException e) {
      System.out.println("Could not read the message.");
      System.out.println(e.getMessage());
    }
  }

  /**
   * Reads the message from the client.
   *
   * @return The message from the client.
   * @throws IOException If an I/O error occurs.
   */
  public String receive() throws IOException {
    return this.reader.readLine();
  }

  /**
   * Transmits the message to the client.
   *
   * @param message The message to transmit.
   */
  public void transmit(String message) {
    this.writer.println(message);
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
   * Sets the shouldTransmit variable.
   *
   * @param shouldTransmit The value to set.
   */
  public void setShouldTransmit(boolean shouldTransmit) {
    this.shouldTransmit = shouldTransmit;
  }

  /**
   * Sets the shouldBroadcast variable.
   *
   * @param shouldBroadcast The value to set.
   */
  public void setShouldBroadcast(boolean shouldBroadcast) {
    this.shouldBroadcast = shouldBroadcast;
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

  /**
   * Return the shouldTransmit variable.
   *
   * @return The shouldTransmit variable.
   */
  public boolean getShouldTransmit() {
    return this.shouldTransmit;
  }

  /**
   * Return the shouldBroadcast variable.
   *
   * @return The shouldBroadcast variable.
   */
  public boolean getShouldBroadcast() {
    return this.shouldBroadcast;
  }
}