package core.command.external;

import core.command.BaseCmd;

/**
 * @description: pit commit 命令实现
 * @author: RealGang
 * @create: 2021-01-31 23:16
 **/
public class CommitCmd extends BaseCmd {

    public static Object run(String[] args) throws Exception {
        baseLogic(() -> {
            //todo
        }, CHECK_PIT_REPOSITORY_VALID);
        return null;
    }
}
