package com.lunar.system.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author szx
 * @date 2022/01/11 15:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("CaptchaResp")
public class CaptchaResp implements Serializable {

    @ApiModelProperty(value = "uuid")
    private String uuid;

    @ApiModelProperty(value = "base64验证码")
    private String img;


}
