package com.lunar.system.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author szx
 * @date 2022/01/11 15:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("MobileReq")
public class MobileReq implements Serializable {

    @ApiModelProperty(value = "登录码")
    private String companyCode;

    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "请输入手机号")
    private String mobile;

    /**
     * 短信验证码
     */
    @ApiModelProperty(value = "短信验证码")
    @NotBlank(message = "请输入短信验证码")
    private String vfcode;

    @ApiModelProperty(value = "语言")
//    @NotBlank(message = "请选择语言")
    private String lang;

}
