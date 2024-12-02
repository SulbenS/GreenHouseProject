package no.ntnu.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class EncryptionUtils {

  public static PublicKey stringToPublicKey(String key) throws Exception {
    byte[] byteKey = Base64.getDecoder().decode(key);
    X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePublic(X509publicKey);
  }

  public static SecretKey generateAESKey() throws Exception {
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(128); // 128-bit AES
    return keyGen.generateKey();
  }

  public static String encryptWithPublicKey(SecretKey aesKey, PublicKey publicKey) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] encryptedBytes = cipher.doFinal(aesKey.getEncoded());
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  public static SecretKey decryptAESKeyWithPrivateKey(String encryptedAESKey, PrivateKey privateKey) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    byte[] decodedBytes = Base64.getDecoder().decode(encryptedAESKey);
    byte[] decryptedKeyBytes = cipher.doFinal(decodedBytes);
    return new SecretKeySpec(decryptedKeyBytes, 0, decryptedKeyBytes.length, "AES");
  }

  public static String encryptWithAES(String data, SecretKey secretKey) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    byte[] encryptedBytes = cipher.doFinal(data.getBytes());
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  public static String decryptWithAES(String data, SecretKey secretKey) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    byte[] decodedBytes = Base64.getDecoder().decode(data);
    return new String(cipher.doFinal(decodedBytes));
  }
}
