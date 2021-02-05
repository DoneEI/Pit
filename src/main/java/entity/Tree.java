package entity;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @description: tree对象
 * @author: RealGang
 * @create: 2021-01-31 23:04
 **/
@Data
public class Tree implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1699841003462145355L;

    /**
     * 子节点(key:fileName,value:idx)
     */
    HashMap<String, String> child;

    /**
     * 返回Child 若无则创建一个空map
     */
    public HashMap<String, String> getChild(Boolean judge) {
        if(child == null) {
            child = new HashMap<>(2);
        }
        return getChild();
    }
}
