package no.ntnu.commands;

/**
 * Class representing the type of data being sent.
 */
public class NodeIdentifier extends Data {

  /**
   * Constructor for the NodeIdentifier class.
   * Does not contain any more than the superclass, Data.
   *
   * @param data the type data to be sent.
   * @param nodeId the node ID. If -1, the message is from the control panel.
   */
  public NodeIdentifier(String data, int nodeId) {
    super(data, nodeId);
  }
}
