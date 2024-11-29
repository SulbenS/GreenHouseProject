package no.ntnu.client;

/**
 * Interface for handling updates received by the client.
 */
public interface ClientListener {
  void onUpdateReceived(String update);
}
