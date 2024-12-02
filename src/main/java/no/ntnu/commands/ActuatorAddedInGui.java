package no.ntnu.commands;

/**
 * Class for the actuator added in the GUI command.
 */
public class ActuatorAddedInGui extends Data {

  private String actuatorType;

  public ActuatorAddedInGui(String data, int nodeId, String actuatorType) {
    super(data, nodeId);
    this.actuatorType = actuatorType;
  }

  public String getActuatorType() {
    return this.actuatorType;
  }
}
