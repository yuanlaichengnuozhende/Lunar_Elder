package com.lunar.common.core.utils.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 节点树
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tree implements Serializable {

    private long code;

    private String name;

    private long pcode;

    @JsonIgnore
    private long sortId;

    /**
     * 不返回空数组
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Tree> children;

}
