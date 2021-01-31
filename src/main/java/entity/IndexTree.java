package entity;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 暂存区 树节点
 *
 * @Author: DoneEI
 * @Since: 2021/1/24 5:17 下午
 **/
@Data
public class IndexTree implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3947917394774961163L;

    /**
     * 当前节点表示的文件名 (文件和文件夹)
     */
    String fileName;

    /**
     * 文件对应的pit/object 索引, 文件夹为null
     */
    String idx;

    /**
     * 节点类型 (文件夹类型 or 文件类型)
     */
    String nodeType;

    /**
     * 时间戳
     */
    Long timeStamp;

    /**
     * 子节点
     */
    HashMap<String, IndexTree> child;

    /**
     * 返回Child 若无则创建一个空map
     */
    public HashMap<String, IndexTree> getChild(boolean create) {
        if (child == null) {
            child = new HashMap<>(2);
        }

        return getChild();
    }
}
