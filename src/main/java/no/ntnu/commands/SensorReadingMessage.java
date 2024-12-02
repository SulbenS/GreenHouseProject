package no.ntnu.commands;

/**
 * Class representing the type of data being sent.
 */
public class SensorReadingMessage extends Data {
  private int sensorId;
  private String type;
  private String value;
  private String unit;

  /**
   * Constructor for the Message class.
   *
   * @param nodeId the ID of the node to which the message is sent.
   * @param data the type data to be sent.
   */
  public SensorReadingMessage(
          String data, int nodeId, int sensorId, String type, String value, String unit) {
    super(data, nodeId);
    this.sensorId = sensorId;
    this.type = type;
    this.value = value;
    this.unit = unit;
  }

  /**
   * Return the reading.
   *
   * @return the reading.
   */
  public String getReading() {
    return "Data=Reading"
            + ";Node=" + getNodeId()
            + ";Sensor=" + this.sensorId
            + ";Type=" + this.type
            + ";Value=" + this.value
            + ";Unit=" + this.unit;
  }

  /**
   * Return the sensorId.
   *
   * @return the sensorId.
   */
  public int getSensorId() {
    return this.sensorId;
  }

  /**
   * Return the value.
   *
   * @return the value.
   */
  public String getValue() {
    return this.value;
  }

  /**
   * Return the unit.
   *
   * @return the unit.
   */
  public String getUnit() {
    return this.unit;
  }

  /**
   * Return the type.
   *
   * @return the type.
   */
  public String getType() {
    return this.type;
  }
}
