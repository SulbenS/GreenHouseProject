package no.ntnu.tools;

import no.ntnu.commands.ActuatorCommand;
import no.ntnu.commands.Data;
import no.ntnu.commands.NodeCommand;
import no.ntnu.commands.SensorReadingMessage;

import java.util.HashMap;
import java.util.Map;


public class MessageSerializer {

  /**
   * Serialize: Convert a ActuatorCommand object into a raw string
   *
   * @param actuatorCommand ActuatorCommand object to serialize
   * @return Raw string representation of the ActuatorCommand object
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
   * Deserialize: Convert a raw string into a Data object.
   *
   * @param rawMessage Raw string to deserialize.
   * @return ActuatorCommand object parsed from the raw string.
   */
  public static ActuatorCommand deserializeActuatorCommand(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    int nodeId = Integer.parseInt(fields.get("Node"));
    String action = fields.get("Action");
    String data = fields.get("Data");

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
   * Serialize: Convert a NodeCommand object into a raw string
   *
   * @param actuatorCommand NodeCommand object to serialize
   * @return Raw string representation of the NodeCommand object
   */
  public static String serializeNodeCommand(NodeCommand actuatorCommand) {
    return "Node=" + actuatorCommand.getNodeId() + ";" + "Action=" + actuatorCommand.getAction();
  }

  public static NodeCommand deserializeNodeCommand(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    int nodeId = Integer.parseInt(fields.get("Node"));
    String action = fields.get("Action");
    String data = fields.get("Data");
    return new NodeCommand(data, nodeId, action);
  }

  public static SensorReadingMessage deserializeSensorReadingMessage(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    int nodeId = Integer.parseInt(fields.get("Node"));
    String reading = fields.get("Reading");
    String data = fields.get("Data");
    return new SensorReadingMessage(data, nodeId, reading);
  }

  /**
   * Parse the fields of a raw message into a map
   *
   * @param rawMessage Raw message to parse
   * @return Map of fields in the raw message
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
    return fields;
  }

  /**
   * Returns the data type of raw message.
   *
   * @param rawMessage to get the data type of.
   * @return the data type of the raw message.
   */
  public static Data getDataType(String rawMessage) {
    Data result;
    Map<String, String> fields = parseFields(rawMessage);
    String dataType = fields.get("Data");
    result = switch (dataType) {
      case "Reading" -> deserializeSensorReadingMessage(rawMessage);
      case "ActuatorCommand" -> deserializeActuatorCommand(rawMessage);
      case "NodeCommand" -> deserializeNodeCommand(rawMessage);
      default -> throw new IllegalArgumentException("Could not get the data type.");
    };
    return result;
  }
}