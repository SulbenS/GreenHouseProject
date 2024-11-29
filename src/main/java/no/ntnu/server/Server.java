package no.ntnu.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The server of the application.
 */
public class Server {
  public static final int TCP_PORT = 1238; //The port of the server.
  private ServerSocket serverSocket;

  private List<ClientHandler> clientHandlers;

  private NodeCollection nodes;
  private boolean isRunning;

  /**
   * Constructor for the class.
   */
  public Server() {
    this.clientHandlers = new ArrayList<>();
    this.nodes = new NodeCollection();
    this.isRunning = false;
  }

  /**
   * Runs the server.
   * The server will establish a connection, and then wait for clients to connect.
   * When a client connects, the server will create a new client handler for the client.
   * This client will be run in a new thread.
   */
  public void run() {
    openSocket(); // Start the server socket
    System.out.println("Server started on port " + TCP_PORT);
    this.isRunning = true; // Mark the server as running

    while (this.isRunning) {
      try {
        // Accept a new client connection
        Socket clientSocket = this.serverSocket.accept();
        System.out.println("New client connected: " + clientSocket.getInetAddress());

        // Create a client handler for the connected client
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);

        // Add the client handler to the list of active handlers
        synchronized (this.clientHandlers) {
          this.clientHandlers.add(clientHandler);
        }

        // Start the client handler in a new thread
        new Thread(clientHandler).start();
      } catch (IOException e) {
        if (this.isRunning) {
          System.out.println("Error accepting a new client: " + e.getMessage());
        }
      }
    }
    // TODO: Add a way to stop the server
  }


  /**
   * Creates a port.
   */
  public void openSocket() {
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
   *
   * @return The client handler for the client.
   */
  public ClientHandler connectClient() {
    if (this.isRunning) {
      try {
        ClientHandler clientHandler = new ClientHandler(this, this.serverSocket.accept());
        clientHandler.start();
        return clientHandler;
      } catch (IOException e) {
        System.out.println("Could not connect a client: " + e.getMessage());
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
      this.clientHandlers.remove(client);
    }
  }

  /**
   * Broadcasts a message to all clients.
   *
   * @param message The message to broadcast.
   */
  public void broadcast(String message) {
    this.clientHandlers.forEach(client -> client.transmit(message));
  }
}