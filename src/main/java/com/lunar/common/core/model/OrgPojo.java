package com.lunar.common.core.model;

import com.lunar.common.core.enums.OrgType;
import io.swagger.annotations.ApiModel;
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
@ApiModel("OrgPojo")
public class OrgPojo implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 组织名称
     */
    @ApiModelProperty(value = "组织名称")
    private String orgName;

    /**
     * 组织编码
     */
    @ApiModelProperty(value = "组织编码")
    private String orgCode;

    /**
     * 上级部门id
     */
    @ApiModelProperty(value = "上级部门id")
    private Long pid;

    /**
     * 组织类型。0 单体组织；1 集团；2 子组织；3 部门；
     */
    @ApiModelProperty(value = "组织类型。0 单体组织；1 集团；2 子组织；3 部门；")
    private OrgType orgType;

    /**
     * 组织路径
     */
    @ApiModelProperty(value = "组织路径")
    private String orgPath;

}