package com.mmt.redis.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author nalin.sharma on 25/09/21
 */
@Repository("myRedisConcurrentUpdatePerformantDao")
public class MyRedisConcurrentUpdatePerformantDao implements IMyRedisDao {

    Logger logger = LoggerFactory.getLogger(MyRedisConcurrentUpdatePerformantDao.class);

    Map<String, LongAdder> map = new ConcurrentHashMap<>();

    public Integer get(String key) {
        LongAdder val;
        if(Objects.isNull(val = map.get(key))) {
            return null;
        }
        return val.intValue();
    }

    public Integer post(String key, int intVal) {
        LongAdder value = new LongAdder();
        value.add(intVal);
        LongAdder oldVal = map.putIfAbsent(key, value);
        if(oldVal == null) {
            return null;
        }
        return oldVal.intValue();
    }

    public Integer put(String key) {
        try {
            LongAdder value;
        if(Objects.isNull(value = map.get(key))) {
            return null;
        }
        //this should work as we don't have remove method removing key,
        //but if remove was there there could arise null pointer exception as
        //between 2 steps map.get(), value.increment() entry could be removed from CHM
        //so let's catch exception. This is mere assumption, could be wrong.
        value.increment();
        return value.intValue();//let's not find the latest value from CHM and instead just return oldValue+1 (performance reason)
    }catch (NullPointerException e) {
        logger.error("MyRedisConcurrentUpdatePerformantDao put NullPointerException " +
                "exception - {}, perhaps the key was removed",e.getMessage());
        throw e;
    } catch (Exception e) {
        logger.error("MyRedisConcurrentUpdatePerformantDao " +
                "put exception - {}, perhaps the key was removed",e.getMessage());
        throw e;
    }
    }
}
