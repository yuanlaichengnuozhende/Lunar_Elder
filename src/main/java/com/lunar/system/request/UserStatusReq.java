package com.lunar.system.request;

import com.lunar.system.enums.UserStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("UserStatusReq")
public class UserStatusReq implements Serializable {

    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long id;

    @ApiModelProperty(value = "用户状态。0 启用 1 禁用")
    @NotNull(message = "用户状态不能为空")
    private UserStatus userStatus;

}