package com.lunar.system.response;

import com.lunar.system.entity.Org;
import com.lunar.system.entity.Role;
import com.lunar.system.enums.UserStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("UserDetailResp")
public class UserDetailResp implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 用户名（工号）
     */
    @ApiModelProperty(value = "用户名（工号）")
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
     * 上次登录时间
     */
    @ApiModelProperty(value = "上次登录时间")
    private Date lastLoginTime;

    /**
     * 用户状态。0 启用 1 禁用
     */
    @ApiModelProperty(value = "用户状态。0 启用 1 禁用")
    private UserStatus userStatus;

    @ApiModelProperty(value = "角色列表")
    private List<Role> roleList;

    @ApiModelProperty(value = "组织列表")
    private List<Org> orgList;

    @ApiModelProperty(value = "角色")
    private String roleNames;

    @ApiModelProperty(value = "组织")
    private String orgNames;

}