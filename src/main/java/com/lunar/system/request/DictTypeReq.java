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
@ApiModel("DictTypeReq")
public class DictTypeReq implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 字典标识
     */
    @ApiModelProperty(value = "字典标识")
    @NotBlank(message = "请输入字典标识")
    private String dictType;

    /**
     * 字典名称
     */
    @ApiModelProperty(value = "字典名称")
    @NotBlank(message = "请输入字典名称")
    private String dictName;

}
