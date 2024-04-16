package com.lunar.common.core.enums;

import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;

import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 日志模块类型
 *
 * @author szx
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum ModuleType implements BaseEnum {

    LOGIN(10, "系统登录"),
    COMPANY(11, "组织管理"),
    USER(12, "用户管理"),
    ROLE(13, "角色管理"),
    DICT(14, "数据字典"),
    UNIT_CONVERSION(16, "单位换算"),
    AUDIT(17, "审批设置"),
    FILE_LOG(18, "下载管理"),
    FACTOR(30, "排放因子"),
    COMPUTATION(40, "企业碳核算"),
    FOOTPRINT(50, "产品碳足迹"),
    SUPPLY_CHAIN(60, "供应链管理"),

//    LANG(19, "多语言"),
    ;

    public static void main(String[] args) {
        StringJoiner joiner = new StringJoiner("；");

        for (BaseEnum value : values()) {
            joiner.add(value.getCode() + " " + value.getName());
        }
        System.out.println("模块类型。" + joiner);
    }

    private static Map<Integer, ModuleType> CODE_MAP = Maps.newHashMap();

    static {
        for (ModuleType p : ModuleType.values()) {
            CODE_MAP.put(p.getCode(), p);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;

    ModuleType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ModuleType get(Integer code) {
        return CODE_MAP.get(code);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(code);
    }

}