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

/**
 * 字符串列表
 */
public class ListStringHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
        throws SQLException {
        if (parameter != null) {
            //parameter.sort(String::compareTo);
            ps.setString(i, StringUtils.join(parameter, ","));
        } else {
            ps.setString(i, null);
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (StringUtils.isBlank(value)) {
            return Lists.newArrayList();
        }
        return Splitter.on(",").splitToList(value.trim());
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        if (StringUtils.isBlank(value)) {
            return Lists.newArrayList();
        }
        return Splitter.on(",").splitToList(value.trim());
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        if (StringUtils.isBlank(value)) {
            return Lists.newArrayList();
        }
        return Splitter.on(",").splitToList(value.trim());
    }
}
