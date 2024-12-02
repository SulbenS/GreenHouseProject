package no.ntnu.node;

import java.util.HashMap;
import java.util.Map;
import no.ntnu.listeners.node.ActuatorListener;

/**
 * An actuator that can change the environment in a way. The actuator will make impact on the
 * sensors attached to this same node.
 */
public class Actuator {
  private static int nextId = 1;
  private final int nodeId;
  private Map<String, Double> impacts = new HashMap<>();

  private final String type;
  private final int id;
  private boolean on;

  /**
   * Create an actuator. An ID will be auto-generated.
   *
   * @param type   The type of the actuator.
   * @param nodeId ID of the node to which this actuator is connected.
   */
  public Actuator(String type, int nodeId) {
    this.type = type;
    this.nodeId = nodeId;
    this.on = false;
    this.id = generateUniqueId();
  }

  /**
   * Create an actuator.
   *
   * @param id     The desired ID of the node.
   * @param type   The type of the actuator.
   * @param nodeId ID of the node to which this actuator is connected.
   */
  public Actuator(int id, String type, int nodeId) {
    this.type = type;
    this.nodeId = nodeId;
    this.on = false;
    this.id = id;
  }

  private static int generateUniqueId() {
    return nextId++;
  }

  /**
   * Register the impact of this actuator when active.
   *
   * @param sensorType     Which type of sensor readings will be impacted. Example: "temperature"
   * @param diffWhenActive What will be the introduced difference in the sensor reading when
   *                       the actuator is active. For example, if this value is 2.0 and the
   *                       sensorType is "temperature", this means that "activating this actuator
   *                       will increase the readings of temperature sensors attached to the
   *                       same node by +2 degrees".
   */
  public void setImpact(String sensorType, double diffWhenActive) {
    impacts.put(sensorType, diffWhenActive);
  }

  public String getType() {
    return type;
  }

  /**
   * Create a clone of this actuator.
   *
   * @return A clone of this actuator, where all the fields are the same
   */
  public Actuator createClone() {
    Actuator a = new Actuator(type, nodeId);
    // Note - we pass a reference to the same map! This should not be problem, as long as we
    // don't modify the impacts AFTER creating the template
    a.impacts = impacts;
    return a;
  }

  /**
   * Toggle the actuator - if it was off, not it will be ON, and vice versa.
   */
  public void toggle() {
    this.on = !this.on;
  }
  /**
   * Check whether the actuator is active (ON), or inactive (OFF).
   *
   * @return True when the actuator is ON, false if it is OFF
   */

  public boolean isOn() {
    return on;
  }

  /**
   * Apply impact of this actuator to all sensors of one specific sensor node.
   *
   * @param node The sensor node to be affected by this actuator.
   */
  public void applyImpact(Node node) {
    for (Map.Entry<String, Double> impactEntry : impacts.entrySet()) {
      String sensorType = impactEntry.getKey();
      double impact = impactEntry.getValue();
      if (!on) {
        impact = -impact;
      }
      node.applyActuatorImpact(sensorType, impact);
    }
  }

  @Override
  public String toString() {
    return "Actuator{"
        + "type='" + type + '\''
        + ", on=" + on
        + '}';
  }

  /**
   * Get the ID of the actuator.
   *
   * @return An ID which is guaranteed to be unique at a node level, not necessarily unique at
   *     the whole greenhouse-network level.
   */
  public int getId() {
    return id;
  }

  public int getNodeId() {
    return nodeId;
  }

  /**
   * Set the actuator to the desired state.
   *
   * @param on Turn on when true, turn off when false
   */
  public void setState(boolean on) {
    this.on = on;
  }
}
