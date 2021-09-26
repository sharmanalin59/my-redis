package com.mmt.redis;

/**
 * @author nalin.sharma on 25/09/21
 */
public class StringUtil {
    public static boolean isEmpty(String str) {
        if(str == null || str.equals("")) {
            return true;
        }
        return false;
    }
}
