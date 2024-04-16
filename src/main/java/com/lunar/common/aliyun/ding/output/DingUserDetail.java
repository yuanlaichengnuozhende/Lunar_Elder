package com.lunar.common.aliyun.ding.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 钉钉用户详情
 *
 * @author szx
 * @date 2019/07/19 14:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DingUserDetail implements Serializable {

    /**
     * extension
     */
    @JsonProperty("extension")
    private String extension;
    /**
     * unionid
     */
    @JsonProperty("unionid")
    private String unionid;
    /**
     * boss
     */
    @JsonProperty("boss")
    private String boss;
    /**
     * exclusiveAccount
     */
    @JsonProperty("exclusive_account")
    private String exclusiveAccount;
    /**
     * managerUserid
     */
    @JsonProperty("manager_userid")
    private String managerUserid;
    /**
     * admin
     */
    @JsonProperty("admin")
    private String admin;
    /**
     * remark
     */
    @JsonProperty("remark")
    private String remark;
    /**
     * title
     */
    @JsonProperty("title")
    private String title;
    /**
     * hiredDate
     */
    @JsonProperty("hired_date")
    private String hiredDate;
    /**
     * userid
     */
    @JsonProperty("userid")
    private String userid;
    /**
     * orgEmailType
     */
    @JsonProperty("org_email_type")
    private String orgEmailType;
    /**
     * workPlace
     */
    @JsonProperty("work_place")
    private String workPlace;
    /**
     * realAuthed
     */
    @JsonProperty("real_authed")
    private String realAuthed;
    /**
     * deptIdList
     */
    @JsonProperty("dept_id_list")
    private String deptIdList;
    /**
     * jobNumber
     */
    @JsonProperty("job_number")
    private String jobNumber;
    /**
     * email
     */
    @JsonProperty("email")
    private String email;
    /**
     * mobile
     */
    @JsonProperty("mobile")
    private String mobile;
    /**
     * active
     */
    @JsonProperty("active")
    private String active;
    /**
     * telephone
     */
    @JsonProperty("telephone")
    private String telephone;
    /**
     * avatar
     */
    @JsonProperty("avatar")
    private String avatar;
    /**
     * hideMobile
     */
    @JsonProperty("hide_mobile")
    private String hideMobile;
    /**
     * senior
     */
    @JsonProperty("senior")
    private String senior;
    /**
     * orgEmail
     */
    @JsonProperty("org_email")
    private String orgEmail;
    /**
     * name
     */
    @JsonProperty("name")
    private String name;
    /**
     * stateCode
     */
    @JsonProperty("state_code")
    private String stateCode;

}
