package com.lunar.common.core.thirdapi.wx.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户手机号
 *
 * @author szx
 * @date 2019/07/19 14:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxPhonenumber implements Serializable {

    private static final long serialVersionUID = -4660405978587401283L;

    /**
     * 错误码
     */
    private int errcode;

    /**
     * 错误提示信息
     */
    private String errmsg;

    /**
     * 用户手机号信息
     */
    @JsonProperty("phone_info")
    private PhoneInfo phoneInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhoneInfo {

        /**
         * 用户绑定的手机号（国外手机号会有区号）
         */
        private String phoneNumber;

        /**
         * 没有区号的手机号
         */
        private String purePhoneNumber;

        /**
         * 区号
         */
        private int countryCode;

    }

}
