package no.ntnu.gui;

import no.ntnu.client.Client;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import no.ntnu.nodes.SensorActuatorNode;
import no.ntnu.server.Server;

public class ControlPanelApplication extends Application {

  @Override
  public void start(Stage primaryStage) {
    // Create a TabPane to hold all node tabs
    TabPane tabPane = new TabPane();

    // Add sample nodes as tabs
    addNodeTab(tabPane, new SensorActuatorNode(1));
    addNodeTab(tabPane, new SensorActuatorNode(2));
    addNodeTab(tabPane, new SensorActuatorNode(3));

    // Create the scene with the TabPane
    Scene scene = new Scene(tabPane, 400, 400);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Greenhouse!");
    primaryStage.show();
  }

  private void addNodeTab(TabPane tabPane, SensorActuatorNode node) {
    // Create a Client instance for this node
    Client client = new Client("127.0.0.1", 12345);

    // Create the NodeGuiWindow with the node and client
    NodeGuiWindow nodeGui = new NodeGuiWindow(node, client);

    // Create a tab with the node's ID as the title
    Tab tab = new Tab("Node " + node.getId(), nodeGui);
    tabPane.getTabs().add(tab);
  }

  private void startServerInBackground() {
    new Thread(() -> {
      System.out.println("Server is running...");
      try {
        new Server().start(); // Replace with actual server start logic
      } catch (Exception e) {
        System.err.println("Server failed to start: " + e.getMessage());
      }
    }).start();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
