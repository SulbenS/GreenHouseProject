package no.ntnu.run;

import no.ntnu.gui.greenhouse.GreenhouseApplication;

/**
 * Main class for starting the greenhouse client.
 */
public class RunClient {
  /**
   * Main method for starting the greenhouse client.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    GreenhouseApplication greenhouseApplication = new GreenhouseApplication();
    greenhouseApplication.startApp();
  }
}