package com.lunar.common.core.thirdapi.wx.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户wx session
 *
 * @author szx
 * @date 2019/07/19 14:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxSession implements Serializable {

    private static final long serialVersionUID = 3403444919369584119L;

    /**
     * 微信session_key
     */
    @JsonProperty("session_key")
    private String sessionKey;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 微信unionid
     */
    private String unionid;

}
