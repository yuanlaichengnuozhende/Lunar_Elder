package com.lunar.common.aliyun.ding.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 根据第三方回调token获取用户信息
 *
 * @author szx
 * @date 2022/09/07 18:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DingUserCallBack implements Serializable {

    public String avatarUrl;
    public String email;
    public String mobile;
    public String nick;
    public String openId;
    public String stateCode;
    public String unionId;

}
