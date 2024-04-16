package com.lunar.system.query;

import com.lunar.common.mybatis.query.Query;
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
public class LibUnitConversionQuery implements Query {

    private Long id;
    private String unitClass;
    private String unitFrom;
    private String unitTo;
    private String unitValue;
    private Boolean deleted;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;

    private Long neId;

    private Collection<Long> idList;

    private String unit;

}