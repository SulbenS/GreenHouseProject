package no.ntnu.server;

import no.ntnu.node.Node;
import no.ntnu.commands.Data;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The server of the application.
 */
public class Server {
  public static final int TCP_PORT = 1238; //The port of the server.
  private ServerSocket serverSocket;

  private CopyOnWriteArrayList<ClientHandler> clientHandlers;

  private NodeCollection nodes;
  private boolean isRunning;

  /**
   * Constructor for the class.
   */
  public Server() {
    this.clientHandlers = new CopyOnWriteArrayList<>();
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
    openSocket();
    System.out.println("Server started on port " + TCP_PORT);
    new Thread(() -> {
      try {
        Thread.sleep(1000);
        this.nodes.initialize();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }).start();
    while (this.isRunning) {
      ClientHandler clientHandler = connectClient();
      this.clientHandlers.add(clientHandler);
      System.out.println("Client connected: " + clientHandler.getNodeId() + "aaaaaaa");
    }
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
    ClientHandler clientHandler = null;
    try {
      if (this.isRunning) {
        clientHandler = new ClientHandler(this, this.serverSocket.accept());
        clientHandler.start();
      } else {
        throw new IllegalArgumentException("Server is not running.");
      }
    } catch (IOException e) {
      System.out.println("Could not connect a client: " + e.getMessage());
    }
    return clientHandler;
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
   * @param message the message to broadcast.
   */
  public void broadcast(Data message) {
    this.clientHandlers.forEach(client -> client.transmitToClient(message));
  }

  /**
   * Sends a message to a specific client.
   *
   * @param message the message to send.
   */
  public void sendToClient(Data message) throws IllegalArgumentException {
    getClientHandler(message.getNodeId()).transmitToClient(message);
  }

  /**
   * Return the clientHandler.
   *
   * @param nodeId the nodeId of the client.
   * @return the clientHandler.
   */
  public ClientHandler getClientHandler(int nodeId) {
    for (ClientHandler clientHandler : this.clientHandlers) {
      if (clientHandler.getNodeId() == nodeId) {
        return clientHandler;
      }
    }
    throw new IllegalArgumentException("No clientHandler found for node with ID " + nodeId);
  }


  /**
   * Return the node collection.
   *
   * @return the node collection.
   */
  public NodeCollection getNodes() {
    return this.nodes;
  }

  /**
   * Return a specific node by its ID.
   *
   * @param nodeId The ID of the node to get
   * @return The node with the given ID, or null if no such node exists
   */
  public Node getNode(int nodeId) {
    return this.nodes.getNode(nodeId);
  }
}