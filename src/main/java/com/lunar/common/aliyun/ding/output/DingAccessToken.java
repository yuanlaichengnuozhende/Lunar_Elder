package com.lunar.common.aliyun.ding.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 钉钉access_token
 *
 * @author szx
 * @date 2019/07/19 14:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DingAccessToken implements Serializable {

    private static final long serialVersionUID = 7448780086535707674L;

    /**
     * 获取到的凭证
     */
    private String accessToken;

    /**
     * access_token的有效期为7200秒（2小时），有效期内重复获取会返回相同结果并自动续期，过期后获取会返回新的access_token。
     */
    private Long expiresIn;

}
