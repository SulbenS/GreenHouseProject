package no.ntnu.listeners;

public interface NodeTabObserver {

  void onActuatorAddedInGui(int nodeId, String actuatorType);

  void onSensorAddedInGui(int nodeId, String sensorType);
}

