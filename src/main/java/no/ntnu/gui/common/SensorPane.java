package no.ntnu.gui.common;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import java.util.Objects;

/**
 * A section of GUI displaying sensor data.
 */
public class SensorPane extends Pane {
  private int sensorId;
  private String sensorType;
  private double sensorValue;

  private Label sensorLabel;

  private final Pane contentBox;

  /**
   * Create a sensor pane.
   * Wrapper for the other constructor with SensorReading-iterable parameter
   *
   * @param sensorId the id of the sensor.
   * @param sensorType the type of the sensor.
   */
  public SensorPane(int sensorId , String sensorType) {
    this.sensorType = sensorType;
    this.sensorId = sensorId;
    this.sensorLabel = new Label(generateSensorLabel().getText());
    this.contentBox = new HBox();
    this.contentBox.getChildren().add(this.sensorLabel);
    this.contentBox.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
    contentBox.getStyleClass().add("sensor-pane");
    getChildren().add(this.contentBox);
  }

  private Label generateSensorLabel() {
    this.sensorLabel = new Label(this.sensorType + ": " + this.sensorValue);
    this.sensorLabel.getStyleClass().add("sensor-label");
    return this.sensorLabel;
  }

  public void updateSensorReading(double sensorReading) {
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