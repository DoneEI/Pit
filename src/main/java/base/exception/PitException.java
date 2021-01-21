package base.exception;

import base.enums.PitResultEnum;

/**
 * @Author: DoneEI
 * @Since: 2021/1/21 8:33 下午
 **/
public class PitException extends RuntimeException {
    /**
     * 错误码
     */
    private PitResultEnum errorCode;

    /**
     * 错误描述
     */
    private String errorMsg;

    /**
     * 错误码构造器
     *
     * @param errorCode
     *            错误码
     */
    public PitException(PitResultEnum errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 常用构造器
     *
     * @param errorMsg
     *            异常信息
     * @param errorCode
     *            错误码
     */
    public PitException(PitResultEnum errorCode, String errorMsg) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public PitResultEnum getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
