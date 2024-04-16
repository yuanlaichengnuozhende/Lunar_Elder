package com.lunar.common.mybatis.type.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * json类型转换
 */
@Slf4j
@MappedJdbcTypes(value = {JdbcType.VARCHAR}, includeNullJdbcType = true)
public class JSONTypeHandler<E> extends BaseTypeHandler<E> {

    private final Class<E> type;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // 未知字段忽略
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 不使用科学计数
        MAPPER.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        // null 值不输出(节省内存)
        MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        // 配置输出 反序列化类名的形式
//        MAPPER.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    /**
     * 设置配置文件设置的转换类以及枚举类内容，供其他方法更便捷高效的实现
     *
     * @param type 配置文件中设置的转换类
     */
    public JSONTypeHandler(Class<E> type) {
        Objects.requireNonNull(type);
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        if (Objects.isNull(parameter)) {
            ps.setString(i, null);
            return;
        }

        ps.setString(i, toJson(parameter));
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toObject(rs.getString(columnName), type);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toObject(rs.getString(columnIndex), type);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toObject(cs.getString(columnIndex), type);
    }

    /**
     * object 转 json
     *
     * @param obj 对象
     * @return String json字符串
     */
    private String toJson(E obj) {
        if (Objects.isNull(obj)) {
            return null;
        }

        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("mybatis column to json error,obj:" + obj, e);
        }
    }

    /**
     * 转换对象
     *
     * @param json  json数据
     * @param clazz 类
     * @return E
     */
    private E toObject(String json, Class<E> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("mybatis column json to object error,json:{}", json, e);
            return null;
        }
    }

}
