package no.ntnu.run;

import no.ntnu.gui.greenhouse.GreenhouseApplication;
import no.ntnu.server.Server;

/**
 * Main class for starting the greenhouse application.
 */
public class RunGreenhouseApplication {

  /**
   * Main method for starting the greenhouse application.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    //Server runs on a separate thread.
    new Thread(() -> {
      Server server = new Server();
      server.run();
    }).start();
    System.out.println("Starting the greenhouse application...");
    GreenhouseApplication greenhouseApplication = new GreenhouseApplication();
    greenhouseApplication.startApp();
  }
}