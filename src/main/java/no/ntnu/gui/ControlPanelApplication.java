package no.ntnu.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import no.ntnu.client.Client;
import no.ntnu.nodes.SensorActuatorNode;

public class ControlPanelApplication extends Application {

  @Override
  public void start(Stage primaryStage) {
    TabPane tabPane = new TabPane();

    // Add nodes as tabs
    addNodeTab(tabPane, new SensorActuatorNode(1));
    addNodeTab(tabPane, new SensorActuatorNode(2));
    addNodeTab(tabPane, new SensorActuatorNode(3));

    Scene scene = new Scene(tabPane, 400, 400);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Greenhouse!");
    primaryStage.show();
  }

  private void addNodeTab(TabPane tabPane, SensorActuatorNode node) {
    Client client = new Client("127.0.0.1", 12345);
    NodeGuiWindow nodeGui = new NodeGuiWindow(node, client);

    Tab tab = new Tab("Node " + node.getId(), nodeGui);
    tabPane.getTabs().add(tab);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
