package no.ntnu.commands;

public class NodeIdentifier extends Data {

  /**
   * Constructor for the NodeIdentifier class.
   * Does not contain any more than the superclass, Data.
   *
   * @param data the type data to be sent.
   * @param nodeId the node ID.
   */

  public NodeIdentifier(String data, int nodeId) {
    super(data, nodeId);
  }
}
