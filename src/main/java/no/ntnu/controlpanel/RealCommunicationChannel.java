package no.ntnu.controlpanel;

import no.ntnu.tools.Logger;

/**
 * A communication channel for disseminating control commands to the sensor nodes
 * (sending commands to the server) and receiving notifications about events.
 */
public class RealCommunicationChannel implements CommunicationChannel {
  private final ControlPanelLogic logic;

  /**
   * Create a new real communication channel.
   *
   * @param logic The application logic of the control panel node.
   */
  public RealCommunicationChannel(ControlPanelLogic logic) {
    this.logic = logic;
  }

  /**
   * Request that state of an actuator is changed.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId Node-wide unique ID of the actuator
   * @param isOn       When true, actuator must be turned on; off when false.
   */
  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    String state = isOn ? "ON" : "OFF";
    Logger.info("Sending command to greenhouse: turn " + state + " actuator"
            + "[" + actuatorId + "] on node " + nodeId);
  }

  /**
   * Open the communication channel.
   *
   * @return true when the communication channel is successfully opened, false on error
   */
  @Override
  public boolean open() {
    return false;
  }
}