package no.ntnu.commands;

public class ActuatorIdentifier extends Data {
  private String type;
  private int actuatorId;

  /**
   * Constructor for the Data class.
   *
   * @param data the type data to be sent.
   */
  public ActuatorIdentifier(String data, String type, int actuatorId) {
    super(data);
    this.type = type;
    this.actuatorId = actuatorId;
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
}
