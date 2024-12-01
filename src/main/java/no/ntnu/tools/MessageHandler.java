package no.ntnu.tools;

import no.ntnu.commands.*;
import no.ntnu.node.Actuator;
import no.ntnu.node.Sensor;
import java.util.HashMap;
import java.util.Map;

public class MessageHandler {

  public static String serializeActuatorInformation(ActuatorIdentifier actuator) {
    return "Data=Identifier;Node=" + actuator.getNodeId()
            + ";Actuator=" + actuator.getActuatorId()
            + ";Type=" + actuator.getType();
  }

  public static Data deserializeActuatorInformation(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    int nodeId = Integer.parseInt(fields.get("Node"));
    int actuatorId = Integer.parseInt(fields.get("Actuator"));
    String actuatorType = fields.get("Type");
    String dataType = fields.get("Data");
    return new ActuatorIdentifier(dataType, nodeId, actuatorType, actuatorId);
  }

  public static String serializeSensorInformation(SensorIdentifier sensor) {
    return "Data=Identifier;Node=" + sensor.getNodeId()
            + ";Sensor=" + sensor.getSensorId()
            + ";Type=" + sensor.getType();
  }

  public static Data deserializeSensorInformation(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    int nodeId = Integer.parseInt(fields.get("Node"));
    int sensorId = Integer.parseInt(fields.get("Sensor"));
    String sensorType = fields.get("Type");
    String dataType = fields.get("Data");
    return new SensorIdentifier(dataType, nodeId, sensorType, sensorId);
  }

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

  public static String serializeNodeCommand(NodeCommand nodeCommand) {
    return "Data=NodeCommand;Node=" + nodeCommand.getNodeId() + ";Action=" + nodeCommand.getAction();
  }

  public static NodeCommand deserializeNodeCommand(String rawMessage) {
    Map<String, String> fields = parseFields(rawMessage);
    String data = fields.get("Data");
    int nodeId = Integer.parseInt(fields.get("Node"));
    String action = fields.get("Action");
    return new NodeCommand(data, nodeId, action);
  }

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

  public static String actuatorToString(Actuator actuator) {
    return "Data=Identifier;Node=" + actuator.getNodeId()
            + ";Actuator=" + actuator.getId()
            + ";Type=" + actuator.getType();
  }

  public static String sensorToString(Sensor sensor) {
    return "Data=Identifier;Node=" + sensor.getNodeId()
            + ";Sensor=" + sensor.getSensorId()
            + ";Type=" + sensor.getType();
  }

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
        case "Identifier" -> {
          if (fields.containsKey("Actuator")) {
            yield deserializeActuatorInformation(rawMessage);
          } else if (fields.containsKey("Sensor")) {
            yield deserializeSensorInformation(rawMessage);
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
