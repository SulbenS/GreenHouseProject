package no.ntnu.commands;

/**
 * Class representing the type of data being sent.
 */
public class SensorReadingMessage extends Data {
  private int nodeId;
  private String reading;

  /**
   * Constructor for the Message class.
   *
   * @param nodeId the ID of the node to which the message is sent.
   * @param reading the message to be sent.
   * @param data the type data to be sent.
   */
  public SensorReadingMessage(String data, int nodeId, String reading) {
    super(data);
    this.nodeId = nodeId;
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
   * Return the nodeId.
   *
   * @return the nodeId.
   */
  public int getNodeId() {
    return this.nodeId;
  }
}
