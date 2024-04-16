package com.lunar.system.entity;

import com.lunar.common.core.enums.BizModule;
import com.lunar.common.core.enums.FileStatus;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lunar.common.core.enums.BizModule;
import com.lunar.common.core.enums.FileStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("FileLog")
public class FileLog extends BaseEntity {

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

    /**
     * 标记删除。0 未删除 1 已删除
     */
    @ApiModelProperty(value = "标记删除。0 未删除 1 已删除")
    private Boolean deleted;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    private Long createBy;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    private Long updateBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建者名称")
    private String createByName;

    @ApiModelProperty(value = "更新者名称")
    private String updateByName;

    @ApiModelProperty(value = "组织名称")
    private String orgName;

    @JsonIgnore
    private Integer reportType;

}
