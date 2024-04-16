package com.lunar.system.request;

import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.system.entity.FactorGas;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("FactorReq")
public class FactorReq extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 排放因子名称
     */
    @ApiModelProperty(value = "排放因子名称")
    @NotBlank(message = "请输入排放因子名称")
    private String name;

//    /**
//     * 二氧化碳当量数值
//     */
//    @ApiModelProperty(value = "二氧化碳当量数值")
//    private String factorValue;
//
//    /**
//     * 单位
//     */
//    @ApiModelProperty(value = "单位")
//    private String unit;

    /**
     * 一级分类（简体中文）
     */
    @ApiModelProperty(value = "一级分类（简体中文）")
    @NotBlank(message = "请选择一级分类")
    private String firstClassify;

    /**
     * 二级分类（简体中文）
     */
    @ApiModelProperty(value = "二级分类（简体中文）")
    @NotBlank(message = "请选择二级分类")
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
    @NotBlank(message = "请选择发布机构")
    private String institution;

    /**
     * 发布年份
     */
    @ApiModelProperty(value = "发布年份")
    @NotBlank(message = "请选择发布年份")
    private String year;

    /**
     * 来源类别-字典
     */
    @ApiModelProperty(value = "来源类别-字典")
    @NotBlank(message = "请选择来源类别")
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


    @ApiModelProperty(value = "气体列表")
    @NotEmpty(message = "气体列表不能为空")
    private List<FactorGas> gasList;

}
