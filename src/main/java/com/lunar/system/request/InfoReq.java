package com.lunar.system.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("InfoReq")
public class InfoReq implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

//    /**
//     * 公司id
//     */
//    @ApiModelProperty(value = "公司id")
//    private Long companyId;

    /**
     * 资讯类型
     */
    @ApiModelProperty(value = "资讯类型")
    @NotBlank(message = "请选择资讯类型")
    private String infoType;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    @NotBlank(message = "请输入标题")
    private String title;

    /**
     * 附件列表
     */
    @ApiModelProperty(value = "附件列表")
    private String attachment;

    /**
     * 资讯内容
     */
    @ApiModelProperty(value = "资讯内容")
    @NotBlank(message = "请输入资讯内容")
    private String content;

}
