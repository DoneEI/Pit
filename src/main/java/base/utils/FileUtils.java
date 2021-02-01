package base.utils;

import base.config.PitConfig;
import base.constants.PitConstant;
import base.enums.PitResultEnum;
import base.exception.PitException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File 工具
 *
 * @Author: DoneEI
 * @Since: 2021/1/24 11:28 下午
 **/
public class FileUtils {
    /**
     * 字节流读文件
     * 
     * @param file
     *            File对象
     * @return byte[]
     * @throws IOException
     *             ioe
     */
    public static byte[] readFileByByte(File file) throws IOException {
        // 判断文件大小
        long fileLen = file.length();

        if (fileLen > PitConstant.MAX_FILE_LIMIT) {
            throw new PitException(PitResultEnum.ERROR,
                String.format("error: file '%s' exceed the max limit", file.getName()));
        }

        int bufferLen = (int)fileLen;

        byte[] data = new byte[bufferLen];

        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            int readLen = fileInputStream.read(data, 0, bufferLen);

        } catch (FileNotFoundException fne) {
            throw new PitException(PitResultEnum.INVALID_FILE_PATH,
                String.format("error: pathspec '%s' did not match any files", file.getName()));
        }

        return data;
    }

    /**
     * 以行读文件 字符流
     * 
     * @param file
     *            File对象
     * @return Set<String>
     */
    public static List<String> readFileByLines(File file) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            // 一次读一行，读入null时文件结束bai
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * 字节流写文件
     * 
     * @param data
     *            字节数据
     * @param filePath
     *            文件路径
     * @throws IOException
     *             ioe
     */
    public static void writeFile(byte[] data, String filePath) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            out.write(data);
        } catch (FileNotFoundException fne) {
            throw new PitException(PitResultEnum.INVALID_FILE_PATH,
                String.format("error: pathspec '%s' did not match any files", filePath));
        }

    }

    /**
     * 获得文件简洁相对路径
     * 
     * @param file
     *            File对象
     * @param base
     *            相对路径的基目录
     * @return 简洁相对路径
     * @throws IOException
     *             ioe
     */
    public static String getCanonicalRelativePath(File file, String base) throws IOException {
        String cp = file.getCanonicalPath();

        if (base == null || !cp.startsWith(base)) {
            throw new PitException(PitResultEnum.ERROR, "error: wrong base path when getting canonical relative path");
        } else {

            if (StringUtils.equal(cp, base)) {
                return PitConstant.ROOT_DIRECTORY_NAME;
            } else {
                int offset = base.length() + PitConfig.FILE_SEPARATOR.length();

                return cp.substring(offset);
            }

        }

    }
}
