package no.ntnu.greenhouse.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * The server of the application.
 */
public class Server {
  public static final int TCP_PORT = 1238; //The port of the server.

  private boolean isRunning;

  private ServerSocket serverSocket;
  private List<ClientHandler> clients;

  public static void main(String[] args) {
    Server server = new Server();
    server.run();
  }

  /**
   * Constructor for the class.
   */
  public Server() {
    this.clients = new ArrayList<>();
    this.isRunning = false;
  }

  /**
   * Runs the server.
   * The server will establish a connection, and then wait for clients to connect.
   * When a client connects, the server will create a new client handler for the client.
   * This client will be run in a new thread.
   */
  public void run() {
    establishConnection();
    while (this.isRunning) {
      ClientHandler client = connectClient();
      client.start();
      this.clients.add(client);
    }
  }

  /**
   * Creates a port, and waits for a client to connect.
   */
  public void establishConnection() {
    try {
      this.serverSocket = new ServerSocket(TCP_PORT);
      this.isRunning = true;
    } catch (IOException e) {
      System.out.println("Could not start the server.");
      System.out.println(e.getMessage());
    }
  }

  /**
   * Connects a client to the server.
   */
  public ClientHandler connectClient() {
    if (this.isRunning) {
      try {
        ClientHandler client = new ClientHandler(this, this.serverSocket.accept());
        client.start();
        this.clients.add(client);
        return client;
      } catch (IOException e) {
        System.out.println("Could not connect to the client.");
        System.out.println(e.getMessage());
      }
    }
    return null;
  }

  /**
   * Disconnects a client from the server.
   *
   * @param client The client to disconnect.
   */
  public void disconnectClient(ClientHandler client) {
    if (this.isRunning) {
      this.clients.remove(client);
    }
  }

  /**
   * Broadcasts a message to all clients.
   *
   * @param message The message to broadcast.
   */
  public void broadcast(String message) {
    this.clients.forEach(client -> client.transmit(message));
  }
}