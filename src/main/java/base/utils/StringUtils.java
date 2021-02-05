package base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具
 *
 * @author Visionary
 * @since 2019/8/19 4:49 PM
 */
public class StringUtils {

    /**
     * 两字符串进行比较
     *
     * @param s1
     *            字符串1
     * @param s2
     *            字符串2
     * @return 是否相同
     */
    public static boolean equal(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.equals(s2);
    }

    /**
     * 判断字符串是否不为空
     *
     * @param s
     *            目标字符串
     */
    public static boolean isNotEmpty(String s) {
        return s != null && !"".equals(s);
    }

    /**
     * @Description:去除首尾指定字符
     * @param str  字符串
     * @param element  指定字符
     * @return: java.lang.String
     */
    public static String trimFirstAndLastChar(String str, String element){
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        do{
            int beginIndex = str.indexOf(element) == 0 ? 1 : 0;
            int endIndex = str.lastIndexOf(element) + 1 == str.length() ? str.lastIndexOf(element) : str.length();
            str = str.substring(beginIndex, endIndex);
            beginIndexFlag = (str.indexOf(element) == 0);
            endIndexFlag = (str.lastIndexOf(element) + 1 == str.length());
        } while (beginIndexFlag || endIndexFlag);
        return str;
    }

    /**
     * @Description: 去掉指定字符串的开头的指定字符
     * @param stream  原始字符串
     * @param trim 要删除的字符串
     * @return: java.lang.String
     */
    public static String StringStartTrim(String stream, String trim) {
        // null或者空字符串的时候不处理
        if (stream == null || stream.length() == 0 || trim == null || trim.length() == 0) {
            return stream;
        }
        // 要删除的字符串结束位置
        int end;
        // 正规表达式
        String regPattern = "[" + trim + "]*+";
        Pattern pattern = Pattern.compile(regPattern, Pattern.CASE_INSENSITIVE);
        // 去掉原始字符串开头位置的指定字符
        Matcher matcher = pattern.matcher(stream);
        if (matcher.lookingAt()) {
            end = matcher.end();
            stream = stream.substring(end);
        }
        // 返回处理后的字符串
        return stream;
    }

}
