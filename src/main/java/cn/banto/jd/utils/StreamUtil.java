package cn.banto.jd.utils;

import sun.misc.BASE64Decoder;

import java.io.*;

/**
 * 流处理工具包
 * @author BANTO
 */
public class StreamUtil {

    /**
     * 将Inputstream转为File对象
     * @param ins
     * @param file
     */
    public static void inputstreamToFile(InputStream ins, File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        try {
            int length = 0;
            byte[] buffer = new byte[8192];
            while ((length = ins.read(buffer)) != -1) {
                os.write(buffer, 0, length);
                os.flush();
            }
        } finally {
            ins.close();
            os.close();
        }
    }

    /**
     * 将base64转为输入流
     * @param content
     */
    public static InputStream base64ToInputStream(String content) {
        try {
            byte[] b = new BASE64Decoder().decodeBuffer(content);
            for(int i = 0; i < b.length; ++i) {
                if(b[i] < 0) {
                    b[i] += 256;
                }
            }

            return new ByteArrayInputStream(b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
