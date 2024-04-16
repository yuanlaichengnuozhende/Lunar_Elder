package com.lunar.system.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author szx
 * @date 2022/01/11 15:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("LoginReq")
public class LoginReq implements Serializable {

    @ApiModelProperty(value = "登录码")
    private String companyCode;

    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "请输入用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "请输入密码")
    private String password;

    @ApiModelProperty(value = "语言")
//    @NotBlank(message = "请选择语言")
    private String lang;

    @ApiModelProperty(value = "图形验证码uuid")
    private String uuid;

    @ApiModelProperty(value = "图形验证码code")
    private String code;

    private String requestStr;

}
