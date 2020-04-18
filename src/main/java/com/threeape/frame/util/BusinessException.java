package com.threeape.frame.util;

/**
 * @Desc:
 * @Author: bill
 * @Date:
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 4573943707326511509L;

	private int errorCode;

    private Object[] args;

    private String message;

    private BusinessException linkedException;

    public BusinessException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(int errorCode, Object[] args) {
        super();
        this.errorCode = errorCode;
        this.args = args;
    }

    public BusinessException(ErrorInfo errorInfo) {
        this.errorCode = errorInfo.getCode();
        this.message = errorInfo.getZhMsg();
    }

    public BusinessException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
    /**
     * add at 2018-8-1 15:46:15
     * @since 2.1.3
     * @param message
     * @param throwable
     */
    public BusinessException(String message, Throwable throwable) {
        super(message, throwable);
        this.message = message;
    }
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BusinessException getLinkedException() {
        return linkedException;
    }

    public void setLinkedException(BusinessException linkedException) {
        this.linkedException = linkedException;
    }
}
