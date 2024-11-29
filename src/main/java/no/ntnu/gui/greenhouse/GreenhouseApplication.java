package no.ntnu.gui.greenhouse;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import no.ntnu.greenhouse.Simulator;
import no.ntnu.listeners.node.NodeStateListener;

/**
 * Run a greenhouse simulation with a graphical user interface (GUI), with JavaFX.
 */
public class GreenhouseApplication extends Application implements NodeStateListener {
  private Simulator simulator;
  private Stage stage;
  private Scene scene;
  private TabPane tabPane;

  private int width = 450;
  private int height = 500;


  public GreenhouseApplication() {
    this.simulator = new Simulator();
  }

  /**
   * Start the GUI Application.
   */
  public void startApp() {
    System.out.println("Running greenhouse simulator with JavaFX GUI...");
    launch();
  }
  
  @Override
  public void start(Stage stage) {
    this.stage = stage;
    this.tabPane = new TabPane();
    this.scene = new Scene(this.tabPane, width, height);
    this.scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    this.stage.setScene(scene);
    this.stage.setTitle("Greenhouse simulator");
    this.stage.setMaxHeight(height);
    this.stage.setMaxWidth(width);
    this.stage.setMinHeight(height);
    this.stage.setMinWidth(width);
    this.stage.show();
    this.simulator.initialize();
    this.simulator.subscribeToLifecycleUpdates(this);
    this.stage.setOnCloseRequest(event -> closeApplication());
    this.simulator.start();
  }

  private void closeApplication() {
    System.out.println("Closing Greenhouse application...");
    this.simulator.stop();
    try {
      stop();
    } catch (Exception e) {
      System.out.println("Could not stop the application: " + e.getMessage());
    }
  }

  @Override
  public void onNodeReady(int nodeId) {
    System.out.println("Starting window for node " + nodeId);
    NodeTab window = new NodeTab(this.simulator.getNode(nodeId));
    Platform.runLater(() ->
            this.tabPane.getTabs().add(new Tab("Node " + nodeId, window.getScene().getRoot()))
    );
  }

  @Override
  public void onNodeStopped(int nodeId) {
    Platform.runLater(() -> {
      tabPane.getTabs().removeIf(tab -> tab.getText().equals("Node " + nodeId));
    });
  }
}