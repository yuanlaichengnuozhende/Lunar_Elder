package com.lunar.system.query;

import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.core.enums.OrgType;
import com.lunar.common.mybatis.query.Query;
import com.lunar.common.core.enums.EnableStatus;
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
public class CompanyQuery implements Query {

    private Long id;
    private String companyName;
    private String companyCode;
    private String uscc;
    private String adminUsername;
    private String adminRealName;
    private String adminMobile;
    private OrgType companyType;
    private Long roleId;
    private EnableStatus companyStatus;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;

    private Long neId;

    private Collection<Long> idList;

}