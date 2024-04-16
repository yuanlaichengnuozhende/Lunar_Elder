package com.lunar.common.core.thirdapi.wx.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author szx
 * @date 2022/06/20 14:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxPhonenumberInput implements Serializable {

    /**
     * 手机号获取凭证
     * <p>
     * code换取用户手机号。 每个 code 只能使用一次，code的有效期为5min
     */
    private String code;

}
