package no.ntnu.tools;

import java.util.HashMap;
import java.util.Map;
import no.ntnu.commands.ActuatorAddedInGui;
import no.ntnu.commands.ActuatorCommand;
import no.ntnu.commands.ActuatorIdentifier;
import no.ntnu.commands.Data;
import no.ntnu.commands.NodeCommand;
import no.ntnu.commands.NodeIdentifier;
import no.ntnu.commands.SensorAddedInGui;
import no.ntnu.commands.SensorIdentifier;
import no.ntnu.commands.SensorReadingMessage;
import no.ntnu.node.Actuator;
import no.ntnu.node.Sensor;

/**
 * A class for handling messages.
 */
public class MessageHandler {

  /**
   * Serialize the sensor reading message.
   *
   * @param actuator The sensor reading message to serialize.
   * @return The serialized sensor reading message.
   */
  public static String serializeActuatorInformation(ActuatorIdentifier actuator) {
    return "Data=Identifier;Node=" + actuator.getNodeId()
            + ";Actuator=" + actuator.getActuatorId()
            + ";Type=" + actuator.getType()
            + ";State=" + actuator.getState();
  }

  /**
   * Deserialize the actuator information.
   *
   * @param rawMessage The raw message to deserialize.
   * @return The actuator information.
   */
  public static ActuatorIdentifier deserializeActuatorInformation(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    String dataType = fields.get("Data");
    int nodeId = Integer.parseInt(fields.get("Node"));
    String actuatorType = fields.get("Type");
    int actuatorId = Integer.parseInt(fields.get("Actuator"));
    boolean state = Boolean.parseBoolean(fields.get("State"));
    return new ActuatorIdentifier(dataType, nodeId, actuatorType, actuatorId, state);
  }

  /**
   * Serialize the sensor information.
   *
   * @param sensor The sensor to serialize.
   * @return The serialized sensor information.
   */
  public static String serializeSensorInformation(SensorIdentifier sensor) {
    return "Data=Identifier;Node=" + sensor.getNodeId()
            + ";Sensor=" + sensor.getSensorId()
            + ";Type=" + sensor.getType();
  }

  /**
   * Deserialize the sensor information.
   *
   * @param rawMessage The raw message to deserialize.
   * @return The sensor information.
   */
  public static SensorIdentifier deserializeSensorInformation(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    int nodeId = Integer.parseInt(fields.get("Node"));
    int sensorId = Integer.parseInt(fields.get("Sensor"));
    String sensorType = fields.get("Type");
    String dataType = fields.get("Data");
    return new SensorIdentifier(dataType, nodeId, sensorType, sensorId);
  }

  /**
   * Serialize the actuator command.
   *
   * @param actuatorCommand The actuator command to serialize.
   * @return The serialized actuator command.
   */
  public static String serializeActuatorCommand(ActuatorCommand actuatorCommand) {
    StringBuilder builder = new StringBuilder("Data=ActuatorCommand;");
    builder.append("Node=").append(actuatorCommand.getNodeId()).append(";");
    if (actuatorCommand.getActuatorType() != null) {
      builder.append("ActuatorType=").append(actuatorCommand.getActuatorType()).append(";");
    } else if (actuatorCommand.getActuatorId() != 0) {
      builder.append("Actuator=").append(actuatorCommand.getActuatorId()).append(";");
    }
    builder.append("Action=").append(actuatorCommand.getAction());
    return builder.toString();
  }

  /**
   * Deserialize the actuator command.
   *
   * @param rawMessage The raw message to deserialize.
   * @return The actuator command.
   */
  public static ActuatorCommand deserializeActuatorCommand(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    String data = fields.get("Data");
    int nodeId = Integer.parseInt(fields.get("Node"));
    String action = fields.get("Action");

    if (fields.containsKey("Actuator")) {
      int actuatorId = Integer.parseInt(fields.get("Actuator"));
      return new ActuatorCommand(data, nodeId, actuatorId, action);
    } else if (fields.containsKey("ActuatorType")) {
      String actuatorType = fields.get("ActuatorType");
      return new ActuatorCommand(data, nodeId, actuatorType, action);
    }
    throw new IllegalArgumentException("Invalid ActuatorCommand format");
  }

  /**
   * Serialize the node command.
   *
   * @param nodeCommand The node command to serialize.
   * @return The serialized node command.
   */
  public static String serializeNodeCommand(NodeCommand nodeCommand) {
    return "Data=NodeCommand;Node=" + nodeCommand.getNodeId() + ";Action="
            + nodeCommand.getAction();
  }

  /**
   * Deserialize the node command.
   *
   * @param rawMessage The raw message to deserialize.
   * @return The node command.
   */
  public static NodeCommand deserializeNodeCommand(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    String data = fields.get("Data");
    int nodeId = Integer.parseInt(fields.get("Node"));
    String action = fields.get("Action");
    return new NodeCommand(data, nodeId, action);
  }

  /**
   * Serialize the sensor reading message.
   *
   * @param rawMessage The sensor reading message to serialize.
   * @return The serialized sensor reading message.
   */
  public static SensorReadingMessage deserializeSensorReadingMessage(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    String data = fields.get("Data");
    int nodeId = Integer.parseInt(fields.get("Node"));
    int sensorId = Integer.parseInt(fields.get("Sensor"));
    String type = fields.get("Type");
    String value = fields.get("Value");
    String unit = fields.get("Unit");
    return new SensorReadingMessage(data, nodeId, sensorId, type, value, unit);
  }

  /**
   * Deserialize the sensor reading message.
   *
   * @param rawMessage The sensor reading message to serialize.
   * @return The serialized sensor reading message.
   */
  public static Data deserializeNodeInformation(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    int nodeId = Integer.parseInt(fields.get("Node"));
    String dataType = fields.get("Data");
    return new NodeIdentifier(dataType, nodeId);
  }

  /**
   * Serialize the sensor reading message.
   *
   * @param actuatorAddedInGui The sensor reading message to serialize.
   * @return The serialized sensor reading message.
   */
  public static String serializeActuatorAddedInGui(ActuatorAddedInGui actuatorAddedInGui) {
    return "Data=ActuatorAddedInGui;Node="
            + actuatorAddedInGui.getNodeId()
            + ";ActuatorType="
            + actuatorAddedInGui.getActuatorType();
  }

  /**
   * Serialize the sensor reading message.
   *
   * @param sensorAddedInGui The sensor reading message to serialize.
   * @return The serialized sensor reading message.
   */
  public static String serializeSensorAddedInGui(SensorAddedInGui sensorAddedInGui) {
    return "Data=SensorAddedInGui;Node="
            + sensorAddedInGui.getNodeId()
            + ";SensorType="
            + sensorAddedInGui.getSensorType();
  }

  /**
   * Serialize the sensor reading message.
   *
   * @param rawMessage The sensor reading message to serialize.
   * @return The serialized sensor reading message.
   */
  public static ActuatorAddedInGui deserializeActuatorAddedInGui(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    int nodeId = Integer.parseInt(fields.get("Node"));
    String actuatorType = fields.get("ActuatorType");
    String dataType = fields.get("Data");
    return new ActuatorAddedInGui(dataType, nodeId, actuatorType);
  }

  /**
   * Deserialize the sensor reading message.
   *
   * @param rawMessage The sensor reading message to serialize.
   * @return The serialized sensor reading message.
   */
  public static SensorAddedInGui deserializeSensorAddedInGui(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    int nodeId = Integer.parseInt(fields.get("Node"));
    String sensorType = fields.get("SensorType");
    String dataType = fields.get("Data");
    return new SensorAddedInGui(dataType, nodeId, sensorType);
  }

  /**
   * Serialize the sensor reading message.
   *
   * @param actuator The sensor reading message to serialize.
   * @return The serialized sensor reading message.
   */
  public static String actuatorToString(Actuator actuator) {
    return "Data=Identifier;Node=" + actuator.getNodeId()
            + ";Actuator=" + actuator.getId()
            + ";Type=" + actuator.getType()
            + ";State=" + actuator.isOn();
  }

  /**
   * Serialize the sensor reading message.
   *
   * @param sensor The sensor reading message to serialize.
   * @return The serialized sensor reading message.
   */
  public static String sensorToString(Sensor sensor) {
    return "Data=Identifier;Node=" + sensor.getNodeId()
            + ";Sensor=" + sensor.getSensorId()
            + ";Type=" + sensor.getType();
  }

  /**
   * Serialize the sensor reading message.
   *
   * @param rawMessage The sensor reading message to serialize.
   * @return The serialized sensor reading message.
   */
  private static Map<String, String> parseFields(String rawMessage) {
    Map<String, String> fields = new HashMap<>();
    String[] parts = rawMessage.split(";");
    for (String part : parts) {
      String[] keyValue = part.split("=");
      if (keyValue.length == 2) {
        fields.put(keyValue[0], keyValue[1]);
      } else {
        throw new IllegalArgumentException("Invalid key-value pair: " + part);
      }
    }
    if (fields.isEmpty()) {
      throw new IllegalArgumentException("Could not parse fields.");
    }
    return fields;
  }

  /**
   * Deserialize the sensor reading message.
   *
   * @param rawMessage The sensor reading message to serialize.
   * @return The serialized sensor reading message.
   */
  public static Data getData(String rawMessage) {
    Data result;
    Map<String, String> fields = parseFields(rawMessage);
    String dataType = fields.get("Data");
    if (rawMessage.isBlank()) {
      throw new IllegalArgumentException("rawMessage is null");
    }
    if (dataType == null) {
      System.out.println(rawMessage);
      throw new IllegalArgumentException("Could not get the data type.");
    }
    try {
      result = switch (dataType) {
        case "Reading" -> deserializeSensorReadingMessage(rawMessage);
        case "ActuatorCommand" -> deserializeActuatorCommand(rawMessage);
        case "NodeCommand" -> deserializeNodeCommand(rawMessage);
        case "ActuatorAddedInGui" -> deserializeActuatorAddedInGui(rawMessage);
        case "SensorAddedInGui" -> deserializeSensorAddedInGui(rawMessage);
        case "Identifier" -> {
          if (fields.containsKey("Actuator")) {
            yield deserializeActuatorInformation(rawMessage);
          } else if (fields.containsKey("Sensor")) {
            yield deserializeSensorInformation(rawMessage);
          } else if (fields.containsKey("Node")) {
            yield deserializeNodeInformation(rawMessage);
          } else {
            throw new IllegalArgumentException("Could not get the data type.");
          }
        }
        default -> throw new IllegalArgumentException("Could not get the data type.");
      };
      return result;
    } catch (NumberFormatException e) {
      System.out.println(rawMessage);
      e.printStackTrace();
    }
    System.out.println(rawMessage);
    throw new IllegalArgumentException("Could not get the data type.");
  }
}
