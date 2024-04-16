package com.lunar.system.entity;

import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.core.enums.OrgType;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.core.enums.EnableStatus;
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
@ApiModel("Company")
public class Company extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 组织名称
     */
    @ApiModelProperty(value = "组织名称")
    private String companyName;

    /**
     * 登录代码
     */
    @ApiModelProperty(value = "登录代码")
    private String companyCode;

    /**
     * 社会信用代码
     */
    @ApiModelProperty(value = "社会信用代码")
    private String uscc;

    /**
     * 负责人账号
     */
    @ApiModelProperty(value = "负责人账号")
    private String adminUsername;

    /**
     * 负责人姓名
     */
    @ApiModelProperty(value = "负责人姓名")
    private String adminRealName;

    /**
     * 负责人手机号
     */
    @ApiModelProperty(value = "负责人手机号")
    private String adminMobile;

    /**
     * 组织类型。0 单体组织 1 集团
     */
    @ApiModelProperty(value = "组织类型。0 单体组织 1 集团")
    private OrgType companyType;

    /**
     * 超管角色id
     */
    @ApiModelProperty(value = "超管角色id")
    private Long roleId;

    /**
     * 组织状态。0 启用 1 禁用
     */
    @ApiModelProperty(value = "组织状态。0 启用 1 禁用")
    private EnableStatus companyStatus;

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

}
