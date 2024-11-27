package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import no.ntnu.greenhouse.GreenhouseServer;

public class ClientHandler extends Thread {
  private Socket socket;
  private GreenhouseServer server;
  private BufferedReader in;
  private PrintWriter out;

  public ClientHandler(Socket socket, GreenhouseServer server) throws IOException {
    this.socket = socket;
    this.server = server;
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.out = new PrintWriter(socket.getOutputStream(), true); // AutoFlush enabled
  }

  @Override
  public void run() {
    try {
      String message;
      while ((message = in.readLine()) != null) {
        System.out.println("Received: " + message);

        // Process the message (for now, just echo it back)
        out.println("Echo: " + message);
      }
    } catch (IOException e) {
      System.err.println("Client communication error: " + e.getMessage());
    } finally {
      try {
        close();
      } catch (IOException e) {
        System.err.println("Error closing client handler: " + e.getMessage());
      }
      server.removeClient(this);
    }
  }

  // Send a message to this client
  public void sendMessage(String message) {
    out.println(message);
  }

  // Close the connection
  public void close() throws IOException {
    in.close();
    out.close();
    socket.close();
  }
}