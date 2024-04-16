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
@ApiModel("DictDataResp")
public class DictDataResp implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    /** 字典类型 */
    @ApiModelProperty(value = "字典类型")
    private String dictType;

    /** 字典排序 */
    @ApiModelProperty(value = "字典排序")
    private Integer dictSort;

    /** 字典标签 */
    @ApiModelProperty(value = "字典标签")
    private String dictLabel;

    /** 字典键值 */
    @ApiModelProperty(value = "字典键值")
    private String dictValue;

}