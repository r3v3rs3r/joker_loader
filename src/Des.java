import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;

public class Des {
    private String mIV;
    private String mKey;

    public Des(String pkg, String subId) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pkg.length(); i++) {
            sb.append(pkg.charAt(i));
            int j = i / 3;
            if (j < subId.length()) {
                sb.append(subId.charAt(j));
            }
        }
        String seed = sb.toString().replace(".", "");
        int index = pkg.length() / 3;
        int end = index + 8;
        this.mKey = seed.substring(index, end);
        this.mIV = seed.substring(seed.length() - end, seed.length() - index);
    }

    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[(len / 2)];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public String desDecrypt(String source) throws Exception {
        if (source == null || source.length() == 0) {
            return null;
        }
        byte[] src = hexStringToByteArray(source);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(2, SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(this.mKey.getBytes(
                StandardCharsets.UTF_8))), new IvParameterSpec(this.mIV.getBytes(
                StandardCharsets.UTF_8)));
        return new String(cipher.doFinal(src));
    }
}
