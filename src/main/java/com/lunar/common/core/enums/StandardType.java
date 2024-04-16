package com.lunar.common.core.enums;

import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 标准类型
 *
 * @author szx
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum StandardType implements BaseEnum {

    GHG(1, "GHG Protocol", 3),
    ISO14064(2, "ISO 14064-1:2018", 6),

    ;

    public static void main(String[] args) {
        StringJoiner joiner = new StringJoiner("；");
        for (BaseEnum value : values()) {
            joiner.add(value.getCode() + " " + value.getName());
        }
        System.out.println("标准类型。" + joiner);
    }

    private static Map<Integer, StandardType> CODE_MAP = Maps.newHashMap();

    static {
        for (StandardType p : StandardType.values()) {
            CODE_MAP.put(p.getCode(), p);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;
    /** 分类数量 */
    private int categoryNum;

    StandardType(int code, String name, int categoryNum) {
        this.code = code;
        this.name = name;
        this.categoryNum = categoryNum;
    }

    public static StandardType get(Integer code) {
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

    public int getCategoryNum() {
        return categoryNum;
    }

    public static String getNames(String codes) {
        if (StringUtils.isBlank(codes)) {
            return "";
        }

        StringJoiner joiner = new StringJoiner("、");
        if (codes.contains(String.valueOf(GHG.code))) {
            joiner.add(GHG.name);
        }
        if (codes.contains(String.valueOf(ISO14064.code))) {
            joiner.add(ISO14064.name);
        }

        return joiner.toString();
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(code);
    }

}