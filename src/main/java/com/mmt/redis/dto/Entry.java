package com.mmt.redis.dto;

/**
 * @author nalin.sharma on 25/09/21
 */
public class Entry {
    private String key;
    private Integer value;

    public String getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    public Entry(String key, Integer value) {
        this.key = key;
        this.value = value;
    }
}
