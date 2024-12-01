package no.ntnu.gui.common;

import java.util.*;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * A section of GUI displaying sensor data.
 */
public class SensorPane extends Pane {
  private int sensorId;
  private String sensorType;
  private int sensorValue;

  private Label sensorLabel;
  private CheckBox sensorCheckbox;

  private final Pane contentBox;

  /**
   * Create a sensor pane.
   * Wrapper for the other constructor with SensorReading-iterable parameter
   *
   * @param sensorId the id of the sensor.
   * @param sensorType the type of the sensor.
   */
  public SensorPane(int sensorId , String sensorType) {
    this.contentBox = new HBox();
    this.contentBox.getChildren().add(new Label(sensorType));
    setPrefHeight(5000);
  }

  private String generateSensorLabel() {
    return this.sensorType + ": " + sensorValue;
  }

  /**
   * Update the GUI according to the changes in sensor data.
   */
  public void update() {
    Platform.runLater(() -> this.sensorLabel.setText(generateSensorLabel()));
  }

  /**
   * Update the GUI according to the changes in sensor data.
   * Wrapper for the other method with SensorReading-iterable parameter
   *
   * @param sensors The sensor data that has been updated
   */
  public void update(List<Sensor> sensors) {
    update(sensors.stream().map(Sensor::getReading).toList());
  }

  private Label createAndRememberSensorLabel(SensorReading sensor) {
    SimpleStringProperty props = new SimpleStringProperty(generateSensorLabel(sensor));
    this.sensorProps.add(props);
    Label label = new Label();
    label.textProperty().bind(props);
    label.getStyleClass().add("sensor-label");
    return label;
  }

  private void updateSensorLabel(SensorReading sensor, int index) {
    if (this.sensorProps.size() > index) {
      SimpleStringProperty props = this.sensorProps.get(index);
      Platform.runLater(() -> props.set(generateSensorLabel(sensor)));
    } else {
      System.out.println("Adding sensor[" + index + "]");
      Platform.runLater(() -> this.contentBox.getChildren().add(createAndRememberSensorLabel(sensor)));
    }
  }
}
