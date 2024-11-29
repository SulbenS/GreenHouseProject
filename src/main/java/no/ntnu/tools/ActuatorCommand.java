package no.ntnu.tools;

public class ActuatorCommand extends NodeCommand {
  private int nodeId;
  private int actuatorId; // Optional (e.g., 3)
  private String actuatorType; // Optional (e.g., "Fan")
  private String action; // "On" or "Off"

  /**
   * Constructor for actuator commands with a specific actuator ID.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId Node-wide unique ID of the actuator
   * @param action     Action to perform on the actuator ("On" or "Off")
   */
  public ActuatorCommand(int nodeId, int actuatorId, String action) {
    super(nodeId, action);
    this.actuatorType = null;
    this.actuatorId = actuatorId;
  }

  /**
   * Constructor for general actuator commands.
   *
   * @param nodeId      ID of the node to which the actuator is attached
   * @param actuatorType Type of the actuator (e.g., "Fan")
   * @param action      Action to perform on the actuator ("On" or "Off")
   */
  public ActuatorCommand(int nodeId, String actuatorType, String action) {
    super(nodeId, action);
    this.actuatorType = actuatorType;
    this.actuatorId = 0;
  }

  // Getters for accessing fields
  public int getNodeId() {
    return nodeId;
  }

  public String getActuatorType() {
    return actuatorType;
  }

  public int getActuatorId() {
    return actuatorId;
  }

  public String getAction() {
    return action;
  }
}