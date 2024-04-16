package com.lunar.system.entity;

import com.lunar.common.mybatis.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【请填写功能名称】对象 c_dict_type
 * 
 * @author liff
 * @date 2021-12-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("DictType")
public class DictType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @ApiModelProperty(value = "id")
    private Long id;

    /** 字典类型 */
    @ApiModelProperty(value = "字典类型")
    private String dictType;

    /** 字典名称 */
    @ApiModelProperty(value = "字典名称")
    private String dictName;

    /** 0 无效 1 有效 */
    @ApiModelProperty(value = "0 无效 1 有效")
    private String status;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;

}
