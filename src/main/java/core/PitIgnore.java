package core;

import base.config.PitConfig;
import base.constants.PitConstant;
import base.utils.FileUtils;
import base.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Pit ignore
 * 
 * @Author: DoneEI
 * @Since: 2021/1/30 9:16 下午
 **/
public class PitIgnore {

    /**
     * pit ignore 文件名
     */
    private static final String PIT_IGNORE_FILE = ".pitignore";

    /**
     * pit ignore 注释
     */
    private static final String PIT_PATTERN_COMMENT = "#";

    /**
     * pit ignore 正则 **
     */
    private static final String PIT_PATTERN_CONSECUTIVE_ASTERISKS = "**";

    /**
     * pit ignore 正则 *
     */
    private static final char PIT_PATTERN_ASTERISK = '*';

    /**
     * pit ignore 正则 ?
     */
    private static final char PIT_PATTERN_SINGLE = '?';

    /**
     * pit ignore 文件分隔符
     */
    private static final char PIT_PATTERN_FILE_SEPARATOR = '/';

    /**
     * pit ignore 入选
     */
    private static final char PIT_PATTERN_INCLUDE = '!';

    /**
     * 忽略规则
     */
    private static HashMap<Boolean, Set<String>> excludeRules = new HashMap<>(2);

    /**
     * 入选规则
     */
    private static HashMap<Boolean, Set<String>> includeRules = new HashMap<>(2);

    static {
        configIgnore();
    }

    /**
     * 配置ignore规则
     */
    private static void configIgnore() {
        // 在pit仓库同级目录下寻找.pitignore文件
        File file = new File(PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + PIT_IGNORE_FILE);

        // 初始化
        includeRules.put(true, new HashSet<>());
        includeRules.put(false, new HashSet<>());
        excludeRules.put(true, new HashSet<>());
        excludeRules.put(false, new HashSet<>());

        if (file.exists()) {
            Set<String> rules = FileUtils.readFileByLines(file);

            for (String r : rules) {
                // 移除行首行尾空格
                r = r.trim();

                // 移除空白行和注释行
                if (!StringUtils.isNotEmpty(r) || r.startsWith(PIT_PATTERN_COMMENT)) {
                    continue;
                }

                addRule(r);

            }
        }

        // 默认添加.pit
        excludeRules.get(true).add(PitConstant.PIT_REPOSITORY_NAME);
    }

    private static void addRule(String r) {
        // 是否是入选规则
        boolean inc = false;

        // 是否是文件夹类型
        boolean isDir = false;

        int startIdx = 0;
        int endIdx = r.length();

        if (r.charAt(startIdx) == PIT_PATTERN_INCLUDE) {
            inc = true;

            // 排除掉!
            startIdx++;
        }

        // 去除开头的文件分隔符
        if (r.charAt(startIdx) == PIT_PATTERN_FILE_SEPARATOR) {
            startIdx++;
        }

        // 若以文件分隔符结尾,则表示文件夹类型
        if (r.charAt(endIdx - 1) == PIT_PATTERN_FILE_SEPARATOR) {
            isDir = true;
            endIdx--;
        }

        r = r.substring(startIdx, endIdx);

        if (inc) {
            Set<String> icr = includeRules.get(isDir);
            icr.add(r);

            includeRules.put(isDir, icr);
        } else {
            Set<String> ecr = excludeRules.get(isDir);
            ecr.add(r);

            excludeRules.put(isDir, ecr);
        }

    }

    /**
     * 根据.pitignore文件过滤文件
     *
     * @param file
     *            File对象
     * @return true保留 false过滤
     */
    public static boolean filter(File file) throws IOException {
        if (file == null) {
            return false;
        }

        // 根据.pitignore文件所在目录为基目录获取文件简洁相对路径
        String filePath = FileUtils.getCanonicalRelativePath(file, PitConfig.PIT_REPOSITORY);

        boolean isDir = file.isDirectory();

        for (String er : excludeRules.get(isDir)) {
            if (match(filePath, er)) {
                for (String ir : includeRules.get(isDir)) {
                    if (match(filePath, ir)) {
                        return true;
                    }
                }

                return false;
            }
        }

        return true;
    }

    private static boolean match(String s, String p) {
        char[] src = s.toCharArray();
        char[] pattern = p.toCharArray();

        boolean[][] dp = new boolean[s.length() + 1][p.length() + 1];

        dp[0][0] = true;

        for (int k = 1; k <= p.length(); k++) {
            if (pattern[k - 1] == PIT_PATTERN_ASTERISK) {
                dp[0][k] = dp[0][k - 1];
            }
        }

        for (int i = 1; i <= src.length; i++) {
            for (int j = 1; j <= pattern.length; j++) {
                if (src[i - 1] == pattern[j - 1]) {
                    // 字符相同,直接抵消一个字符
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    if (src[i - 1] == PIT_PATTERN_FILE_SEPARATOR) {
                        // 当路径字符是文件分隔符时
                        // *只能认定为空 **可以认为是/
                        if (pattern[j - 1] == PIT_PATTERN_ASTERISK) {
                            if (j > 1 && pattern[j - 2] == PIT_PATTERN_ASTERISK) {
                                dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                            } else {
                                dp[i][j] = dp[i][j - 1];
                            }
                        }
                    } else {
                        // ? 可以抵消一个字符
                        if (pattern[j - 1] == PIT_PATTERN_SINGLE) {
                            dp[i][j] = dp[i - 1][j - 1];
                        }

                        // 此时**与*等价
                        if (pattern[j - 1] == PIT_PATTERN_ASTERISK) {
                            dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                        }
                    }

                }
            }
        }

        return dp[s.length()][p.length()];
    }

    public static void main(String[] args) {
        System.out.println(match("a/b/c/test.txt", "b/**.txt"));
    }
}
