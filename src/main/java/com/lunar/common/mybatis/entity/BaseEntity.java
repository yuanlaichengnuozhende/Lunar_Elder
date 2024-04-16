package com.lunar.common.mybatis.entity;

import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BaseEntity implements Serializable {

    /**
     * id
     */
    protected Long id;

    /**
     * 创建者
     */
    protected Long createBy;

    /**
     * 更新者
     */
    protected Long updateBy;

    /**
     * 创建时间
     */
    protected Date createTime;

    /**
     * 更新时间
     */
    protected Date updateTime;

    public static <E> List<Long> getIdList(List<E> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream().map(e -> ((BaseEntity) e).getId()).collect(Collectors.toList());
    }

}
