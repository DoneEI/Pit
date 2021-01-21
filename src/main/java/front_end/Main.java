package front_end;

import base.config.PitConfig;
import base.constants.PitConstant;
import base.enums.PitResultEnum;
import base.exception.PitException;
import base.utils.OutputUtil;


import java.io.File;
import java.lang.reflect.Method;

/**
 * 程序入口
 * @Author: DoneEI
 * @Since: 2021/1/21 2:14 下午
 **/
public class Main {
    /**
     * 程序入口
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            // 项目配置
            config();

            // 查找并运行命令
            findAndRunCommand(args);
        }
        catch (PitException pitEx) {
            OutputUtil.output(pitEx.getErrorMsg());
        }
        catch (Exception e) {
            OutputUtil.output("Pit System Error! See the exception message below:");
            e.printStackTrace();
        }
        finally {
            // finally处理
        }
    }

    /**
     * 项目配置
     */
    public static void config() {
        // 获取当前工作目录
        PitConfig.CURRENT_WORKING_DIRECTORY = System.getProperty("user.dir");

        // 获取系统文件分隔符
        String separator = System.getProperty("file.separator");

        // 根据当前工作目录解析仓库位置
        String currentPath = PitConfig.CURRENT_WORKING_DIRECTORY;
        int idx;

        do {
            File file = new File(currentPath + separator + PitConstant.PIT_REPOSITORY_NAME);

            if (file.exists()) {
                PitConfig.PIT_REPOSITORY = currentPath;

                break;
            } else {
                idx = currentPath.lastIndexOf(separator);

                if (idx == -1) {
                    break;
                }
                currentPath = currentPath.substring(0, idx);
            }
        } while (true);
    }

    /**
     * 根据参数寻找命令对象并执行
     * @param command 参数
     * @throws Exception Exception
     */
    public static void findAndRunCommand(String[] command) throws Exception {
        // 默认为基础命令
        String cmd = "Pit";

        // 若非基础命令
        if (command.length > 0 && !command[0].startsWith(PitConstant.EXTERNAL_COMMAND_OPTIONS_PREFIX)) {
            cmd = command[0];
        }

        try {
            // 构造cmd对象的类名
            String builder = PitConstant.EXTERNAL_COMMAND_PACKAGE +
                    cmd.substring(0, 1).toUpperCase() + cmd.substring(1) + PitConstant.EXTERNAL_COMMAND_SUFFIX;

            Object obj = Class.forName(builder).newInstance();

            // 查找该类下的run方法
            Method m = obj.getClass().getDeclaredMethod(PitConstant.EXTERNAL_COMMAND_RUN_METHOD, String[].class);

            // 设置为可访问
            m.setAccessible(true);

            // 调用,并传命令行参数
            m.invoke(obj, (Object) command);
        }
        catch (ClassNotFoundException cnf) {
            throw new PitException(PitResultEnum.NO_COMMAND_FOUND, String.format("Pit: '%s' is not a pit command, use pit -help for help.", cmd));
        }
    }
}
