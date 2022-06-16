package Test_sfdc_Restapi_Connect;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CommonUtils {
	private static SecretKeySpec secretKey;
	private static byte[] key;

	static {
		setKey();
	}

	public static void setKey() {
		String myKey = "33cb8b2a-76f6-4bcd-befe-b6135f4a694f";
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static String encrypt(String value) throws NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher desCipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		byte[] text = value.getBytes("UTF8");
		desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] textEncrypted = desCipher.doFinal(text);
		return new String(textEncrypted);
	}

	public static String decrypt(String value) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		Cipher desCipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		desCipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] textDecrypted = desCipher.doFinal(value.getBytes());
		return new String(textDecrypted);
	}

	public static void createOrUpdateUserDetails(String path, SystemArgs systemArgs)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		Properties props = new Properties();
		String encyptedClientId = encrypt(systemArgs.getClientId());
		props.put("ClientId", encyptedClientId);
		props.put("ClientSecret", encrypt(systemArgs.getClientSecret()));
		props.put("Password", encrypt(systemArgs.getPassword()));
		props.put("LoginUrl", systemArgs.getLoginUrl());

		path += "//" + systemArgs.getUserName() + ".prop";
		OutputStreamWriter outputStrem = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
		props.store(outputStrem, "Storing data into property file");
	}

	public static SystemArgs getUserDetails(String path) throws IOException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		SystemArgs systemArgs = new SystemArgs();
		InputStreamReader ip = new InputStreamReader(new FileInputStream(path), "UTF-8");
		Properties prop = new Properties();
		prop.load(ip);

		systemArgs.setClientId(decrypt(prop.getProperty("ClientId")));
		systemArgs.setClientSecret(decrypt(prop.getProperty("ClientSecret")));
		systemArgs.setLoginUrl(prop.getProperty("LoginUrl"));
		systemArgs.setPassword(decrypt(prop.getProperty("Password")));
		return systemArgs;
	}
}
