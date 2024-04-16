package com.lunar.common.mybatis.model;

/**
 * @author szx
 * @date 2023/02/21 15:33
 */
public enum OrderDirection {

    ASC("asc"),
    DESC("desc");

    private String key;

    OrderDirection(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
