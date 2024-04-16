package com.lunar.system.entity;

import com.lunar.common.core.enums.ImportStatus;
import com.lunar.common.core.enums.ImportType;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.core.enums.ImportStatus;
import com.lunar.common.core.enums.ImportType;
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
@ApiModel("ImportLog")
public class ImportLog extends BaseEntity {

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
     * 导入内容。1 供应商；2 采购产品
     */
    @ApiModelProperty(value = "导入内容。1 供应商；2 采购产品")
    private ImportType importType;

    /**
     * 导入时间
     */
    @ApiModelProperty(value = "导入时间")
    private Date importTime;

    /**
     * 完成时间
     */
    @ApiModelProperty(value = "完成时间")
    private Date doneTime;

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private String fileName;

    /**
     * 文件url
     */
    @ApiModelProperty(value = "文件url")
    private String fileUrl;

    /**
     * 异常数据url
     */
    @ApiModelProperty(value = "异常数据url")
    private String failedFileUrl;

    /**
     * 总条数
     */
    @ApiModelProperty(value = "总条数")
    private Integer totalCount;

    /**
     * 成功条数
     */
    @ApiModelProperty(value = "成功条数")
    private Integer successCount;

    /**
     * 失败条数
     */
    @ApiModelProperty(value = "失败条数")
    private Integer failedCount;

    /**
     * 导入状态。0 导入中 1 已导入 -1 导入失败
     */
    @ApiModelProperty(value = "导入状态。0 导入中 1 已导入 -1 导入失败")
    private ImportStatus importStatus;

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

    @ApiModelProperty(value = "操作人")
    private String createByName;

}
