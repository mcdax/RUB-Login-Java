import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Crypter {

	private byte[] keyBytes = "L!T$g6z4".getBytes();
	private byte[] ivBytes= "p.3Ffg§!".getBytes();
	
	public Crypter(byte[] keyBytes, byte[] ivBytes)
	{
		this.keyBytes = keyBytes;
		this.ivBytes = ivBytes;
	}
	
	public byte[] encrypt(byte[] decrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException
	{
		byte[] input = decrypted;
		// wrap key data in Key/IV specs to pass to cipher
		SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		// create the cipher with the algorithm you choose
		// see javadoc for Cipher class for more info, e.g.
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		byte[] encrypted= new byte[cipher.getOutputSize(input.length)];
		int enc_len = cipher.update(input, 0, input.length, encrypted, 0);
		enc_len += cipher.doFinal(encrypted, enc_len);
		
		return encrypted;
	}

	public byte[] decrypt(byte[] encrypted, int len) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException
	{
		// wrap key data in Key/IV specs to pass to cipher
		SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		// create the cipher with the algorithm you choose
		// see javadoc for Cipher class for more info, e.g.
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		byte[] decrypted = new byte[cipher.getOutputSize(len)];
		int dec_len = cipher.update(encrypted, 0, len, decrypted, 0);
		dec_len += cipher.doFinal(decrypted, dec_len);
		
		
		return decrypted;
	}
	
}
