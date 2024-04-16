package com.lunar.system.entity;

import com.lunar.common.core.enums.I18nType;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.core.enums.I18nType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("I18n")
public class I18n extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 所属产品。1 通用 2 错误码 3 系统管理
     */
    @ApiModelProperty(value = "所属产品。1 通用 2 错误码 3 系统管理")
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

    /**
     * 英文
     */
    @ApiModelProperty(value = "英文")
    private String fieldNameEn;

    /**
     * 标记删除。0 未删除 1 已删除
     */
    @ApiModelProperty(value = "标记删除。0 未删除 1 已删除")
    private Boolean deleted;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    private Long createBy;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    private Long updateBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "更新者用户名")
    private String updateByName;

}
