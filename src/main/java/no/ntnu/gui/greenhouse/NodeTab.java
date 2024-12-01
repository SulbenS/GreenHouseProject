package no.ntnu.gui.greenhouse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;

/**
 * Window with GUI for overview and control of one specific sensor/actuator node.
 */
public class NodeTab extends VBox {
  private static final double VERTICAL_OFFSET = 50;
  private static final double HORIZONTAL_OFFSET = 150;
  private static final double WINDOW_WIDTH = 300;
  private static final double WINDOW_HEIGHT = 300;

  private Pane contentBox;

  private TitledPane actuatorsTitledPane;
  private TitledPane sensorsTitledPane;

  private Map<Integer, ActuatorPane> actuatorPanes;
  private Map<Integer, SensorPane> sensorPanes;

  private int nodeId;

  /**
   * Create a GUI window for a specific node.
   */
  public NodeTab(int nodeId) {
    this.nodeId = nodeId;
    this.contentBox = new VBox();
    this.actuatorsTitledPane = new TitledPane();
    this.sensorsTitledPane = new TitledPane();
    this.actuatorPanes = new HashMap<>();
    this.sensorPanes = new HashMap<>();
    this.contentBox.getChildren().add(this.actuatorsTitledPane);
    this.contentBox.getChildren().add(this.sensorsTitledPane);
    this.actuatorsTitledPane.setText("Actuators");
    this.sensorsTitledPane.setText("Sensors");

    // Set initial properties for the VBox (this is now the root layout)
    setPrefWidth(WINDOW_WIDTH);
    setPrefHeight(WINDOW_HEIGHT);
    setSpacing(10); // Optional spacing between components
    setPositionAndSize();
    //getStyleClass().add("node-tab");

    // Optionally add some default content or styling
    // getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
    // TODO: add styling, maybe title, etc.
    getChildren().add(this.contentBox);
  }

  public void addActuatorPane(int actuatorId, String type) {
    ActuatorPane actuatorPane = new ActuatorPane(actuatorId, type);
    this.actuatorPanes.put(actuatorId, actuatorPane);
    this.actuatorsTitledPane.setContent(actuatorPane);
  }

  public void addSensorPane(int sensorId, String type) {
    SensorPane sensorPane = new SensorPane(sensorId, type);
    this.sensorPanes.put(sensorId, sensorPane);
    this.sensorsTitledPane.setContent(sensorPane);
  }

  public void updateSensorReading(int sensorId, String value) {
    this.sensorPanes.get(sensorId).updateSensorReading(Double.parseDouble(value));
  }

  private void setPositionAndSize() {
    this.contentBox.setLayoutX((this.nodeId - 1) * HORIZONTAL_OFFSET);
    this.contentBox.setLayoutY(this.nodeId * VERTICAL_OFFSET);
    setMinWidth(WINDOW_HEIGHT);
    setMinHeight(WINDOW_WIDTH);
  }

  /**
   * Return the nodeId.
   *
   * @return The nodeId
   */
  public int getNodeId() {
    return this.nodeId;
  }
}
