package utilsss;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;

public class AKSKUtils {

    public static String getSignature(String accessKey, String secretKey) {
        return aesEncrypt(accessKey + "|" + UUID.randomUUID().toString() + "|" + System.currentTimeMillis(), secretKey, accessKey);
    }

    private static String aesEncrypt(String src, String secretKey, String iv){
        try {
            byte[] raw = secretKey.getBytes("UTF-8");
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv1 = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv1);
            byte[] encrypted = cipher.doFinal(src.getBytes("UTF-8"));
            return Base64.encodeBase64String(encrypted);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
