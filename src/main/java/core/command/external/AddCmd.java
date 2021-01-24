package core.command.external;

import base.config.PitConfig;
import base.constants.PitConstant;
import base.enums.PitResultEnum;
import base.exception.PitException;
import base.utils.FileUtils;
import base.utils.OutputUtils;
import base.utils.StringUtils;
import core.command.BaseCmd;
import core.command.internal.UpdateIndexCmd;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * pit add 命令实现
 *
 * @Author: DoneEI
 * @Since: 2021/1/21 3:14 下午
 **/
public class AddCmd extends BaseCmd {
    /**
     * 将选项放入Set
     */
    private static Set<String> validOptions = new HashSet<>(Arrays.asList(
        // 输出添加的文件
        "-o",

        // 忽略错误文件
        "-i"));

    /**
     * 当前执行包含选项
     */
    private static Set<String> currentOptions = new HashSet<>();

    /**
     * ignore文件名
     */
    private static final String PIT_IGNORE_FILE = ".pitignore";

    /**
     * ignore文件入选规则前缀
     */
    private static final String PIT_IGNORE_INCLUDE_RULE_PREFIX = "!";

    /**
     * 忽略的文件
     */
    private static Set<String> excludeRules = new HashSet<>();

    /**
     * 入选的文件
     */
    private static Set<String> includeRules = new HashSet<>();

    /**
     * 当前add命令执行的文件路径
     */
    private static Set<String> addedPathSpec = new HashSet<>();

    public static Object run(String[] args) throws Exception {
        baseLogic(() -> {
            // 1.解析参数
            Set<String> pathSpec = parseArg(args);

            // 2.添加ignore规则
            addIgnore();

            // 3.检查文件路径
            File[] files = checkPathSpec(pathSpec);

            // 4.将files加入到暂存区
            add(files);

            // 5.更新暂存区至pit仓库
            UpdateIndexCmd.updateRoot();

            if (currentOptions.contains("-o")) {
                for (String f : addedPathSpec) {
                    OutputUtils.output("add " + f);
                }
            }

        }, CHECK_PIT_REPOSITORY_VALID);

        return null;
    }

    /**
     * 参数解析
     * 
     * @param args
     *            参数
     */
    private static Set<String> parseArg(String[] args) {
        // 从参数中解析出pathSpec
        Set<String> pathSpec = new HashSet<>();

        for (String arg : args) {
            if (arg.startsWith(PitConstant.EXTERNAL_COMMAND_OPTIONS_PREFIX)) {
                // 如果参数是option
                if (validOptions.contains(arg)) {
                    currentOptions.add(arg);
                } else {
                    throw new PitException(PitResultEnum.INVALID_OPTION,
                        String.format("error: unknown option '%s'", arg));
                }
            } else if (StringUtils.isNotEmpty(arg)) {
                // 排除命令参数外, 其他参数均认为是pathSpec
                pathSpec.add(arg);
            }
        }

        return pathSpec;
    }

    /**
     * 配置ignore规则
     */
    private static void addIgnore() {
        // 在pit仓库同级目录下寻找.pitignore文件
        File file = new File(PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + PIT_IGNORE_FILE);

        if (file.exists()) {
            Set<String> rules = FileUtils.readFileByLines(file);

            for (String r : rules) {
                if (!StringUtils.isNotEmpty(r) || r.startsWith("#")) {
                    continue;
                }

                addRule(r);

            }
        }

        // 将pit仓库排除
        excludeRules.add(PitConstant.PIT_REPOSITORY_NAME);

        // 将.入选
        includeRules.add(".");
    }

    private static void addRule(String r) {
        boolean inc = false;

        if (r.startsWith(PIT_IGNORE_INCLUDE_RULE_PREFIX)) {
            inc = true;
            r = r.substring(1);
        }

        if (r.startsWith("\\")) {
            r = r.substring("\\".length());
        }

        if (inc) {
            includeRules.add(r);
        } else {
            excludeRules.add(r);
        }

    }

    /**
     * 检查文件路径是否正确
     */
    private static File[] checkPathSpec(Set<String> pathSpec) {
        File[] files = new File[pathSpec.size()];

        int i = 0;

        for (String filePath : pathSpec) {
            File file = new File(filePath);

            if (!file.exists()) {
                if (!currentOptions.contains("-i")) {
                    throw new PitException(PitResultEnum.INVALID_FILE_PATH,
                        String.format("error: pathspec '%s' did not match any files", filePath));
                } else {
                    continue;
                }
            }

            files[i] = file;
            i++;
        }

        return files;
    }

    /**
     * 核心逻辑 将文件加入到暂存区并构建IndexTree
     * 
     * @param files
     *            文件数组
     * @throws IOException
     *             ioe
     */
    private static void add(File[] files) throws IOException {
        if (files == null) {
            return;
        }

        for (File f : files) {
            // 获得文件的canonical路径
            String canonicalPath = FileUtils.getCanonicalRelativePath(f, PitConfig.CURRENT_WORKING_DIRECTORY);

            // 如果处理过或被过滤则跳过
            if (addedPathSpec.contains(canonicalPath) || !filter(f)) {
                continue;
            }

            if (f.isFile()) {
                if (UpdateIndexCmd.updateIndex(f)) {
                    // 如果修改, 加入到addedPathSpec中
                    addedPathSpec.add(canonicalPath);
                }

            } else if (f.isDirectory()) {
                add(f.listFiles());
            }

        }
    }

    /**
     * 根据.pitignore文件过滤文件
     * 
     * @param file
     *            File对象
     * @return true保留 false过滤
     */
    private static boolean filter(File file) throws IOException {
        if (file == null) {
            return false;
        }

        // 根据.pitignore文件所在目录为基目录获取文件简洁相对路径
        String filePath = FileUtils.getCanonicalRelativePath(file, PitConfig.PIT_REPOSITORY);
        String fileName = file.getName();

        // 首先过滤空文件夹
        if (file.isDirectory() && file.listFiles() == null) {
            return false;
        }

        // 根据ignore文件规则过滤
        for (String er : excludeRules) {
            if (filePath.matches(er) || fileName.matches(er)) {
                // 如果文件路径匹配上排除规则,那么检查是否满足入选规则
                for (String ir : includeRules) {
                    if (filePath.matches(ir) || fileName.matches(ir)) {
                        return true;
                    }
                }
                return false;
            }
        }

        return true;
    }

}
