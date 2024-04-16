package com.lunar.system.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("SysI18nLang")
public class I18nLangModel implements Serializable {

    /**
     * 字段标识
     */
    @ApiModelProperty(value = "字段标识")
    private String fieldKey;

    /**
     * 语言（字典值）
     */
    @ApiModelProperty(value = "语言（字典值）")
    private String langDict;

    /**
     * 翻译
     */
    @ApiModelProperty(value = "翻译")
    private String langValue;

}