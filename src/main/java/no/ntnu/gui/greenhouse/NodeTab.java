package no.ntnu.gui.greenhouse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.gui.ControlPanel;
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

  private HBox nodeButtons;
  private VBox actuatorsBox;
  private VBox sensorsBox;

  private TitledPane actuatorsTitledPane;
  private TitledPane sensorsTitledPane;

  private Map<Integer, ActuatorPane> actuatorPanes;
  private Map<Integer, SensorPane> sensorPanes;

  private int nodeId;

  private ControlPanel observer;


  /**
   * Create a GUI window for a specific node.
   */
  public NodeTab(int nodeId) {
    this.nodeId = nodeId;
    this.contentBox = new VBox();

    nodeButtons = createNodeButtons();
    this.actuatorsTitledPane = new TitledPane();
    this.sensorsTitledPane = new TitledPane();
    this.actuatorPanes = new HashMap<>();
    this.sensorPanes = new HashMap<>();
    this.contentBox.getChildren().add(nodeButtons);
    this.contentBox.getChildren().add(this.sensorsTitledPane);
    this.contentBox.getChildren().add(this.actuatorsTitledPane);
    this.actuatorsTitledPane.setText("Actuators");
    this.sensorsTitledPane.setText("Sensors");
    this.sensorsBox = new VBox();
    this.actuatorsBox = new VBox();
    this.sensorsTitledPane.setContent(this.sensorsBox);
    this.actuatorsTitledPane.setContent(this.actuatorsBox);

    this.sensorsBox.getStyleClass().add("sensors-vbox");
    this.actuatorsBox.getStyleClass().add("actuators-vbox");
    getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
    getStyleClass().add("node-tab");
    actuatorsTitledPane.setMaxHeight(10000000);
    VBox.setVgrow(this.actuatorsTitledPane, Priority.ALWAYS);
    VBox.setVgrow(this.contentBox, Priority.ALWAYS);
    actuatorsTitledPane.collapsibleProperty().set(false);
    sensorsTitledPane.collapsibleProperty().set(false);
    getChildren().add(this.contentBox);
  }

  /**
   * Add an actuator pane to the node tab.
   *
   * @param nodeId    The ID of the node
   * @param actuatorId The ID of the actuator
   * @param type      The type of the actuator
   * @param state     The state of the actuator
   */
  public void addActuatorPane(int nodeId, int actuatorId, String type, boolean state) {
    ActuatorPane actuatorPane = new ActuatorPane(this.observer, nodeId, actuatorId, type);
    actuatorPane.setActuatorState(state);
    this.actuatorPanes.put(actuatorId, actuatorPane);
    this.actuatorsBox.getChildren().add(actuatorPane);
  }

  /**
   * Update the state of an actuator in the GUI.
   *
   * @param type The ID of the actuator
   * @param sensorId      The new state of the actuator
   */
  public void addSensorPane(int sensorId, String type) {
    SensorPane sensorPane = new SensorPane(sensorId, type);
    this.sensorPanes.put(sensorId, sensorPane);
    this.sensorsBox.getChildren().add(sensorPane);
  }

  /**
   * Update the state of a sensor in the GUI.
   *
   * @param value The ID of the actuator
   * @param sensorId      The new state of the actuator
   */
  public void updateSensorReading(int sensorId, String value) {
    this.sensorPanes.get(sensorId).updateSensorReading(Double.parseDouble(value));
  }

  /**
   * Create buttons for adding actuators and sensors.
   *
   */
  public HBox createNodeButtons() {
    Button addActuatorButton = new Button("Add Actuator");
    Button addSensorButton = new Button("Add Sensor");
    Button addNodeButton = new Button("Add Node");
    Button removeNodeButton = new Button("Remove Node");
    // Open dialog to add an actuator
    addActuatorButton.setOnAction(e -> showActuatorDialog());

    // Additional buttons can be set up here
    addSensorButton.setOnAction(e -> {
      showSensorDialog();
      // Add logic for adding a sensor
    });

    addNodeButton.setOnAction(e -> {
      if (observer != null) {
        observer.addNodeTab(this.nodeId);
      }
    });

    HBox container = new HBox();
    container.getChildren().addAll(addActuatorButton, addSensorButton, addNodeButton, removeNodeButton);
    return container;
  }

  /**
   * Show a dialog for adding an actuator.
   */
  private void showActuatorDialog() {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Add Actuator");
    ComboBox<String> comboBox = new ComboBox<>();
    comboBox.getItems().addAll("Window", "Fan", "Heater");
    comboBox.setValue("Window");
    Button saveButton = new Button("Add");
    Button close = new Button("Exit");
    saveButton.setOnAction(e -> {
      notifyObserverActuatorAdded(this.nodeId, comboBox.getValue().toLowerCase());
      dialog.setResult("");
    });
    close.setOnAction(e -> {
      dialog.setResult("");
      dialog.close();
    });

    HBox dialogLayout = new HBox(10);
    dialogLayout.getChildren().addAll(comboBox, saveButton, close);
    dialog.getDialogPane().setContent(dialogLayout);
    dialog.showAndWait();
  }

  /**
   * Show a dialog for adding sensor.
   *
   */
  private void showSensorDialog() {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Add Sensor");
    ComboBox<String> comboBox = new ComboBox<>();
    comboBox.getItems().addAll("Temperature", "Humidity", "Light");
    comboBox.setValue("Temperature");
    Button saveButton = new Button("Add");
    Button close = new Button("Exit");
    saveButton.setOnAction(e -> {
      notifyObserverSensorAdded(this.nodeId, comboBox.getValue().toLowerCase());
      dialog.setResult("");
    });
    close.setOnAction(e -> {
      dialog.setResult("");
      dialog.close();
    });
    HBox dialogLayout = new HBox(10);
    dialogLayout.getChildren().addAll(comboBox, saveButton, close);
    dialog.getDialogPane().setContent(dialogLayout);
    dialog.showAndWait();
  }

  /**
   * Sets observer.
   *
   * @param observer The observer to set
   *
   */
  public void setObserver(ControlPanel observer) {
    this.observer = observer;
  }

  private void notifyObserverActuatorAdded(int nodeId, String actuatorType) {
    if (observer != null) {
      observer.onActuatorAddedInGui(nodeId, actuatorType);
    } else {
      System.out.println("Observer is null");
    }
  }

  private void notifyObserverSensorAdded(int nodeId, String sensorType) {
    if (observer != null) {
      observer.onSensorAddedInGui(nodeId, sensorType);
    } else {
      System.out.println("Observer is null");
    }
  }

  public boolean hasActuatorPane(int actuatorId) {
    return this.actuatorPanes.containsKey(actuatorId);
  }

  public boolean hasSensorPane(int sensorId) {
    return this.sensorPanes.containsKey(sensorId);
  }

  /**
   * Return the nodeId.
   *
   * @return The nodeId.
   */
  public int getNodeId() {
    return this.nodeId;
  }
}
