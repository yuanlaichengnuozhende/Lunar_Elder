package com.lunar.system.request;

import com.lunar.common.core.enums.OrgType;
import com.lunar.common.core.enums.OrgType;
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
@ApiModel("OrgReq")
public class OrgReq implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 上级部门id
     */
    @ApiModelProperty(value = "上级部门id")
    @NotNull(message = "请输入上级部门id")
    private Long pid;

    /**
     * 组织类型。0 单体组织；1 集团；2 子组织；3 部门
     */
    @ApiModelProperty(value = "组织类型。0 单体组织；1 集团；2 子组织；3 部门")
    @NotNull(message = "请选择组织类型")
    private OrgType orgType;

    /**
     * 组织名称
     */
    @ApiModelProperty(value = "组织名称")
    @NotBlank(message = "请输入组织名称")
    private String orgName;

    /**
     * 组织编码
     */
    @ApiModelProperty(value = "组织编码")
    @NotBlank(message = "请输入组织编码")
    private String orgCode;

    /**
     * 组织简称
     */
    @ApiModelProperty(value = "组织简称")
    @NotBlank(message = "请输入组织简称")
    private String orgAbbr;

    /**
     * 组织信息
     */
    @ApiModelProperty(value = "组织信息")
    private String orgInfo;

}
