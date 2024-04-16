package com.lunar.common.mybatis.type.handler;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * long列表
 */
public class ListLongHandler extends BaseTypeHandler<List<Long>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType)
        throws SQLException {
        if (parameter != null) {
            parameter.sort(Long::compareTo);
            ps.setString(i, StringUtils.join(parameter, ","));
        } else {
            ps.setString(i, null);
        }
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (StringUtils.isBlank(value)) {
            return Lists.newArrayList();
        }
        return Splitter.on(",").splitToList(value.trim())
            .stream().map(Long::parseLong).collect(Collectors.toList());
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        if (StringUtils.isBlank(value)) {
            return Lists.newArrayList();
        }
        return Splitter.on(",").splitToList(value.trim())
            .stream().map(Long::parseLong).collect(Collectors.toList());
    }

    @Override
    public List<Long> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        if (StringUtils.isBlank(value)) {
            return Lists.newArrayList();
        }
        return Splitter.on(",").splitToList(value.trim())
            .stream().map(Long::parseLong).collect(Collectors.toList());
    }
}
