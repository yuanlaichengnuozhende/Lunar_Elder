package com.lunar.common.core.thirdapi.baidu.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 百度access_token
 *
 * @author szx
 * @date 2019/07/19 14:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaiduAccessToken implements Serializable {

    private static final long serialVersionUID = -8516560819418767582L;

    /**
     * 获取到的凭证
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 凭证有效时间(秒为单位，有效期30天)。
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

}
