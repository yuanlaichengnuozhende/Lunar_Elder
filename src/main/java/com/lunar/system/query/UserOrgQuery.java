package com.lunar.system.query;

import com.lunar.common.core.enums.OptionType;
import com.lunar.common.mybatis.query.Query;
import com.lunar.common.core.enums.OptionType;
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
public class UserOrgQuery implements Query {

    private Long id;
    private Long userId;
    private Long orgId;
    private OptionType optionType;
    private Long createBy;
    private Date createTime;

    private Collection<Long> idList;

}