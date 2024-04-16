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
@ApiModel("LibAddress")
public class LibAddress extends BaseEntity {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 地址code
     */
    @ApiModelProperty(value = "地址code")
    private Integer addressCode;

    /**
     * 地址名称
     */
    @ApiModelProperty(value = "地址名称")
    private String addressName;

    /**
     * 父级地址code
     */
    @ApiModelProperty(value = "父级地址code")
    private Integer pCode;

    /**
     * 地址级别(0-一级，1-二级，2-三级，3-四级)
     */
    @ApiModelProperty(value = "地址级别(0-一级，1-二级，2-三级，3-四级)")
    private Integer addressLevel;

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

}
