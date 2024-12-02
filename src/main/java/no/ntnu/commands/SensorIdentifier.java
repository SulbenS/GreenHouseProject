package no.ntnu.commands;

/**
 * Class representing the type of data being sent.
 */
public class SensorIdentifier extends Data {
  private String type;
  private int SensorId;

  /**
   * Constructor for the Data class.
   *
   * @param data the type data to be sent.
   */
  public SensorIdentifier(String data, int nodeId, String type, int SensorId) {
    super(data, nodeId);
    this.type = type;
    this.SensorId = SensorId;
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
  public int getSensorId() {
    return this.SensorId;
  }

  /**
   * Return the nodeId.
   *
   * @return the nodeId.
   */
  public int getNodeId() {
    return super.getNodeId();
  }
}