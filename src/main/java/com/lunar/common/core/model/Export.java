package com.lunar.common.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author szx
 * @date 2022/12/28 11:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("Export")
public class Export implements Serializable {

    @ApiModelProperty(value = "url")
    private String url;

    @ApiModelProperty(value = "fileName")
    private String fileName;

}
