package no.ntnu.commands;

public class ActuatorAddedInGui extends Data {

  private String actuatorType;

  public ActuatorAddedInGui(String data, int nodeId, String actuatorType) {
    super(data, nodeId);
    this.actuatorType = actuatorType;
  }

  public String getActuatorType() {
    return this.actuatorType;
  }

  public int getNodeId() {
    return super.getNodeId();
  }
}
