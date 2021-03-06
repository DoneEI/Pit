package base.enums;

import base.utils.StringUtils;

/**
 * @Author: DoneEI
 * @Since: 2021/1/21 8:34 下午
 **/
public enum PitResultEnum {
    /**
     * 成功执行
     */
    SUCCESS("SUCCESS", "Success!"),

    /**
     * 出现错误
     */
    ERROR("ERROR", "Error!"),

    /**
     * 未知命令
     */
    UNKNOWN_COMMAND("UNKNOWN_COMMAND", "No such command!"),

    /**
     * 不合法命令选项
     */
    INVALID_OPTION("INVALID_OPTION", "No such option for this command!"),

    /**
     * 不合法文件路径
     */
    INVALID_FILE_PATH("INVALID_FILE_PATH", "file path did not match any files");

    /**
     * 枚举编码
     */
    private String code;

    /**
     * 枚举描述
     */
    private String description;

    PitResultEnum(String code, String description) {
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
    public PitResultEnum getEnumByCode(String code) {
        for (PitResultEnum oneEnum : PitResultEnum.values()) {
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
