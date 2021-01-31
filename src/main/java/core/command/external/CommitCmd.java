package core.command.external;

import core.command.BaseCmd;

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
     * commit 的提交信息
     */
    private static String message;

    public static Object run(String[] args) throws Exception {
        baseLogic(() -> {
            // 解析参数

            //判断能否提交

            // 首先从配置文件中加载提交者信息

            // 从版本库中得到last commit的Idx

            // 根据IndexTree 生成Tree对象

            // 序列化该Commit 对象 得到新的Commit Idx 然后放在版本库中
        }, CHECK_PIT_REPOSITORY_VALID);
        return null;
    }
}
