package com.lunar.system.entity;

import com.lunar.common.core.enums.OrgType;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.core.enums.OrgType;
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
@ApiModel("Org")
public class Org extends BaseEntity {

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
     * 组织简称
     */
    @ApiModelProperty(value = "组织简称")
    private String orgAbbr;

    /**
     * 组织信息
     */
    @ApiModelProperty(value = "组织信息")
    private String orgInfo;

    /**
     * 组织路径
     */
    @ApiModelProperty(value = "组织路径")
    private String orgPath;

    /**
     * 上级部门id
     */
    @ApiModelProperty(value = "上级部门id")
    private Long pid;

    /**
     * 组织类型。0 单体组织 1 集团 2 子组织 3 部门
     */
    @ApiModelProperty(value = "组织类型。0 单体组织 1 集团 2 子组织 3 部门")
    private OrgType orgType;

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

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者名称")
    private String updateByName;

}
