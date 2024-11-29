package no.ntnu.nodes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SensorActuatorNode {
  private final int id;
  private double temperature;
  private double humidity;
  private final Map<String, Boolean> actuators;

  public SensorActuatorNode(int id) {
    this.id = id;
    this.actuators = new ConcurrentHashMap<>();
    actuators.put("heater", false);
    actuators.put("window", false);
    actuators.put("fan", false); // Ensure 'fan' is included
  }

  public int getId() {
    return id;
  }

  public String getFormattedTemperature() {
    return String.format("%.2f°C", temperature);
  }

  public String getFormattedHumidity() {
    return String.format("%.2f%%", humidity);
  }

  public Map<String, Boolean> getActuators() {
    return actuators;
  }

  public void setTemperature(double value) {
    temperature = value;
  }

  public void setHumidity(double value) {
    humidity = value;
  }

  public void setActuatorState(String actuator, boolean state) {
    if (actuators.containsKey(actuator)) {
      actuators.put(actuator, state);
    } else if (!"nodeId".equals(actuator)) { // Ignore "nodeId"
      System.err.println("Actuator not found: " + actuator);
    }
  }


  public boolean getActuatorState(String actuator) {
    return actuators.getOrDefault(actuator, false);
  }

  public void updateSensorValues() {
    if (actuators.get("heater")) {
      temperature += 0.1;
    } else {
      temperature -= 0.1;
    }
    if (actuators.get("window")) {
      humidity += 0.2;
    } else {
      humidity -= 0.1;
    }

    // Clamp values to realistic ranges
    temperature = Math.max(15, Math.min(35, temperature));
    humidity = Math.max(20, Math.min(80, humidity));
  }
}
