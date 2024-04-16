package com.lunar.system.query;

import com.lunar.common.mybatis.query.Query;
import com.lunar.system.enums.InfoStatus;
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
public class InfoQuery implements Query {

    private Long id;
    private Long companyId;
    private String infoType;
    private String title;
    private InfoStatus infoStatus;
    private Date publishTime;
    private Long publishBy;
    private Boolean deleted;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;

    private String likeTitle;
    private Long neId;

    private Collection<Long> idList;

}