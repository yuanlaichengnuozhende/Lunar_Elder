package com.lunar.system.request;

import com.google.common.collect.Lists;
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
@ApiModel("UserOrgReq")
public class UserOrgReq implements Serializable {

    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long id;

    @ApiModelProperty(value = "全选id列表")
    private List<Long> allCheckedList = Lists.newArrayList();

    @ApiModelProperty(value = "半选id列表")
    private List<Long> halfCheckedList = Lists.newArrayList();

}
