package no.ntnu.nodes;

import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SensorNode {
  private static final int SERVER_PORT = 12345;
  private final String nodeId;
  private final Queue<String> commandBuffer = new ConcurrentLinkedQueue<>();

  public SensorNode(String nodeId) {
    this.nodeId = nodeId;
  }

  public void start() {
    Random random = new Random();
    while (true) {
      String sensorData = String.format("SENSOR_DATA|%s|temperature=%.2f,humidity=%.2f",
          nodeId, 20 + random.nextDouble() * 10, 50 + random.nextDouble() * 20);
      try (Socket socket = new Socket("127.0.0.1", SERVER_PORT);
           PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
        // Send sensor data
        out.println(sensorData);
        System.out.println("Sent: " + sensorData);

        // Send buffered commands, if any
        while (!commandBuffer.isEmpty()) {
          String bufferedCommand = commandBuffer.poll();
          out.println(bufferedCommand);
          System.out.println("Sent buffered command: " + bufferedCommand);
        }
      } catch (IOException e) {
        System.err.println("Connection lost, buffering data...");
        commandBuffer.add(sensorData); // Buffer the data for retransmission
      }

      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.err.println("Sensor node interrupted");
        break;
      }
    }
  }

  public static void main(String[] args) {
    new SensorNode("1").start();
  }
}
