package com.lunar.common.aliyun.ding.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 钉钉用户信息
 *
 * @author szx
 * @date 2019/07/19 14:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DingUserInfo implements Serializable {

    private static final long serialVersionUID = 7120044744967643819L;

    /**
     * associatedUnionid
     */
    @JsonProperty("associated_unionid")
    private String associatedUnionid;

    /**
     * unionid
     */
    @JsonProperty("unionid")
    private String unionid;

    /**
     * deviceId
     */
    @JsonProperty("device_id")
    private String deviceId;

    /**
     * sysLevel
     */
    @JsonProperty("sys_level")
    private Integer sysLevel;

    /**
     * 用户名字
     */
    @JsonProperty("name")
    private String name;

    /**
     * sys
     */
    @JsonProperty("sys")
    private Boolean sys;

    /**
     * userid
     */
    @JsonProperty("userid")
    private String userid;

}
