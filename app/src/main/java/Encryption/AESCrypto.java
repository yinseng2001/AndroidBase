package Encryption;

/**
 * Created by yinseng on 9/4/16.
 */

import android.util.Base64;
import android.util.Log;

import java.security.*;
import java.security.spec.KeySpec;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class AESCrypto {
    private static final String CODES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    private static final String ALGO = "AES/CBC/PKCS5Padding";//"AES/CBC/PKCS5Padding";// "AES/CBC/ZeroBytePadding";
    private static final byte[] keyValue =
            new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't',
                    'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};


//    public static byte[] private_key;

    public static byte[] encrypt(String Data, byte[] private_key) throws Exception {
        Key key = generateKey(private_key);


//        Cipher c = Cipher.getInstance(ALGO);
//        c.init(Cipher.ENCRYPT_MODE, key);
//
//        byte[] encVal = c.doFinal(Base64.encode(Data.getBytes(), Base64.DEFAULT));
//        byte[] encVal = c.doFinal(Data.getBytes("utf-8"));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] ciphertext = cipher.doFinal(Data.getBytes("UTF-8"));


//        String encryptedValue = new BASE64Encoder().encode(encVal);
//        String encryptedValue = base64Encode(encVal);
//        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        //Log.e("AES file", base64Encode(encVal));
        return ciphertext;
    }

    public static String decrypt(byte[] encryptedData, byte[] private_key) throws Exception {
        Key key = generateKey(private_key);
        Cipher c = Cipher.getInstance(ALGO);

        c.init(Cipher.DECRYPT_MODE, key);
//        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
//        byte[] decordedValue = base64Decode(encryptedData);
        byte[] decValue = c.doFinal(encryptedData);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }


    private static SecretKey generateKey(byte[] private_key) throws Exception {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec("32_length_String".toCharArray(), "Salted__".getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");


//        SecretKeySpec key = new SecretKeySpec(private_key, "AES");
        // System.out.println("\nkey here  " + base64Encode(key.getEncoded()));
        return secret;
    }

    private static String base64Encode(byte[] in) {
        StringBuilder out = new StringBuilder((in.length * 4) / 3);
        int b;
        for (int i = 0; i < in.length; i += 3) {
            b = (in[i] & 0xFC) >> 2;
            out.append(CODES.charAt(b));
            b = (in[i] & 0x03) << 4;
            if (i + 1 < in.length) {
                b |= (in[i + 1] & 0xF0) >> 4;
                out.append(CODES.charAt(b));
                b = (in[i + 1] & 0x0F) << 2;
                if (i + 2 < in.length) {
                    b |= (in[i + 2] & 0xC0) >> 6;
                    out.append(CODES.charAt(b));
                    b = in[i + 2] & 0x3F;
                    out.append(CODES.charAt(b));
                } else {
                    out.append(CODES.charAt(b));
                    out.append('=');
                }
            } else {
                out.append(CODES.charAt(b));
                out.append("==");
            }
        }

        return out.toString();
    }

    private static byte[] base64Decode(String input) {
        if (input.length() % 4 != 0) {
            throw new IllegalArgumentException("Invalid base64 input");
        }
        byte decoded[] = new byte[((input.length() * 3) / 4) - (input.indexOf('=') > 0 ? (input.length() - input.indexOf('=')) : 0)];
        char[] inChars = input.toCharArray();
        int j = 0;
        int b[] = new int[4];
        for (int i = 0; i < inChars.length; i += 4) {
            // This could be made faster (but more complicated) by precomputing these index locations.
            b[0] = CODES.indexOf(inChars[i]);
            b[1] = CODES.indexOf(inChars[i + 1]);
            b[2] = CODES.indexOf(inChars[i + 2]);
            b[3] = CODES.indexOf(inChars[i + 3]);
            decoded[j++] = (byte) ((b[0] << 2) | (b[1] >> 4));
            if (b[2] < 64) {
                decoded[j++] = (byte) ((b[1] << 4) | (b[2] >> 2));
                if (b[3] < 64) {
                    decoded[j++] = (byte) ((b[2] << 6) | b[3]);
                }
            }
        }

        return decoded;
    }
}