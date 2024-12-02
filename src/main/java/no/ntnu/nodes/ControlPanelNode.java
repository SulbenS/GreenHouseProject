package no.ntnu.nodes;

import no.ntnu.security.EncryptionUtils;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;

public class ControlPanelNode {
  private static final int SERVER_PORT = 12345;

  public void start() {
    try (Socket socket = new Socket("127.0.0.1", SERVER_PORT);
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

      // Receive server's public key
      String publicKeyString = in.readLine();
      PublicKey serverPublicKey = EncryptionUtils.stringToPublicKey(publicKeyString);

      // Generate AES key and send it encrypted with RSA
      SecretKey aesKey = EncryptionUtils.generateAESKey();
      String encryptedAESKey = EncryptionUtils.encryptWithRSA(
          EncryptionUtils.secretKeyToString(aesKey), serverPublicKey
      );
      out.println(encryptedAESKey);

      // Send encrypted command
      String command = "COMMAND|1|heater=on";
      String encryptedCommand = EncryptionUtils.encryptWithAES(command, aesKey);
      out.println(encryptedCommand);

      // Receive and decrypt response
      String encryptedResponse = in.readLine();
      String response = EncryptionUtils.decryptWithAES(encryptedResponse, aesKey);
      System.out.println("Received (decrypted): " + response);
    } catch (Exception e) {
      System.err.println("Control panel error: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    new ControlPanelNode().start();
  }
}
