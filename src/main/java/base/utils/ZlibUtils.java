package base.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Zlib 压缩工具
 * 
 * @Author: DoneEI
 * @Since: 2021/1/25 3:27 下午
 **/
public class ZlibUtils {
    /**
     * 压缩
     * 
     * @param data
     *            字节流数据
     * @return 压缩后字节流
     * @throws IOException
     *             ioe
     */
    public static byte[] compress(byte[] data) throws IOException {
        byte[] output;

        Deflater deflater = new Deflater();

        deflater.reset();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

        byte[] buf = new byte[1024];
        while (!deflater.finished()) {
            int i = deflater.deflate(buf);
            bos.write(buf, 0, i);
        }
        output = bos.toByteArray();
        bos.close();

        deflater.end();
        return output;
    }

    /**
     * 解压数据
     * 
     * @param data
     *            待解压字节流数据
     * @return 解压后字节流数据
     * @throws Exception
     *             ex
     */
    public static byte[] decompress(byte[] data) throws Exception {
        byte[] output;

        Inflater inflater = new Inflater();
        inflater.reset();
        inflater.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);

        byte[] buf = new byte[1024];
        while (!inflater.finished()) {
            int i = inflater.inflate(buf);
            o.write(buf, 0, i);
        }
        output = o.toByteArray();

        o.close();

        inflater.end();
        return output;
    }
}
