package core.command.external;

import base.config.PitConfig;
import base.constants.PitConstant;
import base.enums.PitResultEnum;
import base.exception.PitException;
import base.utils.FileUtils;
import base.utils.SerializationUtils;
import base.utils.StringUtils;
import core.command.BaseCmd;
import core.command.internal.CommitTreeCmd;
import core.command.internal.WriteTreeCmd;
import entity.IndexTree;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @description: pit commit 命令实现
 * @author: RealGang
 * @create: 2021-01-31 23:16
 **/
public class CommitCmd extends BaseCmd {

    /**
     * commit 的参数选项
     */
    private static Set<String> validOptions = new HashSet<>(Arrays.asList(
            // -m 参数表示可以直接输入后面的“message”
            "-m",

            // a参数可以将所有已跟踪文件中的执行修改或删除操作的文件都提交到本地仓库，即使没有经过git add添加到暂存区
            "-a",

            // 追加提交
            "--amend"));

    /**
     * 当前执行包含选项
     */
    private static Set<String> currentOptions = new HashSet<>();

    /**
     * commit 的提交信息
     */
    private static String message;

    public static Object run(String[] args) throws Exception {
        baseLogic(() -> {
            // 解析参数
            parseArgs(args);
            //判断能否提交

            // 首先从配置文件中加载提交者信息

            // 从版本库中得到last commit的Idx
            String lastCommitIdx = getLastCommit();
            // 如果是第一次commit，将当前分支写入到HEAD文件中,默认写master分支
            if (lastCommitIdx == null) {
                String refStr = "ref: refs/heads/master";
                CommitTreeCmd.writeBranch(refStr);
            }

            // 从Index文件中获取暂存区的indexTree
            IndexTree rootIndex = getRootIndex();
            // 核心逻辑 根据IndexTree 生成Tree对象,得到tree的序列化值
            String treeIdx = WriteTreeCmd.writeTree(rootIndex);

            // 序列化该Commit 对象 得到新的Commit Idx 然后放在版本库中
            CommitTreeCmd.commitTree(treeIdx, message, lastCommitIdx);

        }, CHECK_PIT_REPOSITORY_VALID);
        return null;
    }

    /**
     * @param
     * @Description: 从index文件中获取暂存区indexTree
     * @return: entity.IndexTree
     */
    private static IndexTree getRootIndex() {
        IndexTree root;
        try {
            root = SerializationUtils.deserialize(
                    PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + PitConstant.PIT_REPOSITORY_NAME
                            + PitConfig.FILE_SEPARATOR + "index");
            if (root == null) {
                throw new PitException(PitResultEnum.ERROR,
                        "error: Changes not staged for commit \n no changes added to commit");
            }
            return root;
        } catch (Exception e) {
            throw new PitException(PitResultEnum.ERROR,
                    "error: system error occurs when initializing index tree. See the error message below:" + Arrays
                            .toString(e.getStackTrace()));
        }
    }

    /**
     * @param args 参数
     * @Description:解析参数
     * @return: void
     */
    private static void parseArgs(String[] args) {
        for (String arg : args) {
            if (arg.startsWith(PitConstant.EXTERNAL_COMMAND_OPTIONS_PREFIX)) {
                if (validOptions.contains(arg)) {
                    currentOptions.add(arg);
                } else {
                    throw new PitException(PitResultEnum.INVALID_OPTION,
                            String.format("error: unknown option '%s'", arg));
                }
            } else if (StringUtils.isNotEmpty(arg)) {
                // 提交的message参数信息，去除首尾的双引号或者单引号
                if (arg.startsWith("'")) {
                    message = StringUtils.trimFirstAndLastChar(arg, "'");
                } else if (arg.startsWith("\"")) {
                    message = StringUtils.trimFirstAndLastChar(arg, "\"");
                } else {
                    message = arg.trim();
                }

            }
        }
        if (currentOptions.contains("-m") && !StringUtils.isNotEmpty(message)) {
            throw new PitException(PitResultEnum.INVALID_OPTION, "error: switch `m' requires a value\n");
        }
    }

    /**
     * @param
     * @Description: 获取上一次的commit IDx
     * @return: java.lang.String
     */
    private static String getLastCommit() throws IOException {
        String lastCommitIdx;

        // 在.pit目录下寻找HEAD文件
        File file = new File(PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + PitConstant.PIT_REPOSITORY_NAME
                + PitConfig.FILE_SEPARATOR + "HEAD");
        // 从HEAD文件中读取当前分支，eg: ref: refs/heads/master
        String curRefs = new String(FileUtils.readFileByByte(file));
        if (!StringUtils.isNotEmpty(curRefs)) {
            // 如果是第一次commit，将当前分支写入到HEAD文件中
            return null;
        } else {
            String lastCommitPath = StringUtils.StringStartTrim(curRefs, "ref:").trim();
            File lastCommitFile = new File(
                    PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + PitConstant.PIT_REPOSITORY_NAME
                            + PitConfig.FILE_SEPARATOR + lastCommitPath);
            lastCommitIdx = new String(FileUtils.readFileByByte(lastCommitFile));
            return lastCommitIdx;
        }
    }
}
