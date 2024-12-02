package no.ntnu.commands;

public class SensorAddedInGui extends Data {
  private String sensorType;

  public SensorAddedInGui(String data, int nodeId, String sensorType) {
    super(data, nodeId);
    this.sensorType = sensorType;
  }

  public String getSensorType() {
    return this.sensorType;
  }
}
