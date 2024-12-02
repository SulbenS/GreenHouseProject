package no.ntnu.gui;

import no.ntnu.client.Client;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import no.ntnu.nodes.SensorActuatorNode;

import java.util.HashMap;
import java.util.Map;

public class NodeGuiWindow extends VBox {
  private final SensorActuatorNode node;
  private final Label temperatureLabel;
  private final Label humidityLabel;
  private final Client client;
  private final Map<String, CheckBox> actuatorToggles = new HashMap<>();

  public NodeGuiWindow(SensorActuatorNode node, Client client) {
    this.node = node;
    this.client = client;

    // Sensor display
    temperatureLabel = new Label("temperature: " + node.getFormattedTemperature());
    humidityLabel = new Label("humidity: " + node.getFormattedHumidity());
    VBox sensorBox = new VBox(temperatureLabel, humidityLabel);
    TitledPane sensorsPane = new TitledPane("Sensors", sensorBox);

    // Actuator controls
    VBox actuatorPane = new VBox();
    actuatorPane.setSpacing(10);
    for (String actuator : node.getActuators().keySet()) {
      CheckBox toggle = new CheckBox(actuator + ": off");
      actuatorToggles.put(actuator, toggle);
      toggle.setOnAction(event -> {
        boolean isOn = toggle.isSelected();
        node.getActuators().put(actuator, isOn);
        toggle.setText(actuator + ": " + (isOn ? "on" : "off"));

        // Send actuator state to server
        client.sendCommand("COMMAND|" + node.getId() + "|" + actuator + "=" + (isOn ? "on" : "off"));
      });
      actuatorPane.getChildren().add(toggle);
    }
    TitledPane actuatorsPane = new TitledPane("Actuators", actuatorPane);

    getChildren().addAll(sensorsPane, actuatorsPane);

    client.setListener(update -> {
      if (update.contains("nodeId=" + node.getId())) {
        String[] parts = update.split("\\|");
        for (String part : parts) {
          if (part.contains("=")) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
              String key = keyValue[0];
              String value = keyValue[1];

              switch (key) {
                case "nodeId" -> {
                  // Do nothing, or you can log the nodeId for debugging purposes.
                }
                case "temperature" -> node.setTemperature(Double.parseDouble(value.replace(",", ".")));
                case "humidity" -> node.setHumidity(Double.parseDouble(value.replace(",", ".")));
                default -> { // Actuator state
                  if (node.getActuators().containsKey(key)) {
                    boolean state = value.equalsIgnoreCase("on");
                    node.setActuatorState(key, state);
                  } else {
                    System.err.println("Actuator not found: " + key);
                  }
                }
              }
            }
          }
        }
        refreshDisplay();
      }
    });
  }

  private void refreshDisplay() {
    temperatureLabel.setText("temperature: " + node.getFormattedTemperature());
    humidityLabel.setText("humidity: " + node.getFormattedHumidity());
    for (String actuator : node.getActuators().keySet()) {
      CheckBox toggle = actuatorToggles.get(actuator);
      if (toggle != null) {
        boolean state = node.getActuators().get(actuator);
        toggle.setSelected(state);
        toggle.setText(actuator + ": " + (state ? "on" : "off"));
      }
    }
  }
}
