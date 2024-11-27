package no.ntnu.tools;

import no.ntnu.commands.Command;

public class MessageSerializerTest {
  public static void main(String[] args) {
    // Test serialization for a specific actuator
    Command command1 = new Command(7, 3, "On");
    String serialized1 = MessageSerializer.serialize(command1);
    System.out.println("Serialized (specific actuator): " + serialized1);

    // Deserialize the serialized string back into a Command object
    Command deserialized1 = MessageSerializer.deserialize(serialized1);
    System.out.println("Deserialized Node ID: " + deserialized1.getNodeId());
    System.out.println("Deserialized Actuator ID: " + deserialized1.getActuatorId());
    System.out.println("Deserialized Action: " + deserialized1.getAction());

    // Test serialization for an actuator type
    Command command2 = new Command(7, "Fan", "Off");
    String serialized2 = MessageSerializer.serialize(command2);
    System.out.println("Serialized (actuator type): " + serialized2);

    // Deserialize the serialized string back into a Command object
    Command deserialized2 = MessageSerializer.deserialize(serialized2);
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