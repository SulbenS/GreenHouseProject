package no.ntnu.tools;

public class NodeCommand {
  private int nodeId;
  private String action; // "On" or "Off"

  /**
   * Constructor for actuator commands with a specific actuator ID.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param action     Action to perform on the actuator ("On" or "Off")
   */
  public NodeCommand(int nodeId, String action) {
    this.nodeId = nodeId;
    this.action = action;
  }

  /**
   * Constructor for general actuator commands.
   *
   * @param nodeId      ID of the node to which the actuator is attached
   * @param actuatorType Type of the actuator (e.g., "Fan")
   * @param action      Action to perform on the actuator ("On" or "Off")
   */
  public NodeCommand(int nodeId, String actuatorType, String action) {
    this.nodeId = nodeId;
    this.action = action;
  }

  /**
   * Return the nodeId.
   *
   * @return The nodeId
   */
  public int getNodeId() {
    return nodeId;
  }

  /**
   * Return the action.
   *
   * @return The action
   */
  public String getAction() {
    return action;
  }
}
