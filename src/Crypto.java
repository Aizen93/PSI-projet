import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.security.*;
import java.util.Base64;

public class Crypto {

    private KeyPairGenerator keyGen;
    private  KeyPair keypair;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Cipher cipher;

    public Crypto(){
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            keypair = keyGen.genKeyPair();
            privateKey = keypair.getPrivate();
            publicKey = keypair.getPublic();
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        }catch( Exception e){
            Affichage.display_error("Error loading encryption packages");
        }
    }

    public String encryption(String message) {
        String crypt = "";
        byte[] textChiffre = new byte[0];
        try {
            textChiffre = cipher.doFinal(message.getBytes()); //chiffré
            //StringBuffer stringBuffer = new StringBuffer();
            //for (byte bytes : textChiffre) stringBuffer.append(String.format("%02x", bytes & 0xff));
            //crypt =stringBuffer.toString();
        } catch (IllegalBlockSizeException e) {
            Affichage.display_exception("Error size in encryption function");
        } catch (BadPaddingException e) {
            Affichage.display_exception("Error badPadding in encryption function");
        }
        return Base64.getEncoder().encodeToString(textChiffre);
    }

    public String decryption(String crypt) {
        try {
            byte[] textChiffre = Base64.getDecoder().decode(crypt);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] plainText = cipher.doFinal(textChiffre);
            return new String(plainText);
        } catch (InvalidKeyException e) {
            Affichage.display_exception("Error key in decryption function");
        } catch (BadPaddingException e) {
            Affichage.display_exception("Error badPadding in decryption function");
        } catch (IllegalBlockSizeException e) {
            Affichage.display_exception("Error size in decryption function");
        }
        return "";
    }

    public static void main(String[] args) {
        try{
            String mess = "Oussama va manger du chocolat !!";
            Crypto crypto = new Crypto();
            String crypt = crypto.encryption(mess);
            System.out.println("encryption : "+crypt);
            System.out.println("decryption : "+crypto.decryption(crypt));
        }
        catch( Exception e){System.out.println("« probleme");
        }
    }
}
