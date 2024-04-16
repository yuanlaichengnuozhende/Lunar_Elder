package com.lunar.system.response;

import com.lunar.system.entity.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("角色详情页")
public class RoleResp implements Serializable {

    /**
     * 角色信息
     */
    @ApiModelProperty(value = "角色信息")
    private Role role;

    /**
     * 权限id列表
     */
    @ApiModelProperty(value = "子节点权限id列表")
    @NotNull(message = "权限id列表不能为空")
    private List<Integer> keys;

    /**
     * 权限id列表
     */
    @ApiModelProperty(value = "父节点权限id列表")
    @NotNull(message = "权限id列表不能为空")
    private List<Integer> mainKeys;

}