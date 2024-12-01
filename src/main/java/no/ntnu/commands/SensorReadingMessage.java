package no.ntnu.commands;

/**
 * Class representing the type of data being sent.
 */
public class SensorReadingMessage extends Data {
  private String reading;
  private int sensorId;

  /**
   * Constructor for the Message class.
   *
   * @param nodeId the ID of the node to which the message is sent.
   * @param reading the message to be sent.
   * @param data the type data to be sent.
   */
  public SensorReadingMessage(String data, int nodeId, int sensorId, String reading) {
    super(data, nodeId);
    this.sensorId = sensorId;
    this.reading = reading;
  }

  /**
   * Return the reading.
   *
   * @return the reading.
   */
  public String getReading() {
    return this.reading;
  }

  /**
   * Return the sensorId.
   *
   * @return the sensorId.
   */
  public int getSensorId() {
    return this.sensorId;
  }
}
