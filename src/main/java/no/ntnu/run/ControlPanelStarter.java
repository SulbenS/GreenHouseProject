package no.ntnu.run;

import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.RealCommunicationChannel;
import no.ntnu.gui.controlpanel.ControlPanelApplication;
import no.ntnu.tools.Logger;

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
    CommunicationChannel channel = initiateCommunication(logic);
    ControlPanelApplication.startApp(logic, channel);
    // This code is reached only after the GUI-window is closed
    Logger.info("Exiting the control panel application");
    stopCommunication();
  }

  private CommunicationChannel initiateCommunication(ControlPanelLogic logic) {
    // TODO - here you initiate TCP/UDP socket communication
    RealCommunicationChannel channel = new RealCommunicationChannel(logic);
    logic.setCommunicationChannel(channel);
    return channel;
  }

  private void stopCommunication() {
    // TODO - here you stop the TCP/UDP socket communication
  }
}