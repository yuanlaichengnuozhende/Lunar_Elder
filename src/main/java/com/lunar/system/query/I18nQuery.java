package com.lunar.system.query;

import com.lunar.common.core.enums.I18nType;
import com.lunar.common.mybatis.query.Query;
import com.lunar.common.core.enums.I18nType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class I18nQuery implements Query {

    private Long id;
    private I18nType i18nType;
    private String fieldName;
    private String fieldKey;
    private String fieldNameEn;
    private Boolean deleted;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;

    private Collection<Long> idList;

    private String likeFieldKey;
    private String likeFieldName;

}