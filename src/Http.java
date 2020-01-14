import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

  public class Http {
        public static String get(String address) {
            String result = null;
            try {
                HttpURLConnection urlConn = (HttpURLConnection) new URL(address).openConnection();
                urlConn.setConnectTimeout(30000);
                urlConn.setRequestMethod("GET");
                urlConn.connect();
                if (urlConn.getResponseCode() == 200) {
                    result = streamToString(urlConn.getInputStream());
                } else {
                    result = "";
                }
                urlConn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        public static void download(String fileUrl, File descFile) {
            try {
                HttpURLConnection urlConn = (HttpURLConnection) new URL(fileUrl).openConnection();
                urlConn.setConnectTimeout(30000);
                urlConn.setRequestMethod("GET");
                urlConn.connect();
                if (urlConn.getResponseCode() == 200) {
                    FileOutputStream fos = new FileOutputStream(descFile);
                    byte[] buffer = new byte[1024];
                    InputStream inputStream = urlConn.getInputStream();
                    while (true) {
                        int len = inputStream.read(buffer);
                        if (len == -1) {
                            break;
                        }
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                } else descFile.createNewFile();
                urlConn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static String streamToString(InputStream is) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                while (true) {
                    int len = is.read(buffer);
                    if (len != -1) {
                        baos.write(buffer, 0, len);
                    } else {
                        baos.close();
                        is.close();
                        return new String(baos.toByteArray());
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
    }