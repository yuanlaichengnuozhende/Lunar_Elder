package com.lunar.system.entity;

import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.system.enums.InfoStatus;
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
@ApiModel("Info")
public class Info extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 公司id
     */
    @ApiModelProperty(value = "公司id")
    private Long companyId;

    /**
     * 资讯类型
     */
    @ApiModelProperty(value = "资讯类型")
    private String infoType;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 资讯状态。0 待发布；1 已发布；2 已下架
     */
    @ApiModelProperty(value = "资讯状态。0 待发布；1 已发布；2 已下架")
    private InfoStatus infoStatus;

    /**
     * 发布时间
     */
    @ApiModelProperty(value = "发布时间")
    private Date publishTime;

    /**
     * 发布人
     */
    @ApiModelProperty(value = "发布人")
    private Long publishBy;

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

    @ApiModelProperty(value = "发布人名称")
    private String publishByName;

    @ApiModelProperty(value = "附件列表")
    private String attachment;

    @ApiModelProperty(value = "资讯内容")
    private String content;

}
