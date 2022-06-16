package com.cognizant.encryption;


import com.cognizant.utility.AppUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

/**
 * com.cognizant.encryption.EncryptionAesUtil.java
 * this java class hasn't been used in this tool yet
 * @author Debdatta Porya
 */
public class EncryptionAesUtil {

    private static final Logger LOGGER = Logger.getLogger(EncryptionAesUtil.class);

    private static AppUtil.OSType detectedOS;

    // Support single text encryption and decryption

    private static Cipher cipher;
    private byte[] cipherKey = null;

    // 16 bytes was used for 128 bit AES encryption
    public static final int ENCRYPTION_KEY_LENGTH_IN_BYTES = 16;
    public static final int IV_LENGTH_IN_BYTES = 16;

    public static final String DEFAULT_KEYFILE_NAME = "dataLoader.key";

    static {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //Commented by Debdatta 
            //detectedOS = AppUtil.getOSType();

        } catch (Exception e) {
            LOGGER.error("Fail to initialize encryption: " + e.getMessage());
            throw new RuntimeException("Fail to initialize encryption:  ", e);
        }
    }


    private byte[] extractIvBytes(byte[] cipheredText) {
        byte[] iv = new byte[IV_LENGTH_IN_BYTES];
        System.arraycopy(cipheredText, 0, iv, 0, IV_LENGTH_IN_BYTES);
        return iv;
    }

    private byte[] extractCipherContent(byte[] cipheredText) {
        byte[] cipheredPassword = new byte[cipheredText.length - IV_LENGTH_IN_BYTES];
        System.arraycopy(cipheredText, IV_LENGTH_IN_BYTES, cipheredPassword, 0, cipheredText.length - IV_LENGTH_IN_BYTES);
        return cipheredPassword;

    }

    private byte[] concatenateByteArray(byte[] a, byte[] b) {

        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public byte[] generateEncryptionKey() {
        // The secure random uses threadId and tick to make sure it is not repeatable
        // If a call to setSeed had not occurred previously,
        // the first call to this method forces this SecureRandom object to seed itself.
        // This self-seeding will not occur if setSeed was previously called.
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[ENCRYPTION_KEY_LENGTH_IN_BYTES];
        random.nextBytes(bytes);
        return bytes;
    }

    public byte[] generateIv() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[IV_LENGTH_IN_BYTES];
        random.nextBytes(bytes);
        return bytes;
    }
    
    public String createUserProfileKeyName() {
        String path = Paths.get(System.getProperty("user.home"), ".dataloader").toString();
        File customDir = new File(path);

        if (customDir.exists()) {
            LOGGER.debug(customDir + " exists");
        } else if (customDir.mkdirs()) {
            if (detectedOS == AppUtil.OSType.MACOSX || detectedOS == AppUtil.OSType.LINUX) {
                // set all reading to false
                customDir.setReadable(false, false);
                // only owner can read
                customDir.setReadable(true, true);
                customDir.setExecutable(false, false);
                customDir.setExecutable(true, true);
            }
            LOGGER.info(customDir + " was created");

        } else {
            LOGGER.error(customDir + " was not created");
            throw new RuntimeException("Cannot create directory:" + path);
        }
        return Paths.get(path, DEFAULT_KEYFILE_NAME).toString();
    }
    // If given path not existing, create one.
    public String createKeyFileIfNotExisting(String filePath) throws GeneralSecurityException {
        if (filePath == null || filePath.isEmpty() || !Files.exists(Paths.get(filePath))) {
            // no valid file path provided, check if default one exists
            if (filePath == null || filePath.isEmpty()) {
                filePath = createUserProfileKeyName();
            }
            if (!Files.exists(Paths.get(filePath))) {
                byte[] key = generateEncryptionKey();
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    fos.write(key);
                } catch (IOException io) {
                    throw new GeneralSecurityException("Failed to open file:" + filePath, io);
                }
                // Windows platform is already readable only to owner.
                if (detectedOS == AppUtil.OSType.MACOSX || detectedOS == AppUtil.OSType.LINUX) {
                    File file = new File(filePath);
                    // set all reading to false
                    file.setReadable(false, false);
                    // set Owner reading to true
                    file.setReadable(true, true);
                }
            }
        }
        setCipherKeyFromFilePath(filePath);
        return filePath;
    }

    public void resetCipherKey() {
        cipherKey = null;
    }

    public void setCipherKeyFromFilePath(String filePath) throws GeneralSecurityException {
        byte[] data = new byte[1024];
        try (FileInputStream fis = new FileInputStream(filePath)) {
            int size = fis.read(data);
            if (size < ENCRYPTION_KEY_LENGTH_IN_BYTES)
                throw new GeneralSecurityException("Keyfile content is too short:" + filePath);
            else {
                cipherKey = new byte[ENCRYPTION_KEY_LENGTH_IN_BYTES];
                System.arraycopy(data, 0, cipherKey, 0, ENCRYPTION_KEY_LENGTH_IN_BYTES);
            }
        } catch (IOException io) {
            throw new GeneralSecurityException("Failed to open file: " + filePath, io);
        }
    }
    
    private void ensureKeyIsSet() throws GeneralSecurityException {
        if (cipherKey != null) {
            return;
        }
        // if no key was set up already, create one
        createKeyFileIfNotExisting(null);

    }

    public String encryptMsg(String msg) throws GeneralSecurityException {
        try {
            ensureKeyIsSet();
            return EncryptionUtil.bytesToText(encryptMsg(msg, cipherKey));
        } catch (Exception e) {
            LOGGER.error("Fail to encrypt message: " + e.getMessage());
            throw new GeneralSecurityException("Error to encrypt message: ", e);
        }
    }

    public String decryptMsg(String cipherMsgString) throws GeneralSecurityException {
        try {
            ensureKeyIsSet();
            byte[] cipherMsg = EncryptionUtil.textToBytes(cipherMsgString);
            return decryptMsg(cipherMsg, cipherKey);
        } catch (Exception e) {
            LOGGER.error("Fail to decrypt message: " + e.getMessage());
            throw new GeneralSecurityException("Error to decrypt message: ", e);
        }
    }

    public byte[] encryptMsg(String msg, byte[] encryptionKey) throws GeneralSecurityException {

        if (encryptionKey == null || encryptionKey.length != ENCRYPTION_KEY_LENGTH_IN_BYTES)
            throw new GeneralSecurityException("Encryption key is null or has invalid length");
        SecretKeySpec key = new SecretKeySpec(encryptionKey, "AES");
        byte[] ivBytes = generateIv();
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(msg.getBytes());
        return concatenateByteArray(ivBytes, cipherText);
    }

    public String decryptMsg(byte[] cipherMsg, byte[] encryptionKey) throws GeneralSecurityException {
        if (encryptionKey == null || encryptionKey.length != ENCRYPTION_KEY_LENGTH_IN_BYTES)
            throw new GeneralSecurityException("Encryption key is null or has invalid length");
        SecretKeySpec key = new SecretKeySpec(encryptionKey, "AES");
        byte[] iv = extractIvBytes(cipherMsg);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] cipherContent = extractCipherContent(cipherMsg);
        return new String(cipher.doFinal(cipherContent));
    }
}
