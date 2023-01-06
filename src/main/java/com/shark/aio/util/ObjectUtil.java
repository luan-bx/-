package com.shark.aio.util;

import java.lang.reflect.Field;

public class ObjectUtil {

    public static boolean isEmpty(Object obj){
        if(obj==null) return true;
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f: fields){
            f.setAccessible(true);
            Object field = null;
            try {
                field  = f.get(obj);
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
            if (field!=null){
                return false;
            }
        }
        return true;
    }
}
