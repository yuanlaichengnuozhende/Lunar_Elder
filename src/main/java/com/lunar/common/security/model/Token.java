package com.lunar.common.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author szx
 * @date 2022/03/03 20:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token implements Serializable {

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * 过期时间（分钟）
     */
    private Long expiresIn;

}
