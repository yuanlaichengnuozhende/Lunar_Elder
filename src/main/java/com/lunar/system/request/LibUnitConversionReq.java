package com.lunar.system.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("LibUnitConversionReq")
public class LibUnitConversionReq implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 单位类别
     */
    @ApiModelProperty(value = "单位类别")
    @NotNull(message = "请选择单位类别")
    private String unitClass;

    /**
     * 单位from
     */
    @ApiModelProperty(value = "单位from")
    @NotNull(message = "请选择单位from")
    private String unitFrom;

    /**
     * 单位to
     */
    @ApiModelProperty(value = "单位to")
    @NotNull(message = "请选择单位to")
    private String unitTo;

    /**
     * 换算比例
     */
    @ApiModelProperty(value = "换算比例")
    @NotBlank(message = "请输入换算比例")
    private String unitValue;

}
