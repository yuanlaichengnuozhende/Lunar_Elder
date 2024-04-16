package com.lunar.system.request;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("RoleReq")
public class RoleReq implements Serializable {

    @ApiModelProperty(value = "角色id")
    private Long id;

    @ApiModelProperty(value = "角色名称")
    @NotBlank(message = "请输入角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色描述")
    @NotBlank(message = "请输入角色描述")
    private String roleInfo;

    @ApiModelProperty(value = "全选id列表")
    private List<Long> allCheckedList = Lists.newArrayList();

    @ApiModelProperty(value = "半选id列表")
    private List<Long> halfCheckedList = Lists.newArrayList();

}
