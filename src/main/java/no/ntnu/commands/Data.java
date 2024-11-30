package no.ntnu.commands;

/**
 * Class representing the type of data being sent.
 */
public class Data {
  private String data; // Command - Reading
  private String receiver; // Node - Control-Panel

  /**
   * Constructor for the Data class.
   *
   * @param data the type data to be sent.
   */
  public Data(String data) {
    this.data = data;
  }

  /**
   * Return the data type.
   *
   * @return the data type.
   */
  public String getData() {
    return data;
  }
}