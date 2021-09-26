package com.mmt.redis.dao;

/**
 * @author nalin.sharma on 26/09/21
 */
public interface IMyRedisDao {
    public Integer get(String key);
    public Integer post(String key, int val);
    public Integer put(String key);
}
