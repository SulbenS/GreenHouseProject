package no.ntnu.gui.common;

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

  private Label generateSensorLabel() {
    return new Label(this.sensorType + ": " + sensorValue);
  }

  private void updateSensorLabel(int sensorReading) {
    this.sensorValue = sensorReading;
    Platform.runLater(() -> this.contentBox.getChildren().set(0, generateSensorLabel()));
  }

  /**
   * Update the GUI according to the changes in sensor data.
   */
  public void update() {
    Platform.runLater(() -> this.sensorLabel.setText(generateSensorLabel().getText()));
  }
}