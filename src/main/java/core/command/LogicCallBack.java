package core.command;

/**
 * 逻辑抽象
 *
 * @Author: DoneEI
 * @Since: 2021/1/21 9:20 下午
 **/
public interface LogicCallBack {
    /**
     * 实现execute方法来实现命令
     * @throws Exception ex
     */
    void execute() throws Exception;
}
