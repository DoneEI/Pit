package core.command.internal;

import core.PitIndex;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * update index命令
 * 
 * @Author: DoneEI
 * @Since: 2021/1/21 3:14 下午
 **/
public class UpdateIndexCmd {
    /**
     * 将文件在暂存区中更新
     * 
     * @param file
     *            待更新文件
     * @return 是否在暂存区中更新
     * @throws IOException
     *             ioe
     */
    public static boolean updateIndex(File file) throws IOException {
        return PitIndex.updateIndex(file);
    }

}
