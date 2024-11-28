package no.ntnu.gui.greenhouse;

import java.util.List;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.greenhouse.node.Actuator;
import no.ntnu.greenhouse.node.Sensor;
import no.ntnu.greenhouse.node.Node;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.node.ActuatorListener;
import no.ntnu.listeners.node.NodeStateListener;
import no.ntnu.listeners.node.SensorListener;

/**
 * Window with GUI for overview and control of one specific sensor/actuator node.
 */
public class NodeTab extends Stage implements SensorListener, ActuatorListener {
  private static final double VERTICAL_OFFSET = 50;
  private static final double HORIZONTAL_OFFSET = 150;
  private static final double WINDOW_WIDTH = 300;
  private static final double WINDOW_HEIGHT = 300;
  private ActuatorPane actuatorPane;
  private SensorPane sensorPane;
  private final Node node;

  /**
   * Create a GUI window for a specific node.
   *
   * @param node The node which will be handled in this window.
   */
  public NodeTab(Node node) {
    this.node = node;
    Scene scene = new Scene(createContent(), WINDOW_WIDTH, WINDOW_HEIGHT);
    setScene(scene);
    setTitle("Node " + node.getId());
    initializeListeners(node);
    setPositionAndSize();
  }

  private void setPositionAndSize() {
    setX((node.getId() - 1) * HORIZONTAL_OFFSET);
    setY(node.getId() * VERTICAL_OFFSET);
    setMinWidth(WINDOW_HEIGHT);
    setMinHeight(WINDOW_WIDTH);
  }


  private void initializeListeners(Node node) {
    setOnCloseRequest(windowEvent -> shutDownNode());
    node.addSensorListener(this);
    node.addActuatorListener(this);
  }

  private void shutDownNode() {
    node.stop();
  }

  private Parent createContent() {
    actuatorPane = new ActuatorPane(node.getActuators());
    actuatorPane.getStyleClass().add("actuator-pane");
    sensorPane = new SensorPane(node.getSensors());
    sensorPane.getStyleClass().add("sensor-pane");
    VBox vbox = new VBox(sensorPane, actuatorPane);
    vbox.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    return vbox;
  }


  @Override
  public void sensorsUpdated(List<Sensor> sensors) {
    if (sensorPane != null) {
      sensorPane.update(sensors);
    }
  }

  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    if (actuatorPane != null) {
      actuatorPane.update(actuator);
    }
  }
}
