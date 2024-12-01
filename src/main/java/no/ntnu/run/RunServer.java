package no.ntnu.run;

import no.ntnu.server.Server;

/**
 * Main class for starting the greenhouse server.
 */
public class RunServer {

  /**
   * Main method for starting the greenhouse server.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    System.out.println("Starting the greenhouse server.");
    Server server = new Server();
    server.run();
  }
}