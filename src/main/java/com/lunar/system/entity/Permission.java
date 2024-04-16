package com.lunar.system.entity;

import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.system.enums.MenuType;
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
@ApiModel("Permission")
public class Permission extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 权限名称
     */
    @ApiModelProperty(value = "权限名称")
    private String permissionName;

    /**
     * 父权限id
     */
    @ApiModelProperty(value = "父权限id")
    private Long pid;

    /**
     * 路由地址
     */
    @ApiModelProperty(value = "路由地址")
    private String path;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @ApiModelProperty(value = "菜单类型（M目录 C菜单 F按钮）")
    private MenuType menuType;

    /**
     * 权限标识
     */
    @ApiModelProperty(value = "权限标识")
    private String perms;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;

    /**
     * 菜单图标
     */
    @ApiModelProperty(value = "菜单图标")
    private String icon;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 标记删除。0 未删除 1 已删除
     */
    @ApiModelProperty(value = "标记删除。0 未删除 1 已删除")
    private Boolean deleted;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    private Long createBy;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    private Long updateBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
