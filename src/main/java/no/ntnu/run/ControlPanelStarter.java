package no.ntnu.run;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.gui.controlpanel.ControlPanelApplication;

/**
 * Starter class for the control panel.
 * Note: we could launch the Application class directly, but then we would have issues with the
 * debugger (JavaFX modules not found)
 */
public class ControlPanelStarter {

  public ControlPanelStarter() {
  }

  /**
   * Entrypoint for the application.
   */
  public static void main(String[] args) {
    ControlPanelStarter starter = new ControlPanelStarter();
    starter.start();
  }

  private void start() {
    ControlPanelLogic logic = new ControlPanelLogic();
    CommunicationChannel channel = new CommunicationChannel("localhost", 1238);
    ControlPanelApplication.startApp(logic, channel);
    // This code is reached only after the GUI-window is closed
    System.out.println("Exiting the control panel application");
  }

  private void stopCommunication() {
  }
}