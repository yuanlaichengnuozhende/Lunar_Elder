package com.lunar.system.entity;

import com.lunar.common.mybatis.entity.BaseEntity;
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
@ApiModel("LibUnitConversion")
public class LibUnitConversion extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 单位类别
     */
    @ApiModelProperty(value = "单位类别")
    private String unitClass;

    /**
     * 单位from
     */
    @ApiModelProperty(value = "单位from")
    private String unitFrom;

    /**
     * 单位to
     */
    @ApiModelProperty(value = "单位to")
    private String unitTo;

    /**
     * 换算比例
     */
    @ApiModelProperty(value = "换算比例")
    private String unitValue;

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

    @ApiModelProperty(value = "更新者名称")
    private String updateByName;

    @ApiModelProperty(value = "单位类别 name")
    private String unitClassName;

    @ApiModelProperty(value = "单位from name")
    private String unitFromName;

    @ApiModelProperty(value = "单位to name")
    private String unitToName;

}
