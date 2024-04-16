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
@ApiModel("RolePermission")
public class RolePermission extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id")
    private Long roleId;

    /**
     * 权限id
     */
    @ApiModelProperty(value = "权限id")
    private Long permissionId;

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
