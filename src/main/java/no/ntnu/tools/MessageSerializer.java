package no.ntnu.tools;

import no.ntnu.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class MessageSerializer {

  // Serialize: Convert a Command object into a string
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

  // Deserialize: Convert a raw string into a Command object
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

  // Utility to parse key-value pairs from a raw message
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