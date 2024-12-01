import no.ntnu.client.Client;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

  private ServerSocket serverSocket;
  private ExecutorService serverExecutor;

  @BeforeEach
  void setUp() throws IOException {
    serverSocket = new ServerSocket(12345); // Mock server port
    serverExecutor = Executors.newCachedThreadPool();
  }

  @AfterEach
  void tearDown() throws IOException {
    serverExecutor.shutdownNow();
    if (serverSocket != null && !serverSocket.isClosed()) {
      serverSocket.close();
    }
  }

  @Test
  void testSuccessfulConnection() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);

    // Start a mock server
    serverExecutor.submit(() -> {
      try (Socket clientSocket = serverSocket.accept()) {
        latch.countDown(); // Notify test that the client connected
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String message = in.readLine();
        assertEquals("PING", message); // Check heartbeat
      } catch (IOException e) {
        fail("Server error: " + e.getMessage());
      }
    });

    // Create the client
    Client client = new Client("127.0.0.1", 12345);

    // Wait for the client to connect
    assertTrue(latch.await(5, TimeUnit.SECONDS), "Client failed to connect to the server");
  }

  @Test
  void testBufferedCommands() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);

    // Start a mock server
    serverExecutor.submit(() -> {
      try (Socket clientSocket = serverSocket.accept()) {
        latch.countDown(); // Notify test that the client connected
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Simulate server responding to buffered commands
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String message;
        while ((message = in.readLine()) != null) {
          if (message.equals("COMMAND|1|heater=on")) {
            out.println("ACK|heater=on");
          }
        }
      } catch (IOException e) {
        fail("Server error: " + e.getMessage());
      }
    });

    // Create the client
    Client client = new Client("127.0.0.1", 12345);

    // Simulate disconnection
    client.sendCommand("COMMAND|1|heater=on"); // Should buffer the command

    // Reconnect and verify the buffered command was sent
    assertTrue(latch.await(5, TimeUnit.SECONDS), "Client failed to connect to the server");
  }

  @Test
  void testReconnection() throws Exception {
    CountDownLatch reconnectLatch = new CountDownLatch(1);

    // Start a mock server
    serverExecutor.submit(() -> {
      try (Socket clientSocket = serverSocket.accept()) {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Simulate server disconnecting and reconnecting
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String message = in.readLine();
        assertEquals("PING", message); // Initial heartbeat

        // Close the connection to simulate server failure
        clientSocket.close();

        // Restart the server to simulate reconnection
        reconnectLatch.await(5, TimeUnit.SECONDS);
        try (Socket newClientSocket = serverSocket.accept()) {
          BufferedReader newIn = new BufferedReader(new InputStreamReader(newClientSocket.getInputStream()));
          assertEquals("PING", newIn.readLine()); // Heartbeat after reconnection
        }
      } catch (IOException | InterruptedException e) {
        fail("Server error: " + e.getMessage());
      }
    });

    // Create the client
    Client client = new Client("127.0.0.1", 12345);

    // Wait for reconnection
    reconnectLatch.countDown();
  }

  @Test
  void testUpdateListener() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);

    // Start a mock server
    serverExecutor.submit(() -> {
      try (Socket clientSocket = serverSocket.accept()) {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Simulate server sending updates
        out.println("UPDATE|nodeId=1|temperature=22.5");
      } catch (IOException e) {
        fail("Server error: " + e.getMessage());
      }
    });

    // Create the client
    Client client = new Client("127.0.0.1", 12345);

    // Set a listener to process the update
    client.setListener(update -> {
      assertTrue(update.contains("temperature=22.5"), "Update listener did not receive correct data");
      latch.countDown();
    });

    // Wait for the listener to receive the update
    assertTrue(latch.await(5, TimeUnit.SECONDS), "Update listener did not receive the update");
  }
}
