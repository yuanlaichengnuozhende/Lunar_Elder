package com.lunar.system.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("DictTypeResp")
public class DictTypeResp implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    /** 字典类型 */
    @ApiModelProperty(value = "字典类型")
    private String dictType;

    /** 字典名称 */
    @ApiModelProperty(value = "字典名称")
    private String dictName;

}