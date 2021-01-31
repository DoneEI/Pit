package entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Blob 对象
 *
 * @Author: DoneEI
 * @Since: 2021/1/21 3:02 下午
 **/
@Data
public class Blob implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5890163400723288240L;

    /**
     * 文件内容 (字节流)
     */
    byte[] fileContent;

    /**
     * 文件内容长度
     */
    Long fileContentLen;
}
