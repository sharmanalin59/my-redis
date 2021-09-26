package com.mmt.redis.dao;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nalin.sharma on 25/09/21
 */

@Repository("myRedisMapComputeDao")
public class MyRedisMapComputeDao implements IMyRedisDao {
    Map<String, Integer> map = new ConcurrentHashMap<>();

    public Integer get(String key) {
        if(!map.containsKey(key)) {
            return null;
        }
        return map.get(key);
    }

    public Integer post(String key, int val) {
        return map.putIfAbsent(key, val);
    }

    public Integer put(String key) {
        if(!map.containsKey(key)) {
            return null;
        }
        return map.computeIfPresent(key, (k, v) -> {
            return v+1;
        });
    }
}
