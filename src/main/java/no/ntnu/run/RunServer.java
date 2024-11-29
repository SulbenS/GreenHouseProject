package no.ntnu.run;

import no.ntnu.server.Server;

public class RunServer {
  public static void main(String[] args) {
    System.out.println("Starting the greenhouse server.");
    Server server = new Server();
    server.run();
  }
}
