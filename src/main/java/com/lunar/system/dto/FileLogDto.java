package com.lunar.system.dto;

import com.lunar.common.core.enums.BizModule;
import com.lunar.common.core.enums.FileStatus;
import com.lunar.common.core.enums.BizModule;
import com.lunar.common.core.enums.FileStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileLogDto implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 公司id
     */
    @ApiModelProperty(value = "公司id")
    private Long companyId;

    /**
     * 组织id
     */
    @ApiModelProperty(value = "组织id")
    private Long orgId;

    /**
     * 功能模块
     */
    @ApiModelProperty(value = "功能模块")
    private BizModule bizModule;

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private String fileName;

    /**
     * 文件外网url
     */
    @ApiModelProperty(value = "文件外网url")
    private String fileUrl;

    /**
     * 文件path
     */
    @ApiModelProperty(value = "文件path")
    private String filePath;

    /**
     * 文件状态。0 生成中 1 已生成 -1 生成失败
     */
    @ApiModelProperty(value = "文件状态。0 生成中 1 已生成 -1 生成失败")
    private FileStatus fileStatus;

    private Integer reportType;

}
