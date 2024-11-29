package no.ntnu.gui.greenhouse;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import no.ntnu.greenhouse.ControlPanel;
import no.ntnu.listeners.node.NodeStateListener;

/**
 * Run a greenhouse simulation with a graphical user interface (GUI), with JavaFX.
 */
public class GreenhouseApplication extends Application implements NodeStateListener {
  private Stage stage;
  private Scene scene;
  private TabPane tabPane;

  private int width = 300;
  private int height = 450;

  private ControlPanel controlPanel;

  public GreenhouseApplication() {
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
    this.stage.show();
    this.stage.setOnCloseRequest(event -> this.controlPanel.closeApplication());
  }

  @Override
  public void onNodeReady(int nodeId) {
    System.out.println("Starting window for node " + nodeId);
    NodeTab window = new NodeTab(this.controlPanel.requestNode(nodeId));
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