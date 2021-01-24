package entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Blob 对象
 * @Author: DoneEI
 * @Since: 2021/1/21 3:02 下午
 **/
@Data
public class Blob implements Serializable {
    /**
     * 文件内容
     */
    String fileContent;

    /**
     * 文件内容长度
     */
    String fileContentLen;
}
