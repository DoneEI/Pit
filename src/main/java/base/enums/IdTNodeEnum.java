package base.enums;

import base.utils.StringUtils;

/**
 * 暂存区 树节点
 *
 * @Author: DoneEI
 * @Since: 2021/1/24 10:44 下午
 **/
public enum IdTNodeEnum {

    /**
     * 文件节点
     */
    FILE_NODE("FILE_NODE", "file"),

    /**
     * 文件夹节点
     */
    DIRECTORY_NODE("DIRECTORY_NODE", "directory")

    ;

    /**
     * 枚举编码
     */
    private String code;

    /**
     * 枚举描述
     */
    private String description;

    IdTNodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code 获取对应枚举
     *
     * @param code
     *            枚举code
     * @return 有则返回对应枚举，无则返回null
     */
    public IdTNodeEnum getEnumByCode(String code) {
        for (IdTNodeEnum oneEnum : IdTNodeEnum.values()) {
            if (StringUtils.equal(code, oneEnum.getCode())) {
                return oneEnum;
            }
        }
        return null;
    }

    /**
     * Get the value of code
     *
     * @return the value of code
     */
    public String getCode() {
        return code;
    }

    /**
     * Set the code
     *
     * @param code
     *            code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Get the value of description
     *
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description
     *
     * @param description
     *            description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
