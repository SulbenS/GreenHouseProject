package no.ntnu.gui.greenhouse;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * The main GUI window for greenhouse simulator.
 */
public class MainGreenhouseGuiWindow extends Scene {
  public static final int WIDTH = 300;
  public static final int HEIGHT = 300;
  private final TabPane tabPane;

  public MainGreenhouseGuiWindow(TabPane tabPane) {
    super(tabPane, WIDTH, HEIGHT);
    this.tabPane = tabPane;
  }

  public void addNodeTab(int nodeId, Parent content) {
    Tab nodeTab = new Tab("Node " + nodeId, content);
    tabPane.getTabs().add(nodeTab);
  }

  public void removeNodeTab(int nodeId) {
    tabPane.getTabs().removeIf(tab -> tab.getText().equals("Node " + nodeId));
  }


}