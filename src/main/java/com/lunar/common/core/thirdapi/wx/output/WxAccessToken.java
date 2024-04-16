package com.lunar.common.core.thirdapi.wx.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 微信access_token
 *
 * @author szx
 * @date 2019/07/19 14:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxAccessToken implements Serializable {

    private static final long serialVersionUID = 2058040263956843465L;

    /**
     * 获取到的凭证
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 凭证有效时间，单位：秒。目前是7200秒之内的值。
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

}
