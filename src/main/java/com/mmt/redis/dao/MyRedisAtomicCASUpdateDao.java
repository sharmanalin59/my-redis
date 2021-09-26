package com.mmt.redis.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author nalin.sharma on 25/09/21
 */
@Repository("myRedisAtomicCASUpdateDao")
public class MyRedisAtomicCASUpdateDao implements IMyRedisDao {

    Logger logger = LoggerFactory.getLogger(MyRedisAtomicCASUpdateDao.class);

    Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

    public Integer get(String key) {
        if(!map.containsKey(key)) {
            return null;
        }
        return map.get(key).get();
    }

    public Integer post(String key, int val) {
        AtomicInteger oldVal = map.putIfAbsent(key, new AtomicInteger(val));
        if(oldVal == null) {
            return null;
        }
        return oldVal.get();
    }

    public Integer put(String key) {
        try {
            AtomicInteger value;
            if (Objects.isNull(value = map.get(key))) {
                return null;
            }
            //this should work as we don't have remove method removing key,
            //but if remove was there there could arise null pointer exception as
            //between 2 steps map.get(), value.addAndGet entry could be removed from CHM
            //so let's catch exception. This is mere assumption, could be wrong.
            return value.addAndGet(1);
        }catch (NullPointerException e) {
            logger.error("MyRedisAtomicCASUpdateDao put NullPointerException " +
                    "exception - {}, perhaps the key was removed",e.getMessage());
            return null;
        }
        catch (Exception e) {
            logger.error("MyRedisAtomicCASUpdateDao " +
                    "put exception - {}, perhaps the key was removed",e.getMessage());
            return null;
        }
    }
}
