package no.ntnu.tools;

public class MessageSerializerTest {
  public static void main(String[] args) {
    // Test serialization for a specific actuator
    ActuatorCommand actuatorCommand1 = new ActuatorCommand(7, 3, "On");
    String serialized1 = MessageSerializer.serialize(actuatorCommand1);
    System.out.println("Serialized (specific actuator): " + serialized1);

    // Deserialize the serialized string back into a ActuatorCommand object
    ActuatorCommand deserialized1 = MessageSerializer.deserialize(serialized1);
    System.out.println("Deserialized Node ID: " + deserialized1.getNodeId());
    System.out.println("Deserialized Actuator ID: " + deserialized1.getActuatorId());
    System.out.println("Deserialized Action: " + deserialized1.getAction());

    // Test serialization for an actuator type
    ActuatorCommand actuatorCommand2 = new ActuatorCommand(7, "Fan", "Off");
    String serialized2 = MessageSerializer.serialize(actuatorCommand2);
    System.out.println("Serialized (actuator type): " + serialized2);

    // Deserialize the serialized string back into a ActuatorCommand object
    ActuatorCommand deserialized2 = MessageSerializer.deserialize(serialized2);
    System.out.println("Deserialized Node ID: " + deserialized2.getNodeId());
    System.out.println("Deserialized Actuator Type: " + deserialized2.getActuatorType());
    System.out.println("Deserialized Action: " + deserialized2.getAction());

    // Invalid format test
    try {
      String invalidMessage = "Node=7;Action=On";
      MessageSerializer.deserialize(invalidMessage);
    } catch (IllegalArgumentException e) {
      System.err.println("Expected error for invalid format: " + e.getMessage());
    }
  }
}