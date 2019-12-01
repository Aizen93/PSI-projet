import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Crypto {

    private static KeyPairGenerator keyGen;
    private static KeyPair keypair;

    public static void generateKeyPair() {
        keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        keyGen.initialize(1024, new SecureRandom());
        keypair = keyGen.generateKeyPair();
    }

    public static KeyPair getKeypair(){
        return keypair;
    }

    public static String encrypt(String plainText){
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, keypair.getPublic());
            byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));
            String mess =  Base64.getEncoder().encodeToString(cipherText);
            System.out.println("necrypt : "+mess);
            return mess;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String cipherText) {
        try {
            byte[] bytes = Base64.getDecoder().decode(cipherText);
            Cipher decriptCipher = Cipher.getInstance("RSA");
            decriptCipher.init(Cipher.DECRYPT_MODE, keypair.getPrivate());
            return new String(decriptCipher.doFinal(bytes), UTF_8);
        }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        catch (NoSuchPaddingException e) { e.printStackTrace(); }
        catch (BadPaddingException e) { e.printStackTrace(); }
        catch (IllegalBlockSizeException e) { e.printStackTrace(); }
        catch (InvalidKeyException e) { e.printStackTrace(); }
        return null;
    }

    public static void main(String[] args) {
//First generate a public/private key pair
        generateKeyPair();
//Our secret message
        String message = "the answer to life the universe and everything";

//Encrypt the message
        String cipherText = encrypt(message);

        System.out.println(cipherText);

//Now decrypt it
        String decipheredMessage = decrypt(cipherText);

        System.out.println(decipheredMessage);
    }
}
