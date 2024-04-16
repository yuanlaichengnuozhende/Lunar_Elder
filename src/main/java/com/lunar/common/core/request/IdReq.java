package com.lunar.common.core.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("IdReq")
public class IdReq implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id最小值为 1")
    private Long id;

}