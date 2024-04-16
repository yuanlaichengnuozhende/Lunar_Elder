package com.lunar.system.entity;

import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.system.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ApiModel("User")
public class User extends BaseEntity {

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
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String realName;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 上次登录时间
     */
    @ApiModelProperty(value = "上次登录时间")
    private Date lastLoginTime;

    /**
     * 用户状态。0 启用 1 禁用
     */
    @ApiModelProperty(value = "用户状态。0 启用 1 禁用")
    private UserStatus userStatus;

    /**
     * 是否默认密码
     */
    @ApiModelProperty(value = "是否默认密码")
    private Boolean defaultPassword;

    /**
     * 是否负责人。0 否 1 是
     */
    @ApiModelProperty(value = "是否负责人。0 否 1 是")
    private Boolean adminFlag;

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

}
