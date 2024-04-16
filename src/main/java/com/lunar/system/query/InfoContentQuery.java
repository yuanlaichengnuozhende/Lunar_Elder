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
public class InfoContentQuery implements Query {

    private Long id;
    private Long infoId;
    private String attachment;
    private String content;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;


    private Collection<Long> idList;

}