package core.command.internal;

import base.enums.PitResultEnum;
import base.exception.PitException;
import base.utils.FileUtils;
import base.utils.SerializationUtils;
import base.utils.ZlibUtils;
import core.command.BaseCmd;
import entity.Blob;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * hash object命令
 * 
 * @Author: DoneEI
 * @Since: 2021/1/24 4:55 下午
 **/
public class HashObjectCmd extends BaseCmd {

    /**
     * 将选项放入Set
     */
    private static Set<String> validOptions = new HashSet<>(Arrays.asList(
        // 将文件写入.pit/objects
        "-w"));

    /**
     * 当前执行包含选项
     */
    private static Set<String> currentOptions = new HashSet<>();

    public static void run(String[] args) throws Exception {
        baseLogic(() -> {

        });
    }

    /**
     * 将文件对应的内容写入pit仓库并返回40位索引值
     * 
     * @param file
     *            文件
     * @return 40位索引值
     * @throws IOException
     *             ioe
     */
    public static String hashObject(File file) throws IOException {

        if (!file.isFile()) {
            throw new PitException(PitResultEnum.ERROR, String.format("error: unable to hash '%s'", file.getName()));
        }

        // 读取文件内容计算40位索引值
        byte[] readFileData = FileUtils.readFileByByte(file);
        byte[] readFileLenData = longToByte(file.length());

        byte[] data = new byte[readFileData.length + readFileLenData.length];
        System.arraycopy(readFileLenData, 0, data, 0, readFileLenData.length);
        System.arraycopy(readFileData, 0, data, readFileLenData.length, readFileData.length);

        // 使用SHA1算法计算索引, 保留前40位
        String idx = DigestUtils.sha1Hex(data).substring(0, 40);

        // 将blob对象序列化
        if (currentOptions != null && currentOptions.contains("-w")) {
            // 构建Blob对象
            Blob blob = new Blob();

            // 将文件内容进行压缩
            blob.setFileContent(ZlibUtils.compress(readFileData));
            blob.setFileContentLen(file.length());

            // 以idx前两位作为文件夹名
            String dir = idx.substring(0, 2);

            // 以idx后38位作为文件名
            String fileName = idx.substring(2);

            String path = ".pit/object/" + dir;

            SerializationUtils.serialize(blob, path, fileName);
        }

        return idx;
    }

    /**
     * 其他命令使用的该命令时可设置选项
     * 
     * @param file
     *            文件
     * @param options
     *            选项
     * @return 40位索引值
     * @throws IOException
     *             ioe
     */
    public static String hashObject(File file, String... options) throws IOException {
        currentOptions = new HashSet<>(Arrays.asList(options));

        return hashObject(file);
    }

    private static byte[] longToByte(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            // 将最低位保存在最低位
            b[i] = new Long(temp & 0xff).byteValue();

            // 右移8位
            temp = temp >> 8;
        }
        return b;
    }
}
