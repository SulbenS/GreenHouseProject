package no.ntnu.tools;

import java.util.HashMap;
import java.util.Map;

public class MessageSerializer {

  /**
   * Serialize: Convert a ActuatorCommand object into a raw string
   *
   * @param actuatorCommand ActuatorCommand object to serialize
   * @return Raw string representation of the ActuatorCommand object
   */
  public static String serialize(ActuatorCommand actuatorCommand) {
    StringBuilder builder = new StringBuilder("Node=" + actuatorCommand.getNodeId() + ";");
    if (actuatorCommand.getActuatorType() != null) {
      builder.append("ActuatorType=").append(actuatorCommand.getActuatorType()).append(";");
    } else if (actuatorCommand.getActuatorId() != 0) {
      builder.append("Actuator=").append(actuatorCommand.getActuatorId()).append(";");
    }
    builder.append("Action=").append(actuatorCommand.getAction());
    return builder.toString();
  }

  /**
   * Serialize: Convert a NodeCommand object into a raw string
   *
   * @param actuatorCommand NodeCommand object to serialize
   * @return Raw string representation of the NodeCommand object
   */
  public static String serialize(NodeCommand actuatorCommand) {
    return "Node=" + actuatorCommand.getNodeId() + ";" + "Action=" + actuatorCommand.getAction();
  }

  /**
   * Deserialize: Convert a raw string into a ActuatorCommand object
   *
   * @param rawMessage Raw string to deserialize
   * @return ActuatorCommand object parsed from the raw string
   */
  public static ActuatorCommand deserialize(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    int nodeId = Integer.parseInt(fields.get("Node"));
    String action = fields.get("Action");

    if (fields.containsKey("Actuator")) {
      int actuatorId = Integer.parseInt(fields.get("Actuator"));
      return new ActuatorCommand(nodeId, actuatorId, action);
    } else if (fields.containsKey("ActuatorType")) {
      String actuatorType = fields.get("ActuatorType");
      return new ActuatorCommand(nodeId, actuatorType, action);
    }

    throw new IllegalArgumentException("Invalid ActuatorCommand format");
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
      }
    }
    return fields;
  }
}