package no.ntnu.gui.greenhouse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;

/**
 * Window with GUI for overview and control of one specific sensor/actuator node.
 */
public class NodeTab extends Stage {
  private static final double VERTICAL_OFFSET = 50;
  private static final double HORIZONTAL_OFFSET = 150;
  private static final double WINDOW_WIDTH = 300;
  private static final double WINDOW_HEIGHT = 300;

  private Pane contentBox;

  private Map<Integer, ActuatorPane> actuatorPanes;
  private Map<Integer, SensorPane> sensorPanes;

  private int nodeId;

  /**
   * Create a GUI window for a specific node.
   */
  public NodeTab(int nodeId) {
    this.nodeId = nodeId;
    this.actuatorPanes = new HashMap<>();
    this.sensorPanes = new HashMap<>();
    this.contentBox = new VBox();
    Scene scene = new Scene(this.contentBox, WINDOW_WIDTH, WINDOW_HEIGHT);
    setScene(scene);
    setTitle("Node " + this.nodeId);
    setPositionAndSize();
  }

  public void addActuatorPane(int actuatorId, String type) {
    ActuatorPane actuatorPane = new ActuatorPane(actuatorId, type);
    this.actuatorPanes.put(actuatorId, actuatorPane);
    createContent(actuatorPane);
  }

  public void addSensorPane(int sensorId, String type) {
    SensorPane sensorPane = new SensorPane(sensorId, type);
    this.sensorPanes.put(sensorId, sensorPane);
    createContent(sensorPane);
  }

  private void createContent(Pane pane) {
    VBox ActuatorSensorPane = new VBox(pane);
    ActuatorSensorPane.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
    this.contentBox.getChildren().add(ActuatorSensorPane);
  }

  public void updateSensorReading(int sensorId, String value) {
    this.sensorPanes.get(sensorId).updateSensorReading(Double.parseDouble(value));
  }

  private void setPositionAndSize() {
    setX((this.nodeId - 1) * HORIZONTAL_OFFSET);
    setY(this.nodeId * VERTICAL_OFFSET);
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
