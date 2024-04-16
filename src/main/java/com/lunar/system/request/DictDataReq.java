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
@ApiModel("DictDataReq")
public class DictDataReq implements Serializable {

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

    /** 字典排序 */
    @ApiModelProperty(value = "字典排序")
    @NotNull(message = "请输入字典排序")
    private Integer dictSort;

    /** 字典标签 */
    @ApiModelProperty(value = "分类名称")
    @NotBlank(message = "请输入分类名称")
    private String dictLabel;

    /** 字典键值 */
    @ApiModelProperty(value = "分类标识")
    @NotBlank(message = "请输入分类标识")
    private String dictValue;

}
