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
public class LibAddressQuery implements Query {

    private Long id;
    private Integer addressCode;
    private String addressName;
    private Integer pCode;
    private Integer addressLevel;
    private Date createTime;
    private Date updateTime;

    private Collection<Long> idList;

    private Collection<Integer> codeList;
    // 地址级别<=ltLevel
    private Integer ltLevel;

}