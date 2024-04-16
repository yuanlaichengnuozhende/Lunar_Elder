package com.lunar.common.core.enums;

import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * ISO分类
 *
 * @author szx
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum ISOCategory implements BaseEnum {

    CAT1(1, "直接排放或清除", Lists.newArrayList(1), "直接排放"),
    CAT2(2, "能源间接排放", Lists.newArrayList(2), "间接排放"),
    CAT3(3, "运输间接排放", Lists.newArrayList(3), "间接排放"),
    CAT4(4, "外购产品或服务间接排放", Lists.newArrayList(3), "间接排放"),
    CAT5(5, "供应链下游排放", Lists.newArrayList(3), "间接排放"),
    CAT6(6, "其他间接排放", Lists.newArrayList(3), "间接排放"),

    ;

    public static void main(String[] args) {
        StringJoiner joiner = new StringJoiner("；");
        for (BaseEnum value : values()) {
            joiner.add(value.getCode() + " " + value.getName());
        }
        System.out.println("ISO分类。" + joiner);
    }

    private static Map<Integer, ISOCategory> CODE_MAP = Maps.newHashMap();

    static {
        for (ISOCategory p : ISOCategory.values()) {
            CODE_MAP.put(p.getCode(), p);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;
    private List<Integer> relList;
    private String type;

    ISOCategory(int code, String name, List<Integer> relList, String type) {
        this.code = code;
        this.name = name;
        this.relList = relList;
        this.type = type;
    }

    public static ISOCategory get(Integer code) {
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

    public List<Integer> getRelList() {
        return relList;
    }

    public String getType() {
        return type;
    }

    public static String[] getNameArray() {
        return Arrays.stream(ISOCategory.values()).map(ISOCategory::getName).toArray(String[]::new);
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(code);
    }

}