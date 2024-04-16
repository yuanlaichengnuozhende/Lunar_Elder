package com.lunar.common.core.utils.tree;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("树")
public class TreeSelect implements Serializable {

    @ApiModelProperty(value = "树")
    private List<Tree> tree;

    @ApiModelProperty(value = "全选id列表")
    private List<Long> allCheckedList = Lists.newArrayList();

    @ApiModelProperty(value = "半选id列表")
    private List<Long> halfCheckedList = Lists.newArrayList();

}
