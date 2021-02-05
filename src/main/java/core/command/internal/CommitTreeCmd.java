package core.command.internal;

import base.config.PitConfig;
import base.constants.PitConstant;
import base.utils.FileUtils;
import base.utils.SerializationUtils;
import base.utils.StringUtils;
import core.command.BaseCmd;
import entity.Commit;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @description: commit-tree  命令（创建一个 commit object，让它指向一个 tree object）
 * @author: RealGang
 * @create: 2021-02-01 09:48
 **/
public class CommitTreeCmd extends BaseCmd {

    /**
     * @Description: 创建一个 commit object，指向一个 tree object
     * @param treeIdx
     * @param message
     * @param lastCommitIdx
     * @return: java.lang.String
     */
    public static void commitTree(String treeIdx, String message, String lastCommitIdx) throws IOException {

        Commit commit = new Commit();
        commit.setCommitMsg(message);
        commit.setTreeIdx(treeIdx);
        commit.setLastCommitIdx(lastCommitIdx);
        commit.setCommitTime(new Date());
        commit.setCommitter(PitConfig.AUTHOR_NAME);

        // “ {commit对象生成时间} {作者} {提交信息} {treeIdx}”作为SHA1对象，生成对应的commitIdx
        String SHA1Content = new Date()+PitConfig.AUTHOR_NAME+message+treeIdx;
        String commitIdxNow = DigestUtils.sha1Hex(SHA1Content).substring(0,40);
        String serializePath = PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + PitConstant.PIT_REPOSITORY_NAME+PitConfig.FILE_SEPARATOR+"object"+PitConfig.FILE_SEPARATOR+commitIdxNow.substring(0,2);
        // 序列化该commit对象
        SerializationUtils.serialize(commit,serializePath,commitIdxNow.substring(2));

        // 在.pit目录下寻找HEAD文件
        File file = new File(PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + PitConstant.PIT_REPOSITORY_NAME
                + PitConfig.FILE_SEPARATOR + "HEAD");
        // 从HEAD文件中读取当前分支，eg: ref: refs/heads/master
        String curRefs = new String(FileUtils.readFileByByte(file));
        String branchPath = StringUtils.StringStartTrim(curRefs, "ref:").trim();
        // 在.pit目录下寻找refs/heads/master文件
        File branchFile = new File(PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + PitConstant.PIT_REPOSITORY_NAME
                + PitConfig.FILE_SEPARATOR + branchPath);
        if (!branchFile.exists()) {
            branchFile.createNewFile();
        }
        // 将commitIdx写入到对应的分支路径下，eg: .pit/refs/heads/master
        FileUtils.writeFile(commitIdxNow.getBytes(StandardCharsets.UTF_8),PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + PitConstant.PIT_REPOSITORY_NAME
                + PitConfig.FILE_SEPARATOR + branchPath);
    }

    /**
     * @Description: 分支路径写入到HEAD文件中
     * @param ref
     * @return: void
     */
    public static void writeBranch(String ref) throws IOException {
        FileUtils.writeFile(ref.getBytes(StandardCharsets.UTF_8),PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + PitConstant.PIT_REPOSITORY_NAME
                + PitConfig.FILE_SEPARATOR + "HEAD");
    }
}
