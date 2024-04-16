package com.lunar.system.entity;

import com.lunar.common.mybatis.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字典数据对象 sys_dict_type
 * 
 * @author liff
 * @date 2021-12-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("DictData")
public class DictData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "字典编码")
    private Long id;

    /** 字典排序 */
    @ApiModelProperty(value = "字典排序")
    private Integer dictSort;

    /** 字典标签 */
    @ApiModelProperty(value = "字典标签")
    private String dictLabel;

    /** 字典键值 */
    @ApiModelProperty(value = "字典键值")
    private String dictValue;

    /** 字典类型 */
    @ApiModelProperty(value = "字典类型")
    private String dictType;

    /** 0 无效 1 有效 */
    @ApiModelProperty(value = "0 无效 1 有效")
    private String status;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;

}
