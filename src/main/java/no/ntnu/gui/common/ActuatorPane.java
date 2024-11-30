package no.ntnu.gui.common;

import java.util.Objects;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * A section of the GUI representing a list of actuators. Can be used both on the sensor/actuator
 * node, and on a control panel node.
 */
public class ActuatorPane extends Pane {
  private int actuatorId;
  private String actuatorType;
  private boolean actuatorState;

  private Label actuatorLabel;
  private CheckBox actuatorCheckbox;

  private final Pane contentBox;

  /**
   * Create an actuator pane.
   *
   * @param actuatorId The ID of the actuator
   * @param actuatorType The type of the actuator
   **/
  public ActuatorPane(int actuatorId, String actuatorType) {
    this.actuatorType = actuatorType;
    this.actuatorId = actuatorId;
    this.actuatorState = false;

    this.contentBox = new HBox();
    this.actuatorLabel = new Label(generateActuatorText());
    this.actuatorCheckbox = createActuatorCheckbox();
    this.contentBox.getChildren().add(actuatorLabel);
    this.contentBox.getChildren().add(actuatorCheckbox);
    this.contentBox.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
    this.setPrefHeight(5000);
  }


  private CheckBox createActuatorCheckbox() {
    CheckBox checkbox = new CheckBox();
    checkbox.setSelected(false);
    checkbox.setOnAction(event -> {
      this.actuatorState = checkbox.isSelected();
    });
    // TODO: Add checkbox listener
    checkbox.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
    return checkbox;
  }

  private String generateActuatorText() {
    return this.actuatorType + ": " + (this.actuatorState ? "ON" : "OFF");
  }

  /**
   * An actuator has been updated, update the corresponding GUI parts.
   */
  public void update() {
    Platform.runLater(() -> {
      this.actuatorCheckbox.setSelected(!actuatorCheckbox.isSelected());
    });
  }

  public int getActuatorId() {
    return actuatorId;
  }

  public String getActuatorType() {
    return actuatorType;
  }

  public boolean getActuatorState() {
    return actuatorState;
  }

}
