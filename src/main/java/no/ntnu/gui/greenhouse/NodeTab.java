package no.ntnu.gui.greenhouse;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import no.ntnu.gui.ControlPanel;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import java.util.Objects;

import javafx.scene.layout.Priority;

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

  public void addActuatorPane(int nodeId, int actuatorId, String type, boolean state) {
    ActuatorPane actuatorPane = new ActuatorPane(this.observer, nodeId, actuatorId, type);
    actuatorPane.setActuatorState(state);
    this.actuatorPanes.put(actuatorId, actuatorPane);
    this.actuatorsBox.getChildren().add(actuatorPane);
  }

  public void addSensorPane(int sensorId, String type) {
    SensorPane sensorPane = new SensorPane(sensorId, type);
    this.sensorPanes.put(sensorId, sensorPane);
    this.sensorsBox.getChildren().add(sensorPane);
  }

  public void updateSensorReading(int sensorId, String value) {
    this.sensorPanes.get(sensorId).updateSensorReading(Double.parseDouble(value));
  }

  public HBox createNodeButtons() {
    HBox container = new HBox();
    Button AddActuatorButton = new Button("Add Actuator");
    Button AddSensorButton = new Button("Add Sensor");
    Button addNodeButton = new Button("Add Node");
    // Open dialog to add an actuator
    AddActuatorButton.setOnAction(e -> ShowActuatorDialog());


    // Additional buttons can be set up here
    AddSensorButton.setOnAction(e -> {
      ShowSensorDialog();
      // Add logic for adding a sensor
    });

    addNodeButton.setOnAction(e -> {
      // Add logic for adding a node
    });

    container.getChildren().addAll(AddActuatorButton, AddSensorButton, addNodeButton);
    return container;
  }

  private void ShowActuatorDialog(){
    Dialog<String> dialog = new Dialog<>();
    HBox dialogLayout = new HBox(10);
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

    dialogLayout.getChildren().addAll(comboBox, saveButton, close);
    dialog.getDialogPane().setContent(dialogLayout);
    dialog.showAndWait();
  }

  private void ShowSensorDialog() {
    Dialog<String> dialog = new Dialog<>();
    HBox dialogLayout = new HBox(10);
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
  }

  public void setObserver(ControlPanel observer) {
    this.observer = observer;
  }

  private void notifyObserverActuatorAdded(int nodeId, String actuatorType) {
    if (observer != null) {
      observer.onActuatorAddedInGui(nodeId, actuatorType);
    }
  }

  private void notifyObserverSensorAdded(int nodeId, String sensorType) {
    if (observer != null) {
      observer.onSensorAddedInGui(nodeId, sensorType);
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
