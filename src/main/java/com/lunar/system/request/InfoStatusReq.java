package com.lunar.system.request;

import com.lunar.system.enums.InfoStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("InfoStatusReq")
public class InfoStatusReq implements Serializable {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 资讯状态。0 待发布；1 已发布；2 已下架
     */
    @ApiModelProperty(value = "资讯状态。0 待发布；1 已发布；2 已下架")
    @NotNull(message = "请选择资讯状态")
    private InfoStatus infoStatus;

}
