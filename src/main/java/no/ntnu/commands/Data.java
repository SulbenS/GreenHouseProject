package no.ntnu.commands;

/**
 * Class representing the type of data being sent.
 */
public class Data {
  private final String data; // Command - Reading - Identifier - Stop
  private int nodeId; // 0 if message is to be broadcast

  /**
   * Constructor for the Data class.
   *
   *
   * @param data the type data to be sent.
   */
  public Data(String data, int nodeId) {
    this.data = data;
    this.nodeId = nodeId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * Return the data type.
   *
   * @return the data type.
   */
  public String getData() {
    return this.data;
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