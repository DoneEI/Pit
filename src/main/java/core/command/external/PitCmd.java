package core.command.external;

import base.utils.OutputUtil;
import core.command.BaseCmd;

/**
 * Pit 基本命令
 *
 * @Author: DoneEI
 * @Since: 2021/1/21 11:13 下午
 **/
public class PitCmd extends BaseCmd {

    /**
     * 该命令可供提供的选项
     */
    static String[] optionsAvailable = {
            "-h"
    };

    /**
     * 每个外部命令都需要实现run方法, 该方法的主要逻辑放在LogicCallBack中的execute方法里.
     *
     * @param args 参数
     * @throws Exception Exception
     */
    public void run(String[] args) throws Exception {
        baseLogic(() -> {
            OutputUtil.output("Welcome for using Pit! Using pit -h for help.");
        });
    }
}
