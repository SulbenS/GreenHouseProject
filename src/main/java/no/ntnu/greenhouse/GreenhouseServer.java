package no.ntnu.greenhouse;

import java.io.*;
import java.net.*;
import java.util.*;

public class GreenhouseServer {
  public static final int PORT_NUMBER = 1238;
  boolean isTcpServerRunning;
  private ServerSocket serverSocket;
  private List<ClientHandler> clientHandlers; // Track connected clients

  public GreenhouseServer() {
      this.clientHandlers = new ArrayList<>(); // Initialize the list
      isTcpServerRunning = true;
      System.out.println("Greenhouse Server initialized on port " + PORT_NUMBER);
      GreenhouseSimulator greenhouseSimulator = new GreenhouseSimulator();
  }

  // Starts the server and listens for connections
  public void initiateRealCommunication() {
    ServerSocket listeningSocket = openListeningSocket();
    System.out.println("Server listening on port " + PORT_NUMBER);

    if (listeningSocket != null) {
      isTcpServerRunning = true;

      while (isTcpServerRunning) {
        ClientHandler clientHandler = acceptNextClientConnection(listeningSocket);

        if (clientHandler != null) {
          clientHandlers.add(clientHandler); // Track the connected client
          clientHandler.start(); // Start the client handler thread
        }
      }
    }
  }

  private ServerSocket openListeningSocket() {
    ServerSocket listeningSocket = null;

    try {
      listeningSocket = new ServerSocket(PORT_NUMBER);
    } catch (IOException e) {
      System.err.println("Could not open server socket: " + e.getMessage());
    }

    return listeningSocket;
  }


  private ClientHandler acceptNextClientConnection(ServerSocket listeningSocket) {
    ClientHandler clientHandler = null;

    try {
      Socket clientSocket = listeningSocket.accept(); // Wait for a client to connect
      System.out.println("New client connected from " + clientSocket.getRemoteSocketAddress());

      clientHandler = new ClientHandler(clientSocket, this); // Create a handler for the client
    } catch (IOException e) {
      System.err.println("Could not accept client connection: " + e.getMessage());
    }

    return clientHandler;
  }


  // Broadcast a message to all connected clients
  public void broadcastMessage(String msg) {
    for (ClientHandler handler : clientHandlers) {
      handler.sendMessage(msg);
    }
  }

  // Remove a disconnected client
  public void removeClient(ClientHandler handler) {
    clientHandlers.remove(handler);
  }

  // Gracefully stop the server
  public void stopServer() {
    try {
      for (ClientHandler handler : clientHandlers) {
        handler.close(); // Close each client connection
      }
      clientHandlers.clear(); // Clear the list of handlers
      serverSocket.close(); // Close the server socket
    } catch (IOException e) {
      System.err.println("Error stopping server: " + e.getMessage());
    }
  }
}