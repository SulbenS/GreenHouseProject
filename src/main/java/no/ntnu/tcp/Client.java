package no.ntnu.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The client of the application.
 */
public class Client {
  private Socket socket;

  private BufferedReader reader;
  private PrintWriter writer;

  private boolean isConnected;

  /**
   * Constructor for the class.
   *
   * @throws IOException If an I/O error occurs.
   */
  public Client() throws IOException {
    this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    this.writer = new PrintWriter(this.socket.getOutputStream(), true);
  }

  /**
   * Establishes a connection to the server.
   *
   * @throws IOException If an I/O error occurs.
   */
  public boolean establishConnection() throws IOException {
    String rawMessage;
    try {
      this.socket = new Socket("localhost", Server.TCP_PORT);
    } catch (IOException e) {
      System.out.println("Could not connect to the server.");
      System.out.println(e.getMessage());
      return false;
    }
    try {
      rawMessage = this.reader.readLine();
    } catch (IOException e) {
      System.out.println("Could not read the message.");
      System.out.println(e.getMessage());
      return false;
    }
    executeCommand(rawMessage);
    return true;
  }

  /**
   * Executes a command based on the message given.
   *
   * @param rawMessage The message given.
   */
  public void executeCommand(String rawMessage) {
    throw new IllegalArgumentException("Not implemented yet");
  }

  /**
   * Returns true if the client is connected to the server.
   *
   * @return true if the client is connected to the server.
   */
  public boolean getIsConnected() {
    return this.isConnected;
  }
}
