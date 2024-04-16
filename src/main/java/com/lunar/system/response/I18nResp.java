package com.lunar.system.response;

import com.lunar.common.core.enums.I18nType;
import com.lunar.system.model.I18nLangModel;
import com.lunar.common.core.enums.I18nType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("I18nResp")
public class I18nResp implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 所属产品。1 通用 2 错误码 3 系统管理 4 碳因子库 5 组织碳核算 6 产品碳阻迹 7 供应链 8 碳账户 9 降碳课题
     */
    @ApiModelProperty(value = "所属产品。1 通用 2 错误码 3 系统管理 4 碳因子库 5 组织碳核算 6 产品碳阻迹 7 供应链 8 碳账户 9 降碳课题")
    private I18nType i18nType;

    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    /**
     * 字段标识
     */
    @ApiModelProperty(value = "字段标识")
    private String fieldKey;

//    /**
//     * 英文
//     */
//    @ApiModelProperty(value = "英文")
//    private String fieldNameEn;

    @ApiModelProperty(value = "语言列表")
    private List<I18nLangModel> langList;
}