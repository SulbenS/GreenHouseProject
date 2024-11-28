package no.ntnu.gui.greenhouse;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import no.ntnu.greenhouse.Simulator;
import no.ntnu.greenhouse.tcp.Node;
import no.ntnu.listeners.greenhouse.NodeStateListener;

/**
 * Run a greenhouse simulation with a graphical user interface (GUI), with JavaFX.
 */
public class GreenhouseApplication extends Application implements NodeStateListener {
  private static Simulator simulator;
  private final Map<Node, NodeGuiWindow> nodeWindows = new HashMap<>();
  private Stage stage;

  @Override
  public void start(Stage mainStage) {
    this.stage = mainStage;
    mainStage.setScene(new MainGreenhouseGuiWindow());
    mainStage.setMinWidth(MainGreenhouseGuiWindow.WIDTH);
    mainStage.setMinHeight(MainGreenhouseGuiWindow.HEIGHT);
    mainStage.setTitle("Greenhouse simulator");
    mainStage.show();
    simulator.initialize();
    simulator.subscribeToLifecycleUpdates(this);
    mainStage.setOnCloseRequest(event -> closeApplication());
    simulator.start();
  }

  private void closeApplication() {
    System.out.println("Closing Greenhouse application...");
    simulator.stop();
    try {
      stop();
    } catch (Exception e) {
      System.out.println("Could not stop the application: " + e.getMessage());
    }
  }

  /**
   * Start the GUI Application.
   */
  public static void startApp() {
    System.out.println("Running greenhouse simulator with JavaFX GUI...");
    simulator = new Simulator();
    launch();
  }

  @Override
  public void onNodeReady(Node node) {
    System.out.println("Starting window for node " + node.getId());
    NodeGuiWindow window = new NodeGuiWindow(node);
    nodeWindows.put(node, window);
    window.show();
  }

  @Override
  public void onNodeStopped(Node node) {
    NodeGuiWindow window = nodeWindows.remove(node);
    if (window != null) {
      Platform.runLater(window::close);
      if (nodeWindows.isEmpty()) {
        Platform.runLater(stage::close);
      }
    }
  }
}
