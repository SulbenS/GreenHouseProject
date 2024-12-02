package no.ntnu.gui.greenhouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import no.ntnu.gui.ControlPanel;

/**
 * Run a greenhouse simulation with a graphical user interface (GUI), with JavaFX.
 */
public class GreenhouseApplication extends Application {
  private Stage stage;
  private Scene scene;
  private TabPane tabPane;

  private ControlPanel controlPanel;
  private List<NodeTab> nodeTabs;

  /**
   * Constructor for the GreenhouseApplication.
   */
  public GreenhouseApplication() {
    this.nodeTabs = new ArrayList<>();
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
    this.scene = new Scene(this.tabPane, 500, 600);
    this.scene.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
    this.stage.setScene(this.scene);
    this.stage.setTitle("Greenhouse simulator");
    this.stage.show();
    this.stage.setOnCloseRequest(event -> this.controlPanel.closeApplication());
    this.controlPanel = new ControlPanel(this);
    this.controlPanel.start();
  }

  /**
   * Add a NodeTab to the GUI.
   *
   * @param nodeId ID of the node
   */
  public void addNodeTab(int nodeId) {
    NodeTab nodeTab = new NodeTab(nodeId);
    this.nodeTabs.add(nodeTab);
    Tab tab = new Tab("Node " + nodeTab.getNodeId(), nodeTab);
    this.tabPane.getTabs().add(tab);
  }

  /**
   * Check if a NodeTab for a specific node exists.
   *
   * @param nodeId ID of the node
   * @return True if a NodeTab exists for the node
   */
  public boolean hasNodeTab(int nodeId) {
    if (this.nodeTabs == null) {
      return false;
    }
    for (NodeTab tab : this.nodeTabs) {
      if (tab.getNodeId() == (nodeId)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return the NodeTab for a specific node.
   *
   * @param nodeId ID of the node
   * @return The NodeTab for the node
   */
  public NodeTab getNodeTab(int nodeId) {
    for (NodeTab tab : this.nodeTabs) {
      if (tab.getNodeId() == (nodeId)) {
        return tab;
      }
    }
    throw new IllegalArgumentException("No tab found for node " + nodeId);
  }
}