package core.algorithm;

import base.enums.PitResultEnum;
import base.exception.PitException;
import base.utils.FileUtils;
import base.utils.OutputUtils;
import base.utils.StringUtils;
import javafx.util.Pair;
import lombok.Data;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * LCS 算法
 * 
 * @Author: DoneEI
 * @Since: 2021/1/21 3:12 下午
 **/
public class Diff {

    public static List<String> diff(List<String> origin, List<String> current) {
        List<Snake> paths = new ArrayList<>();

        int solutionStep = myer(origin, current, paths);

        if (solutionStep != -1) {
            return findChanges(paths, solutionStep, origin, current);
        } else {
            throw new PitException(PitResultEnum.ERROR, "error: can not compare the differences between two files");
        }
    }

    /**
     * Myer 算法 s1表示原始文本 s2表示新文本。 diff算法即求s1 -> s2的最小编辑问题 将编辑问题转换成图形式, s1位于x轴,s2位于y轴.
     * 右移一步表示删除s1的x位上的数据(字符或行),下移一步表示增加一个s2的y位上的数据,若x的下一位与y的下一位相同,则沿着对角线移动. 右移和下移操作代表编辑一步,对角线移动则不计步数 设k = x -
     * y,在图中将k值相同的点连接成线, 则有结论:对于k值相同的点来说,x值越大则它移动至终点的所需要的步数越小. 设d为从原点出发移动至终点所需要的步数, 当d=0时,仅能移动至k=0的线上,当d=1时,仅能移动至k=1 或
     * k=-1的线上, 当d=2时,仅能移动至k=-2 k=0 k=2的线上,依次类推 则当d=d时,仅能移动至k=-d k=-d+2 .... k=d-2 k=d的线上.
     * 我们利用贪心的思想求得每多前进一步时每条k线能到达的x值最大的点 当此时所走步数为d时,对于一般k线而言,它的来源是k+1线 -> k线,对应着下移操作(即增加一个s2数据),此时x1 = v[d-1][k+1],或是
     * k-1线 -> k线,对应着右移操作(即删除一个s1数据) 此时x2 = v[d-1][k-1]+1
     * 则我们根据x1与x2的大小来决定此时k能到达的最大x值,需要注意的是相等情况,为更直观的反映变化,先删除再增加是更佳选择,所以尽量先右移
     * 当到达k线后需要沿着对角线尽可能走下去,因为沿着对角线移动并不占步数.这里v原本应用二维矩阵存储,但观察可以发现对相邻d而言,与k相关的取值不重叠(奇偶),为节省空间可以压缩成一维数组 d的取值是0 ~ s1.len +
     * s2.len k的取值是-d ~ d 为索引方便需要将k索引至0起始 所以设max = s1.len + s2.len + 1 Xk = v[k+max]
     * 
     * @param s1
     *            原始数据
     * @param s2
     *            新数据
     */
    private static int myer(List<String> s1, List<String> s2, List<Snake> paths) {
        int max = s1.size() + s2.size();
        int[] v = new int[2 * max + 1];

        // for (int k = 0; k <= s1.size(); k++) {
        // v[k + max] = k;
        // }

        for (int d = 0; d <= max; d++) {
            for (int k = -d; k <= d; k += 2) {
                // 判断来源是下移还是右移
                boolean down = (k == -d) || (k != d && (v[k + 1 + max] > v[k - 1 + max]));

                // 记录变化路径
                Snake snake = new Snake();

                // 求移动前的k值和x值
                int kPrev = down ? k + 1 : k - 1;
                int xPrev = v[kPrev + max];

                // 设置路径出发点
                snake.setStart(xPrev, xPrev - kPrev);

                // 右移xCur = xPrev + 1
                int xCur = down ? xPrev : xPrev + 1;
                int yCur = xCur - k;

                // 设置路径中点,即右移或者下移后的点
                snake.setMid(xCur, yCur);

                // 沿着对角线尽可能的移动
                while (xCur <= s1.size() - 1 && yCur <= s2.size() - 1
                    && StringUtils.equal(s1.get(xCur), s2.get(yCur))) {
                    xCur++;
                    yCur++;
                }

                // 设置路径终点,因为可能会沿着对角线移动
                snake.setEnd(xCur, yCur);
                paths.add(snake);

                // 记录此时的d值下 k能到达的最大值
                v[k + max] = xCur;

                // 找到一个解退出
                if (xCur == s1.size() && yCur == s2.size()) {
                    return d;
                }
            }
        }

        return -1;

    }

    private static List<String> findChanges(List<Snake> paths, int d, List<String> s1, List<String> s2) {
        int idx = paths.size() - 1;

        // 终点
        int xEnd = paths.get(idx).getEnd().x;
        int yEnd = paths.get(idx).getEnd().y;

        List<Point> points = new ArrayList<>();

        while (d > 0) {
            for (int i = idx; i >= 0; i--) {
                Snake cur = paths.get(i);

                // 沿着终点逆向寻找
                // 终点的起点是上一个路径的终点
                if (xEnd == cur.getEnd().x && yEnd == cur.getEnd().y) {

                    points.add(cur.end);
                    points.add(cur.mid);
                    points.add(cur.start);

                    xEnd = cur.getStart().x;
                    yEnd = cur.getStart().y;

                    idx = i - 1;
                }

                if (xEnd == 0 && yEnd == 0) {
                    // 终止寻找
                    idx = -1;
                    break;
                }
            }

            d--;
        }

        List<String> changes = new ArrayList<>();

        // 这里假设points的个数是d * 3(即start,mid,end)
        for (int i = points.size() - 1; i >= 0; i -= 3) {
            Point start = points.get(i);
            Point mid = points.get(i - 1);
            Point end = points.get(i - 2);

            // 如果中点.x等于起点.x, 说明是下移操作 -> 添加s2的mid.y
            if (mid.x == start.x) {
                changes.add("+ " + s2.get(mid.y - 1));
            } else {
                // 否则是右移操作 -> 删除s1的mid.x
                changes.add("- " + s1.get(mid.x - 1));
            }

            // 如果中点与终点不同,说明沿着对角线有移动
            if (mid.x != end.x && mid.y != end.y) {
                do {
                    changes.add("  " + s1.get(++mid.x - 1));
                } while (mid.x != end.x);
            }

        }

        return changes;
    }

    @Data
    static class Snake {
        private Point start;

        private Point mid;

        private Point end;

        public void setStart(int x, int y) {
            start = new Point(x, y);
        }

        public void setMid(int x, int y) {
            mid = new Point(x, y);
        }

        public void setEnd(int x, int y) {
            end = new Point(x, y);
        }
    }

    public static void main(String[] args) {
        File f1 = new File("Test/1/test1.txt");
        File f2 = new File("Test/1/test2.txt");

        OutputUtils.output(diff(FileUtils.readFileByLines(f1), FileUtils.readFileByLines(f2)));
    }
}
