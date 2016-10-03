package Encryption;

import android.graphics.Path;
import android.util.Base64;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by yinseng on 9/2/16.
 */
public class RSACrypto {

    public RSACrypto() {

    }


    public PublicKey readPublicKey(String public_key) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {


        byte[] encodedPublicKey = Base64.decode(public_key, Base64.DEFAULT);
        // PKCS8 decode the encoded RSA private key

//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedPublicKey);
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(encodedPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicSpec);
    }

    public byte[] encrypt(PublicKey key, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
//        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

}
