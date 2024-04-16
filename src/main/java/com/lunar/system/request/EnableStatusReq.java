package com.lunar.system.request;

import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.core.enums.EnableStatus;
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
@ApiModel("EnableStatusReq")
public class EnableStatusReq implements Serializable {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "状态。0 启用 1 禁用")
    @NotNull(message = "状态不能为空")
    private EnableStatus status;

}