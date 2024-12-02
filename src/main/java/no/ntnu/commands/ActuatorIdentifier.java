package no.ntnu.commands;

public class ActuatorIdentifier extends Data {
  private String type;
  private int actuatorId;
  private boolean state;

  /**
   * Constructor for the Data class.
   *
   * @param data the type data to be sent.
   */
  public ActuatorIdentifier(String data, int nodeId, String type, int actuatorId, boolean state) {
    super(data, nodeId);
    this.type = type;
    this.actuatorId = actuatorId;
    this.state = state;
  }

  /**
   * Return the data type.
   *
   * @return the data type.
   */
  public String getType() {
    return this.type;
  }

  /**
   * Return the actuatorId.
   *
   * @return the actuatorId.
   */
  public int getActuatorId() {
    return this.actuatorId;
  }

  /**
   * Return the nodeId.
   *
   * @return the nodeId.
   */
  public int getNodeId() {
    return super.getNodeId();
  }

  /**
   * Return the state.
   *
   * @return the state.
   */
  public boolean getState() {
    return this.state;
  }
}
