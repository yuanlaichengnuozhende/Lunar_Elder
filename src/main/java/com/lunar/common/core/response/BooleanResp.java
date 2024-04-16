package com.lunar.common.core.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("BooleanResp")
public class BooleanResp implements Serializable {

    @ApiModelProperty(value = "true是 false否")
    private Boolean result;

}
