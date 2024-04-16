package com.lunar.system.query;

import com.lunar.common.core.enums.OrgType;
import com.lunar.common.mybatis.query.Query;
import com.lunar.common.core.enums.OrgType;
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
public class OrgQuery implements Query {

    private Long id;
    private Long companyId;
    private String orgName;
    private String orgCode;
    private String orgAbbr;
    private String orgInfo;
    private String orgPath;
    private Long pid;
    private OrgType orgType;
    private Boolean deleted;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;

    private Collection<Long> idList;

    private String likeOrgName;

    /**
     * 路径所有 like 'xxx%'
     */
    private String likeOrgPath;

}