package core.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 命令父类
 *
 * @Author: DoneEI
 * @Since: 2021/1/21 8:31 下午
 **/
public class BaseCmd {

    /** 检查pit仓库是否有效 */
    protected final String CHECK_PIT_REPOSITORY_VALID = "CHECK_PIT_REPOSITORY_VALID";

    /**
     * 基础逻辑
     *
     * @param logicCallBack 逻辑处理
     * @param control 控制参数
     * @throws Exception Exception
     */
    protected void baseLogic(LogicCallBack logicCallBack, String... control) throws Exception {
        // 1.前置操作

        // 将扩展参数转成Set
        Set<String> ctl = new HashSet<>(Arrays.asList(control));

        // 检查pit仓库是否有效
        if (ctl.contains(CHECK_PIT_REPOSITORY_VALID)) {
            //todo
        }

        // 执行核心逻辑
        logicCallBack.execute();
    }
}
