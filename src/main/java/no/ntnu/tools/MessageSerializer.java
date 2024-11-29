package no.ntnu.tools;

import java.util.HashMap;
import java.util.Map;

public class MessageSerializer {

  /**
   * Serialize: Convert a Command object into a raw string
   *
   * @param command Command object to serialize
   * @return Raw string representation of the Command object
   */
  public static String serialize(Command command) {
    StringBuilder builder = new StringBuilder("Node=" + command.getNodeId() + ";");
    if (command.getActuatorType() != null) {
      builder.append("ActuatorType=").append(command.getActuatorType()).append(";");
    } else if (command.getActuatorId() != 0) {
      builder.append("Actuator=").append(command.getActuatorId()).append(";");
    }
    builder.append("Action=").append(command.getAction());
    return builder.toString();
  }

  /**
   * Deserialize: Convert a raw string into a Command object
   *
   * @param rawMessage Raw string to deserialize
   * @return Command object parsed from the raw string
   */
  public static Command deserialize(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    int nodeId = Integer.parseInt(fields.get("Node"));
    String action = fields.get("Action");

    if (fields.containsKey("Actuator")) {
      int actuatorId = Integer.parseInt(fields.get("Actuator"));
      return new Command(nodeId, actuatorId, action);
    } else if (fields.containsKey("ActuatorType")) {
      String actuatorType = fields.get("ActuatorType");
      return new Command(nodeId, actuatorType, action);
    }

    throw new IllegalArgumentException("Invalid Command format");
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