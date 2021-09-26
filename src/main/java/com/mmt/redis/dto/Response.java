package com.mmt.redis.dto;

/**
 * @author nalin.sharma on 25/09/21
 */
public class Response<T> {
    private T val;
    private String message;
    private boolean success;

    public Response(T val, String message, boolean success) {
        this.val = val;
        this.message = message;
        this.success = success;
    }

    public T getVal() {
        return val;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setVal(T val) {
        this.val = val;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
