package no.ntnu.commands;

/**
 * Class representing an ActuatorCommand.
 */
public class ActuatorCommand extends NodeCommand {
  private int actuatorId; // Optional (e.g., 3)
  private String actuatorType; // Optional (e.g., "Fan")
  private String action; // "On" or "Off"

  /**
   * Constructor for actuator commands with a specific actuator ID.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId Node-wide unique ID of the actuator
   * @param action     Action to perform on the actuator ("On" or "Off")
    * @param data      The data to be sent
   */
  public ActuatorCommand(String data, int nodeId, int actuatorId, String action) {
    super(data, nodeId, action);
    this.actuatorType = null;
    this.actuatorId = actuatorId;
    this.action = action;
  }

  /**
   * Constructor for general actuator commands.
   *
   * @param nodeId      ID of the node to which the actuator is attached
   * @param actuatorType Type of the actuator (e.g., "Fan")
   * @param action      Action to perform on the actuator ("On" or "Off")
   * @param data        The data to be sent
   */
  public ActuatorCommand(String data, int nodeId, String actuatorType, String action) {
    super(data, nodeId, action);
    this.actuatorType = actuatorType;
    this.actuatorId = 0;
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