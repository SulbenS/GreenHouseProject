package no.ntnu.commands;

/**
 * Class representing the type of data being sent.
 */
public class NodeCommand extends Data {
  private int nodeId;
  private String action; // "On" or "Off"

  /**
   * Constructor for actuator commands with a specific actuator ID.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param action     Action to perform on the actuator ("On" or "Off")
   */
  public NodeCommand(String data, int nodeId, String action) {
    super(data);
    this.action = action;
    this.nodeId = nodeId;
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
