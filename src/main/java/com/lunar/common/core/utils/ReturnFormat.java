package com.lunar.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 返回对象格式化，格式化单对象
 *
 */
@Slf4j
public class ReturnFormat {

    /**
     * 格式化单体对象
     * @param object
     * @param propertyNames
     * @return
     */
    public static Map<String,Object> oneModelFormat(Object object,String... propertyNames){
        Map<String,Object> returnMap = new HashMap<>();

        if(object!=null){
            //当前对象属性
            Field[] fields = object.getClass().getDeclaredFields();
            convert(fields,returnMap,object,propertyNames);

            //处理父类字段
            Class superClazz = object.getClass().getSuperclass();
            convert(superClazz.getDeclaredFields(),returnMap,object,propertyNames);
        }

        return returnMap;
    }

    /**
     * 格式化集合对象
     * @param object
     * @param propertyNames
     * @return
     */
    public static List<Map<String,Object>> arrayModelFormat(List object, String... propertyNames){
        List<Map<String,Object>> maps = new ArrayList<>();
        for (Object o : object) {
            maps.add(oneModelFormat(o,propertyNames));
        }
        return maps;
    }


    public static void convert(Field[] fields,Map<String,Object> returnMap,Object object,String... propertyNames){

        for (Field field : fields) {
            if (ArrayUtils.contains(propertyNames,field.getName())){
                field.setAccessible(true);
                try {
                    returnMap.put(field.getName(),field.get(object));
                } catch (IllegalAccessException e) {
                    log.error("ReturnFormat error", e);
                }
            }
        }

    }
}
