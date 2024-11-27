package no.ntnu.run;

import no.ntnu.greenhouse.GreenhouseServer;

public class GreenhouseServerStarter {


  public static void main(String[] args) {
    GreenhouseServer server = new GreenhouseServer();
    server.initiateRealCommunication();
  }
}