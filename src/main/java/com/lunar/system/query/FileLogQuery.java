package com.lunar.system.query;

import com.lunar.common.core.enums.BizModule;
import com.lunar.common.core.enums.FileStatus;
import com.lunar.common.mybatis.query.Query;
import com.lunar.common.core.enums.BizModule;
import com.lunar.common.core.enums.FileStatus;
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
public class FileLogQuery implements Query {

    private Long id;
    private Long companyId;
    private Long orgId;
    private BizModule bizModule;
    private String fileName;
    private String fileUrl;
    private String filePath;
    private FileStatus fileStatus;
    private Boolean deleted;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;

    private Long neId;

    private Collection<Long> idList;
    private Collection<Long> orgIdList;

}