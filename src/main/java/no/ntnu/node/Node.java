package no.ntnu.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import no.ntnu.commands.*;
import no.ntnu.listeners.node.ActuatorListener;
import no.ntnu.listeners.node.NodeStateListener;
import no.ntnu.listeners.node.SensorListener;
import no.ntnu.tools.MessageHandler;

/**
 * Represents one node with sensors and actuators.
 */
public class Node {
  // How often to generate new sensor values, in seconds.
  private static final long SENSING_DELAY = 5000;
  private final int id;

  private final List<Sensor> sensors = new LinkedList<>();
  private final ActuatorCollection actuators = new ActuatorCollection();

  private final List<SensorListener> sensorListeners = new LinkedList<>();
  private final List<ActuatorListener> actuatorListeners = new LinkedList<>();
  private final List<NodeStateListener> stateListeners = new LinkedList<>();

  private Timer sensorReadingTimer;

  private boolean running;
  private final Random random = new Random();

  private Socket socket;

  private BufferedReader reader;
  private PrintWriter writer;

  /**
   * Create a sensor/actuator node. Note: the node itself does not check whether the ID is unique.
   * This is done at the greenhouse-level.
   *
   * @param id A unique ID of the node
   */
  public Node(int id) {
    this.running = false;
    this.id = id;
  }

  /**
   * Start simulating the sensor node's operation.
   */
  public void start() {
    if (!running) {
      try {
        establishConnection();
        this.actuators.forEach(actuator ->
                sendMessage(MessageHandler.actuatorToString(actuator))
        );
        this.sensors.forEach(sensor ->
                sendMessage(MessageHandler.sensorToString(sensor))
        );
      } catch (IllegalArgumentException e) {
        System.out.println("Could not establish connection to node.");
        System.out.println(e.getMessage());
      }
      running = true;
      new Thread(() -> {
        System.out.println("-- Starting simulation of node " + id);
        run();
      }).start();
      startPeriodicSensorReading();
    }
  }

  /**
   * Run the node.
   */
  public void run() {
    while (running) {
      String rawMessage = readMessage();
      Data dataType = MessageHandler.getData(rawMessage);
      if (dataType instanceof NodeCommand) {
        executeCommand(rawMessage);
      } else if (dataType instanceof ActuatorAddedInGui) {
        executeCommand(rawMessage);
      }
    }
  }

  /**
   * Establishes a connection to the server.
   */
  public void establishConnection() {
    try {
      this.socket = new Socket("localhost", 1238);
    } catch (IOException e) {
      System.out.println("Could not connect to the server.");
      System.out.println(e.getMessage());
      throw new IllegalArgumentException("Could not connect to the server");
    } try {
      this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      this.writer = new PrintWriter(this.socket.getOutputStream(), true);
      this.writer.println("Data=Identifier;Node=" + this.id);
    } catch (IOException e) {
      System.out.println("Could not create the reader/writer.");
      System.out.println(e.getMessage());
      throw new IllegalArgumentException("Could not create the reader/writer");
    }
  }

  private void disconnectFromServer() {
    try {
      if (socket != null) {
        socket.close();
        socket = null;
        reader = null;
        writer = null;
      }
    } catch (IOException e) {
      System.err.println("Node " + id + ": Error disconnecting from server: " + e.getMessage());
    }
  }

  /**
   * Returns the message from the server.
   *
   * @return The message from the server.
   */
  public String readMessage() {
    String rawMessage = "";
    try {
      rawMessage = this.reader.readLine();
    } catch (IOException e) {
      System.out.println("Could not read the message.");
      System.out.println(e.getMessage());
    }
    return rawMessage;
  }

  /**
   * Sends a message to the server.
   *
   * @param message The message to send.
   */
  public void sendMessage(String message) {
    this.writer.println(message);
  }

  /**
   * Executes a command based on the message given.
   *
   * @param rawMessage The message given.
   */
  public void executeCommand(String rawMessage) {
    System.out.println("Executing command: " + rawMessage);
    if (MessageHandler.getData(rawMessage).getData().equals("NodeCommand")) {
      NodeCommand nodeCommand = (NodeCommand) MessageHandler.getData(rawMessage);
      if (nodeCommand.getAction().equals("Off")) {
        stop();
      }
    } else if (MessageHandler.getData(rawMessage).getData().equals("ActuatorCommand")) {
      ActuatorCommand actuatorCommand = (ActuatorCommand) MessageHandler.getData(rawMessage);
      Actuator actuator = getActuator(actuatorCommand.getActuatorId());
      if (actuatorCommand.getAction().equals("On")) {
        actuator.setState(true);
        sendMessage("Data=Identifier"
                + ";Node=" + actuator.getNodeId()
                + ";Type=" + actuatorCommand.getActuatorType()
                + ";Actuator=" + actuator.getId()
                + ";State=On");
        System.out.println("Actuator " + actuatorCommand.getActuatorId() + " turned on");
      } else if (actuatorCommand.getAction().equals("Off")) {
        actuator.setState(false);
        System.out.println("Actuator " + actuatorCommand.getActuatorId() + " turned off");
        sendMessage("Data=Identifier"
                + ";Node=" + actuator.getNodeId()
                + ";Type=" + actuatorCommand.getActuatorType()
                + ";Actuator=" + actuator.getId()
                + ";State=Off");
        System.out.println("Actuator " + actuatorCommand.getActuatorId() + " turned off" + "aksbd-uaGVSDHJ: man;CD");
      }
    } else if (MessageHandler.getData(rawMessage).getData().equals("ActuatorAddedInGui")) {
      ActuatorAddedInGui actuatorAddedInGui = (ActuatorAddedInGui) MessageHandler.getData(rawMessage);

      Actuator addedActuator = new Actuator(actuatorAddedInGui.getActuatorType(), actuatorAddedInGui.getNodeId());
      addActuator(addedActuator);
      System.out.println("Actuator " + actuatorAddedInGui.getActuatorType() + " added to node "
              + actuatorAddedInGui.getNodeId() + "asdgauydsg oA BSdgiuysavdauyGLDS");
      // send identifier back to the server and gui so that the gui can update the actuator with correct actuatorId
      sendMessage("Data=Identifier"
              + ";Node=" + actuatorAddedInGui.getNodeId()
              + ";Actuator=" + addedActuator.getId()
              + ";Type=" + addedActuator.getType()
              + ";State=" + addedActuator.isOn());
    }
  }

  /**
   * Add sensors to the node.
   *
   * @param template The template to use for the sensors. The template will be cloned.
   *                 This template defines the type of sensors, the value range, value
   *                 generation algorithms, etc.
   * @param n        The number of sensors to add to the node.
   */
  public void addSensors(Sensor template, int n) {
    if (template == null) {
      throw new IllegalArgumentException("Sensor template is missing");
    }
    String type = template.getType();
    if (type == null || type.isEmpty()) {
      throw new IllegalArgumentException("Sensor type missing");
    }
    if (n <= 0) {
      throw new IllegalArgumentException("Can't add a negative number of sensors");
    }
    for (int i = 0; i < n; ++i) {
      sensors.add(template.createClone());
    }
  }

  /**
   * Add an actuator to the node.
   *
   * @param actuator The actuator to add
   */
  public void addActuator(Actuator actuator) {
    actuators.add(actuator);
    System.out.println("Created " + actuator.getType() + "[" + actuator.getId() + "] on node " + id);
  }

  /**
   * Register a new listener for sensor updates.
   *
   * @param listener The listener which will get notified every time sensor values change.
   */
  public void addSensorListener(SensorListener listener) {
    if (!sensorListeners.contains(listener)) {
      sensorListeners.add(listener);
    }
  }

  /**
   * Register a new listener for actuator updates.
   *
   * @param listener The listener which will get notified every time actuator state changes.
   */
  public void addActuatorListener(ActuatorListener listener) {
    if (!actuatorListeners.contains(listener)) {
      actuatorListeners.add(listener);
    }
  }

  /**
   * Register a new listener for node state updates.
   *
   * @param listener The listener which will get notified when the state of this node changes.
   */
  public void addStateListener(NodeStateListener listener) {
    if (!stateListeners.contains(listener)) {
      stateListeners.add(listener);
    }
  }

  /**
   * Stop simulating the sensor node's operation.
   */
  public void stop() {
    if (running) {
      System.out.println("-- Stopping simulation of node " + id);
      stopPeriodicSensorReading();
      running = false;
      disconnectFromServer();
    }
  }

  /**
   * Check whether the node is currently running.
   *
   * @return True if it is in a running-state, false otherwise
   */
  public boolean isRunning() {
    return running;
  }

  private void startPeriodicSensorReading() {
    sensorReadingTimer = new Timer();
    TimerTask newSensorValueTask = new TimerTask() {
      @Override
      public void run() {
        generateNewSensorValues();
      }
    };
    long randomStartDelay = random.nextLong(SENSING_DELAY);
    sensorReadingTimer.scheduleAtFixedRate(newSensorValueTask, randomStartDelay, SENSING_DELAY);
  }

  private void stopPeriodicSensorReading() {
    if (sensorReadingTimer != null) {
      sensorReadingTimer.cancel();
    }
  }

  /**
   * Generate new sensor values and send a notification to all listeners.
   */
  public void generateNewSensorValues() {
    addRandomNoiseToSensors();
    notifySensorChanges();
    debugPrint();
    for (Sensor sensor : sensors) {
      sendMessage(sensor.getReading().readingToString(id, sensor.getSensorId()));
    }
  }

  private void addRandomNoiseToSensors() {
    for (Sensor sensor : sensors) {
      sensor.addRandomNoise();
    }
  }

  private void debugPrint() {
    for (Sensor sensor : sensors) {
      System.out.println(" " + sensor.getReading().getFormatted());
    }
    //actuators.debugPrint();
  }

  /**
   * Toggle an actuator attached to this device.
   *
   * @param actuatorId The ID of the actuator to toggle
   * @throws IllegalArgumentException If no actuator with given configuration is found on this node
   */
  public void toggleActuator(int actuatorId) {
    Actuator actuator = getActuator(actuatorId);
    if (actuator == null) {
      throw new IllegalArgumentException("actuator[" + actuatorId + "] not found on node " + id);
    }
    actuator.toggle();
  }

  private void notifySensorChanges() {
    for (SensorListener listener : sensorListeners) {
      listener.sensorsUpdated(sensors);
    }
  }



  /**
   * An actuator has been turned on or off. Apply an impact from it to all sensors of given type.
   *
   * @param sensorType The type of sensors affected
   * @param impact     The impact to apply
   */
  public void applyActuatorImpact(String sensorType, double impact) {
    for (Sensor sensor : sensors) {
      if (sensor.getType().equals(sensorType)) {
        sensor.applyImpact(impact);
      }
    }
  }

  /**
   * Returns all the actuators available on the node.
   *
   * @return collection of the actuators
   */
  public ActuatorCollection getActuators() {
    return this.actuators;
  }

  /**
   * Set an actuator to a desired state.
   *
   * @param actuatorId ID of the actuator to set.
   * @param on         Whether it should be on (true) or off (false)
   */
  public void setActuator(int actuatorId, boolean on) {
    Actuator actuator = getActuator(actuatorId);
    if (actuator != null) {
      actuator.setState(on);
    }
  }

  /**
   * Set all actuators to desired state.
   *
   * @param on Whether the actuators should be on (true) or off (false)
   */
  public void setAllActuators(boolean on) {
    for (Actuator actuator : actuators) {
      actuator.setState(on);
    }
  }

  public Actuator getActuator(int actuatorId) {
    return actuators.get(actuatorId);
  }

  /**
   * Get all the sensors available on the device.
   *
   * @return List of all the sensors
   */
  public List<Sensor> getSensors() {
    return sensors;
  }

  /**
   * Get the unique ID of the node.
   *
   * @return the ID
   */
  public int getId() {
    return id;
  }


}