package com.lunar.system.query;

import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.mybatis.query.Query;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
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
public class OperLogQuery implements Query {

    private Long id;
    private Long companyId;
    private OperType operType;
    private ModuleType moduleType;
    private String username;
    private String realName;
    private String ipAddr;
    private String content;
    private Long createBy;
    private Date createTime;

    private String likeUsername;
    private Long neId;

    private Collection<Long> idList;

    private Date startDate;
    private Date endDate;
}