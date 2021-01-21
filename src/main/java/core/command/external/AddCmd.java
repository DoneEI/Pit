package core.command.external;

import core.command.BaseCmd;

/**
 * pit add 命令实现
 *
 * @Author: DoneEI
 * @Since: 2021/1/21 3:14 下午
 **/
public class AddCmd extends BaseCmd {
    public void run(String[] args) throws Exception {
        baseLogic(() -> {
            for (String arg : args) {
                System.out.println(arg);
            }
        });
    }
}
