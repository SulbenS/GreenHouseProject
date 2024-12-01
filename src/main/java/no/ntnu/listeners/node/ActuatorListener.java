package no.ntnu.listeners.node;

import no.ntnu.node.Actuator;

/**
 * Listener for actuator state changes.
 * This could be used both on the sensor/actuator (greenhouse) side, as wall as
 * on the control panel side.
 */
public interface ActuatorListener {
  /**
   * An event that is fired every time an actuator changes state.
   *
   * @param nodeId   ID of the node on which this actuator is placed
   * @param actuatorId The ID of the actuator that has changed its state
   * @param newState The new state of the actuator
   */
  void onActuatorStateChanged(int nodeId, int actuatorId, boolean newState);
}