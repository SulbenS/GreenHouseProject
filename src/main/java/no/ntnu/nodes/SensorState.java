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
    if (actuators.containsKey(actuator)) {
      actuators.put(actuator, state);
    } else {
      System.err.println("Actuator not found: " + actuator);
    }
  }

  public Map<String, Boolean> getActuators() {
    return actuators;
  }

  public synchronized void updateRandomly() {
    // Simulate temperature and humidity changes
    if (actuators.get("heater")) {
      temperature += 0.12; // Heaters gradually increase temperature
    } else if (actuators.get("window")) {
      temperature -= 0.11; // Open windows decrease temperature
      humidity += 0.25; // Open windows increase humidity
    }

    if (actuators.get("fan")) {
      humidity -= 0.15; // Fans decrease humidity
    }

    // Add minor random fluctuations
    temperature += (Math.random() - 0.5) * 0.1;
    humidity += (Math.random() - 0.5) * 0.2;

    // Clamp values to realistic ranges
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
