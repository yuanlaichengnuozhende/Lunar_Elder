package com.lunar.system.entity;

import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.core.enums.EnableStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("Factor")
public class Factor extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 排放因子名称
     */
    @ApiModelProperty(value = "排放因子名称")
    private String name;

    /**
     * 二氧化碳当量数值
     */
    @ApiModelProperty(value = "二氧化碳当量数值")
    private String factorValue;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 一级分类（简体中文）
     */
    @ApiModelProperty(value = "一级分类（简体中文）")
    private String firstClassify;

    /**
     * 二级分类（简体中文）
     */
    @ApiModelProperty(value = "二级分类（简体中文）")
    private String secondClassify;

    /**
     * 源语言。字典值 1中文 2英语 3法语 4德语
     */
    @ApiModelProperty(value = "源语言。字典值 1中文 2英语 3法语 4德语")
    private String sourceLanguage;

    /**
     * 源语言名称
     */
    @ApiModelProperty(value = "源语言名称")
    private String sourceLanguageName;

    /**
     * 场景描述
     */
    @ApiModelProperty(value = "场景描述")
    private String description;

    /**
     * 发布机构(全称)-字典
     */
    @ApiModelProperty(value = "发布机构(全称)-字典")
    private String institution;

    /**
     * 发布年份
     */
    @ApiModelProperty(value = "发布年份")
    private String year;

    /**
     * 来源类别-字典
     */
    @ApiModelProperty(value = "来源类别-字典")
    private String sourceLevel;

    /**
     * 来源文件名称
     */
    @ApiModelProperty(value = "来源文件名称")
    private String source;

    /**
     * 网址/文献
     */
    @ApiModelProperty(value = "网址/文献")
    private String url;

    /**
     * 0 启用 1 禁用
     */
    @ApiModelProperty(value = "0 启用 1 禁用")
    private EnableStatus status;

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

    @ApiModelProperty(value = "更新者名称")
    private String updateByName;

    @ApiModelProperty(value = "气体列表")
    private List<FactorGas> gasList;

}
