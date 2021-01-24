package base.constants;

/**
 * Pit 相关常量
 *
 * @Author: DoneEI
 * @Since: 2021/1/21 10:38 下午
 **/

public class PitConstant {
    /** 命令相关 **/

    /**
     * 外部命令所在包名
     */
    public static final String EXTERNAL_COMMAND_PACKAGE = "core.command.external.";

    /**
     * 外部命令类后缀
     */
    public static final String EXTERNAL_COMMAND_SUFFIX = "Cmd";

    /**
     * 外部命令启动方法
     */
    public static final String EXTERNAL_COMMAND_RUN_METHOD = "run";

    /**
     * 外部命令选项前缀
     */
    public static final String EXTERNAL_COMMAND_OPTIONS_PREFIX = "-";

    /** 其他 **/

    /**
     * pit仓库名字
     */
    public static final String PIT_REPOSITORY_NAME = ".pit";

    /**
     * pit支持最大文件 16MB
     */
    public static final long MAX_FILE_LIMIT = 16777216L;

}
