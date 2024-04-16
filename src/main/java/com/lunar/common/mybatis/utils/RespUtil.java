package com.lunar.common.mybatis.utils;

import cn.hutool.json.JSONUtil;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.google.common.collect.Lists;
import com.lunar.common.mybatis.entity.BaseEntity;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author szx
 * @date 2022/12/05 11:16
 */
public class RespUtil {

    /**
     * 获取createBy列表（去重）
     */
    public static <T> List<Long> getCreateByList(List<T> entityList) {
        List<Long> idList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(entityList)) {
            return idList;
        }

        return entityList.stream()
            .map(x -> ((BaseEntity) x).getCreateBy())
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 获取updateBy列表（去重）
     */
    public static <T> List<Long> getUpdateByList(List<T> entityList) {
        List<Long> idList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(entityList)) {
            return idList;
        }

        return entityList.stream()
            .map(x -> ((BaseEntity) x).getUpdateBy())
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 获取updateBy列表（去重）
     */
    public static <T> List<Long> getUpdateByListPlus(List<T> entityList) {
        List<Long> idList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(entityList)) {
            return idList;
        }

        return entityList.stream()
            .map(a -> JSONUtil.parseObj(a).getLong("updateBy"))
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    }
}
