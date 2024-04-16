package com.lunar.system.request;

import com.lunar.system.enums.MenuType;
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
@ApiModel("PermissionReq")
public class PermissionReq implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 权限名称
     */
    @ApiModelProperty(value = "权限名称")
    @NotBlank(message = "请输入权限名称")
    private String permissionName;

    /**
     * 父权限id
     */
    @ApiModelProperty(value = "父权限id")
    @NotNull(message = "请输入父权限id")
    private Long pid;

    /**
     * 路由地址
     */
    @ApiModelProperty(value = "路由地址")
    @NotBlank(message = "请输入路由地址")
    private String path;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @ApiModelProperty(value = "菜单类型（M目录 C菜单 F按钮）")
    @NotNull(message = "请输入菜单类型")
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

}
