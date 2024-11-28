package no.ntnu.run;

import no.ntnu.greenhouse.tcp.Server;
import no.ntnu.gui.greenhouse.GreenhouseApplication;

public class RunGreenhouseApplication {
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
