package com.lunar.system.entity;

import com.lunar.common.core.enums.OptionType;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.core.enums.OptionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("UserOrg")
public class UserOrg extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 组织id
     */
    @ApiModelProperty(value = "组织id")
    private Long orgId;

    /**
     * 选择类型。1 全选 2 半选
     */
    @ApiModelProperty(value = "选择类型。1 全选 2 半选")
    private OptionType optionType;

    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id")
    private Long createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
