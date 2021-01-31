package base.utils;

/**
 * 输出工具
 *
 * @Author: DoneEI
 * @Since: 2021/1/21 9:33 下午
 **/
public class OutputUtils {
    public static void output(String message) {
        if (StringUtils.isNotEmpty(message)) {
            System.out.println(message);
        }
    }

    public static void output(Object object) {
        if (object != null) {
            System.out.println(object.toString());
        }
    }
}
