package com.lunar.system.model;

import com.lunar.common.core.enums.OrgType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lunar.common.core.enums.OrgType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 节点树
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgTree implements Serializable {

    @ApiModelProperty(value = "组织id")
    private long code;

    @ApiModelProperty(value = "组织名称")
    private String name;

    @ApiModelProperty(value = "上级部门id")
    private long pcode;

    @ApiModelProperty(value = "组织类型。0 单体组织；1 集团；2 子组织；3 部门")
    private OrgType orgType;

    private String region;

    @JsonIgnore
    private long sortId;

    /**
     * 不返回空数组
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<OrgTree> children;

    @ApiModelProperty(value = "更新者")
    private Long updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "组织简称")
    private String abbr;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者名称")
    private String updateByName;

    @ApiModelProperty(value = "组织编码")
    private String orgCode;

    @ApiModelProperty(value = "组织层级。从1开始")
    private int level;

}
