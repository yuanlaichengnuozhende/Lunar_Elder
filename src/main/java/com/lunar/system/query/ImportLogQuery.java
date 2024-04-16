package com.lunar.system.query;

import com.lunar.common.core.enums.ImportStatus;
import com.lunar.common.core.enums.ImportType;
import com.lunar.common.mybatis.query.Query;
import com.lunar.common.core.enums.ImportStatus;
import com.lunar.common.core.enums.ImportType;
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
public class ImportLogQuery implements Query {

    private Long id;
    private Long companyId;
    private Long orgId;
    private ImportType importType;
    private Date importTime;
    private Date doneTime;
    private String fileName;
    private String fileUrl;
    private String failedFileUrl;
    private Integer totalCount;
    private Integer successCount;
    private Integer failedCount;
    private ImportStatus importStatus;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;

    private Long neId;

    private Collection<Long> idList;
    private Collection<Long> orgIdList;

}