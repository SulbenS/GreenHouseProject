package no.ntnu;

public class Command {
  private int nodeId;
  private String actuatorType; // Optional (e.g., "Fan")
  private int actuatorId; // Optional (e.g., 3)
  private String action; // "On" or "Off"

  // Constructor for specific actuator commands
  public Command(int nodeId, int actuatorId, String action) {
    this.nodeId = nodeId;
    this.actuatorType = null;
    this.actuatorId = actuatorId;
    this.action = action;
  }

  // Constructor for actuator type commands
  public Command(int nodeId, String actuatorType, String action) {
    this.nodeId = nodeId;
    this.actuatorType = actuatorType;
    this.actuatorId = 0;
    this.action = action;
  }

  // Getters for accessing fields
  public int getNodeId() {
    return nodeId;
  }

  public String getActuatorType() {
    return actuatorType;
  }

  public int getActuatorId() {
    return actuatorId;
  }

  public String getAction() {
    return action;
  }
}