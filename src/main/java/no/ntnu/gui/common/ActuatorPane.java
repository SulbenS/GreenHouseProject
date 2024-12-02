package no.ntnu.gui.common;

import java.util.Objects;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import no.ntnu.gui.ControlPanel;
import no.ntnu.listeners.node.ActuatorListener;

/**
 * A section of the GUI representing a list of actuators. Can be used both on the sensor/actuator
 * node, and on a control panel node.
 */
public class ActuatorPane extends Pane {
  private ActuatorListener listener;
  private int nodeId;

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
  public ActuatorPane(ControlPanel controlPanel, int nodeId, int actuatorId, String actuatorType) {
    this.nodeId = nodeId;
    this.actuatorType = actuatorType;
    this.actuatorId = actuatorId;
    this.actuatorState = false;
    this.listener = controlPanel;

    this.contentBox = new HBox();

    this.actuatorLabel = new Label(generateActuatorLabel());
    this.actuatorCheckbox = createActuatorCheckbox();
    this.contentBox.getChildren().add(generateRemoveButton());
    this.contentBox.getChildren().add(actuatorLabel);
    this.contentBox.getChildren().add(actuatorCheckbox);
    this.contentBox.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
    this.actuatorLabel.getStyleClass().add("actuator-label");
    this.contentBox.getStyleClass().add("actuator-pane");
    getChildren().add(this.contentBox);
  }

  private Button generateRemoveButton() {
    Button removeButton = new Button("X");
    removeButton.getStyleClass().add("remove-button");
    removeButton.setOnAction(e -> {
      Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
              "Are you sure you want to remove this actuator?", ButtonType.YES, ButtonType.NO);
      confirmationAlert.showAndWait().ifPresent(response -> {
        if (response == ButtonType.YES) {
          ((Pane) this.getParent()).getChildren().remove(this);
        }
      });
    });
    return removeButton;
  }

  private CheckBox createActuatorCheckbox() {
    CheckBox checkbox = new CheckBox();
    checkbox.setSelected(false);
    checkbox.setOnAction(event -> {
      this.actuatorState = checkbox.isSelected();
      this.actuatorLabel.setText(generateActuatorLabel());
      if (this.listener != null) {
        this.listener.onActuatorStateChanged(this.nodeId, this.actuatorId, this.actuatorState);
      } else {
        System.out.println("No listener set for actuator state changes.");
      }
    });
    checkbox.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
    return checkbox;
  }

  private String generateActuatorLabel() {
    String label = this.actuatorType + ": ";
    label = label + switch (this.actuatorType) {
      case "window" -> (this.actuatorState ? "Open" : "Closed");
      case "fan", "heater" -> (this.actuatorState ? "On" : "Off");
      default -> (this.actuatorState ? "On" : "Off");
    };
    return label;
  }

  public void setActuatorListener(ActuatorListener listener) {
    this.listener = listener;
  }

  /**
   * Set the state of the actuator.
   *
   * @param state The state of the actuator.
   *
   */

  public void setActuatorState(boolean state) {
    this.actuatorState = state;
    Platform.runLater(() -> this.actuatorCheckbox.setSelected(state));
    this.actuatorLabel.setText(generateActuatorLabel());
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