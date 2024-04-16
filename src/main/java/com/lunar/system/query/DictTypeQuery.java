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
public class
DictTypeQuery implements Query {

    private Long id;
    private String dictType;
    private String dictName;
    private Character status;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;

    private Collection<Long> idList;

    private String likeDictName;

}