package com.lunar.common.aliyun.oss;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

/**
 * url模块路径
 *
 * @author szx
 * @date 2022/06/10 10:52
 */
public enum OSSPath {

    /** 测试 */
    TEST("test"),
    /** 账户SaaS */
    ACCOUNT_SAAS("account/saas"),
    /** 账户C端 */
    ACCOUNT_C("account/c"),

    ;

    private String name;

    private static Map<String, OSSPath> VALUE_MAP = Maps.newHashMap();

    static {
        for (OSSPath element : OSSPath.values()) {
            VALUE_MAP.put(element.getName(), element);
        }
        VALUE_MAP = Collections.unmodifiableMap(VALUE_MAP);
    }

    public static OSSPath get(String name) {
        return VALUE_MAP.get(name);
    }

    OSSPath(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
