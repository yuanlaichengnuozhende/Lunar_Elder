package com.lunar.system.request;

import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.system.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("UserReq")
public class UserReq extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    @NotNull(message = "请输入用户名")
    private String username;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @NotNull(message = "请输入姓名")
    private String realName;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @NotNull(message = "请输入mobile")
    private String mobile;

    /**
     * 所属组织
     */
    @ApiModelProperty(value = "所属组织")
    @NotNull(message = "请选择组织")
    private String orgs;

    /**
     * 用户角色
     */
    @ApiModelProperty(value = "用户角色")
    @NotNull(message = "请至少选择一个角色")
    private String roles;

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
