package com.lunar.system.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("DefaultPasswordReq")
public class DefaultPasswordReq implements Serializable {

    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "请输入新密码")
    private String newPassword;

}
