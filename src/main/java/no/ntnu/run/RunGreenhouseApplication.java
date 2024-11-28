package no.ntnu.run;

import no.ntnu.greenhouse.tcp.Server;
import no.ntnu.gui.greenhouse.GreenhouseApplication;

public class RunGreenhouseApplication {
  public static void main(String[] args) {
    Server server = new Server();
    server.run();
    GreenhouseApplication greenhouseApplication = new GreenhouseApplication();
    greenhouseApplication.startApp();
  }
}
