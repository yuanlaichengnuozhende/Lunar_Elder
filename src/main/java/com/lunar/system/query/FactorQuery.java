package com.lunar.system.query;

import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.mybatis.query.Query;
import com.lunar.common.core.enums.EnableStatus;
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
public class FactorQuery implements Query {

    private Long id;
    private String name;
    private String factorValue;
    private String unit;
    private String firstClassify;
    private String secondClassify;
    private String sourceLanguage;
    private String sourceLanguageName;
    private String description;
    private String institution;
    private String year;
    private String sourceLevel;
    private String source;
    private String url;
    private EnableStatus status;
    private Boolean deleted;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;

    private Collection<Long> idList;

    private String likeName;
    private String likeInstitution;
    private String likeDescription;

}