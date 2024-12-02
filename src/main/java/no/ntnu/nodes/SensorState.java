package no.ntnu.nodes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SensorState {
  private final int nodeId;
  private double temperature;
  private double humidity;
  private final Map<String, Boolean> actuators;

  public SensorState(int nodeId) {
    this.nodeId = nodeId;
    this.temperature = 25.0; // Default temperature
    this.humidity = 50.0; // Default humidity
    this.actuators = new ConcurrentHashMap<>();
    actuators.put("heater", false);
    actuators.put("window", false);
    actuators.put("fan", false);
  }

  public synchronized void setActuatorState(String actuator, boolean state) {
    actuators.put(actuator, state);
  }

  public synchronized void updateRandomly() {
    // Simulate changes in temperature and humidity
    temperature += (Math.random() - 0.5) * 0.5;
    humidity += (Math.random() - 0.5) * 1.0;
    temperature = Math.max(15, Math.min(30, temperature));
    humidity = Math.max(30, Math.min(70, humidity));
  }

  public synchronized String toUpdateMessage() {
    return "UPDATE|nodeId=" + nodeId +
        "|temperature=" + String.format("%.2f", temperature) +
        "|humidity=" + String.format("%.2f", humidity) +
        "|heater=" + (actuators.get("heater") ? "on" : "off") +
        "|window=" + (actuators.get("window") ? "on" : "off") +
        "|fan=" + (actuators.get("fan") ? "on" : "off");
  }
}