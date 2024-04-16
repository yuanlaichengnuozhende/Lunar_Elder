package com.lunar.system.request;

import com.lunar.system.model.I18nLangModel;
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
@ApiModel("SysI18nLangReq")
public class I18nLangReq implements Serializable {

    /**
     * 字段标识
     */
    @ApiModelProperty(value = "字段标识")
    @NotBlank(message = "字段标识不能为空")
    private String fieldKey;

    @ApiModelProperty(value = "语言列表")
    private List<I18nLangModel> langList;

}