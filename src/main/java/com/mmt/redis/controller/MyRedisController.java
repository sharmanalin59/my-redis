package com.mmt.redis.controller;

import com.mmt.redis.StringUtil;
import com.mmt.redis.dao.IMyRedisDao;
import com.mmt.redis.dto.Entry;
import com.mmt.redis.dto.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author nalin.sharma on 17/09/21
 */

@Controller
@RequestMapping("/my-redis")
public class MyRedisController {

    Logger logger = LoggerFactory.getLogger(MyRedisController.class);

    @Autowired
    @Qualifier("myRedisMapComputeDao")
    IMyRedisDao myRedisDao;


    @GetMapping(value = "/{key}/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<Integer>> getKey(@PathVariable("key") String key) {
        logger.info("getKey() for key - {}", key);
        if(StringUtil.isEmpty(key)) {
            logger.error("getKey() key is empty key- {}", key);
            return new ResponseEntity<>(
                    new Response<>(null, "key is empty",false),
                    HttpStatus.PRECONDITION_FAILED);
        }
        Integer val;
        try {
            if (Objects.isNull(val = myRedisDao.get(key))) {
                return new ResponseEntity<>(
                        new Response<>(null, "not found",true)
                        , HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e) {
            System.out.println("getKey() has exception "+e.getMessage());
            logger.error("getKey() for key - {} has exception - {}", key, e.getMessage());
            return new ResponseEntity<>(
                    new Response<>(null, "could not be created "+e.getMessage(),false),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(
                new Response<>(val, "key found",true)
                , HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<Object>> postEntry(@RequestBody Entry entry) {
        logger.info("postEntry() for key - {}, value - {}", entry.getKey(), entry.getValue());
        if(StringUtil.isEmpty(entry.getKey())) {
            logger.error("postEntry() key is empty entry.getKey()- {}", entry.getKey());
            return new ResponseEntity<>(
                    new Response<>(null, "could not be created, key is empty",false),
                    HttpStatus.PRECONDITION_FAILED);
        }
        if(Objects.isNull(entry.getValue())) {
            logger.error("postEntry() value is empty entry.getValue()- {}", entry.getValue());
            return new ResponseEntity<>(
                    new Response<>(null, "could not be created, value is empty",false),
                    HttpStatus.PRECONDITION_FAILED);
        }

        try {
            if (myRedisDao.post(entry.getKey(), entry.getValue()) == null) {
                return new ResponseEntity<>(
                        new Response<>(null, "created successfully",true),
                        HttpStatus.CREATED);
            }
        }
        catch (Exception e) {
            System.out.println("postEntry() has exception "+e.getMessage());
            logger.error("postEntry() has exception - {}", e.getMessage());
            return new ResponseEntity<>(
                    new Response<>(null, "could not be created "+e.getMessage(),false),
                    HttpStatus.CONFLICT
            );
        }
        return new ResponseEntity<>(
                new Response<>(null, "Key already exists!!",false),
                HttpStatus.CONFLICT
        );
    }

    @PutMapping(value = "/{key}/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<Integer>> put(@PathVariable("key") @NonNull String key) {

        logger.info("put() for key - {}", key);
        if(StringUtil.isEmpty(key)) {
            logger.error("put() key is empty entry.getKey()- {}", key);
            return new ResponseEntity<>(
                    new Response<>(null, "could not be updated, key is empty",false),
                    HttpStatus.PRECONDITION_FAILED);
        }

        Integer val;
        try {
            if (Objects.nonNull(val = myRedisDao.put(key))) {
                return new ResponseEntity<>(
                        new Response<>(val, "updated successfully",true),
                        HttpStatus.CREATED);
            }
        }
        catch (Exception e) {
            System.out.println("put() has exception "+e.getMessage());
            logger.error("put() has exception - {}", e.getMessage());
            return new ResponseEntity<>(
                    new Response<>(null, "could not be updated "+e.getMessage(),false),
                    HttpStatus.CONFLICT
            );
        }
        return new ResponseEntity<>(
                new Response<>(null, "Key does not exist!!",true),
                HttpStatus.CONFLICT
        );
    }
}
