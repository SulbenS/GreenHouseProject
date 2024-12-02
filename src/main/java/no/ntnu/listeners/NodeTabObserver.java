package no.ntnu.listeners;

/**
 * Interface for the NodeTabObserver.
 */
public interface NodeTabObserver {

  void onActuatorAddedInGui(int nodeId, String actuatorType);

  void onSensorAddedInGui(int nodeId, String sensorType);

  void onNodeAddedInGui(int nodeId);
}

