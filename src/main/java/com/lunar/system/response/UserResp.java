package com.lunar.system.response;

import com.lunar.common.core.convert.serializer.EmailMosaicJsonSerializer;
import com.lunar.common.core.convert.serializer.MobileMosaicJsonSerializer;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.system.entity.Org;
import com.lunar.system.entity.Role;
import com.lunar.system.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lunar.common.core.convert.serializer.EmailMosaicJsonSerializer;
import com.lunar.common.core.convert.serializer.MobileMosaicJsonSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("UserResp")
public class UserResp extends BaseEntity {

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
     * 用户名（工号）
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String realName;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @JsonSerialize(using = MobileMosaicJsonSerializer.class)
    private String mobile;

    /**
     * email
     */
    @ApiModelProperty(value = "email")
    @JsonSerialize(using = EmailMosaicJsonSerializer.class)
    private String email;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 上次登录时间
     */
    @ApiModelProperty(value = "上次登录时间")
    private Date lastLoginTime;

    /**
     * 用户状态。0 启用 1 禁用
     */
    @ApiModelProperty(value = "用户状态。0 启用 1 禁用")
    private UserStatus userStatus;

    /**
     * 是否默认密码
     */
    @ApiModelProperty(value = "是否默认密码")
    private Boolean defaultPassword;

    /**
     * 标记删除。0 未删除 1 已删除
     */
    @ApiModelProperty(value = "标记删除。0 未删除 1 已删除")
    private Boolean deleted;

    /**
     * 是否负责人。0 否 1 是
     */
    @ApiModelProperty(value = "是否负责人。0 否 1 是")
    private Boolean adminFlag;

    /**
     * 创建者
     */
    @JsonIgnore
    private Long createBy;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    private Long updateBy;

    /**
     * 创建时间
     */
    @JsonIgnore
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @JsonIgnore
    @ApiModelProperty(value = "角色列表")
    private List<Role> roleList;

    @JsonIgnore
    @ApiModelProperty(value = "组织列表")
    private List<Org> orgList;

    @ApiModelProperty(value = "角色")
    private String roleNames;

    @ApiModelProperty(value = "组织")
    private String orgNames;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者名称")
    private String updateByName;

}