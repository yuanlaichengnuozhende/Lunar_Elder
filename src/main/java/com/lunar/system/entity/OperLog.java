package com.lunar.system.entity;

import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
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
@ApiModel("OperLog")
public class OperLog extends BaseEntity {

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
     * 操作类型。1 登录 2 下载 3 功能操作
     */
    @ApiModelProperty(value = "操作类型。1 登录 2 下载 3 功能操作")
    private OperType operType;

    /**
     * 操作模块
     */
    @ApiModelProperty(value = "操作模块")
    private ModuleType moduleType;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String realName;

    /**
     * ip地址
     */
    @ApiModelProperty(value = "ip地址")
    private String ipAddr;

    /**
     * 操作内容
     */
    @ApiModelProperty(value = "操作内容")
    private String content;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    private Long createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
