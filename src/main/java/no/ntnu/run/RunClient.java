package no.ntnu.run;

import no.ntnu.gui.greenhouse.GreenhouseApplication;

public class RunClient {
  public static void main(String[] args) {
    System.out.println("Starting the greenhouse client.");
    GreenhouseApplication greenhouseApplication = new GreenhouseApplication();
    greenhouseApplication.startApp();
  }
}
