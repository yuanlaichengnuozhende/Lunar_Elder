package com.lunar.system.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * @author szx
 * @date 2022/01/11 15:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("loginToken")
public class TokenResp implements Serializable {

    @ApiModelProperty(value = "userId")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "姓名")
    private String realName;

    @ApiModelProperty(value = "当前语言")
    private String lang;

    @ApiModelProperty(value = "accessToken")
    private String accessToken;

    @ApiModelProperty(value = "过期时间")
    private Long expiresIn;

    @ApiModelProperty(value = "是否默认密码")
    private Boolean defaultPassword;

    @ApiModelProperty(value = "公司id")
    private Long companyId;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "用户当前组织id")
    private Long orgId;

    @ApiModelProperty(value = "用户当前组织名称")
    private String orgName;

    @ApiModelProperty(value = "权限perms列表")
    private Set<String> permissions;

    @ApiModelProperty(value = "角色id列表")
    private Set<String> roles;

//    @ApiModelProperty(value = "组织类型列表。1 集团 2 业务线")
//    private Set<Integer> orgTypes;

}
