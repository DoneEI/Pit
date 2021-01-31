package entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: commit对象
 * @author: RealGang
 * @create: 2021-01-31 23:11
 **/
@Data
public class Commit implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6171577770940384807L;

    /**
     * 提交者
     */
    String committer;

    /**
     * 提交时间
     */
    Date commitTime;

    /**
     * 提交信息
     */
    String commitMsg;

    /**
     * 上次提交的节点对象的Idx
     */
    String lastCommitIdx;

    /**
     * 提交的文件对象的idx
     */
    String treeIdx;
}
