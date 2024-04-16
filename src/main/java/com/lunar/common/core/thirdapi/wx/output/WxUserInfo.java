package com.lunar.common.core.thirdapi.wx.output;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信用户数据
 * <p>
 * {"nickName":"Band","gender":1,"language":"zh_CN","city":"Guangzhou","province":"Guangdong","country":"CN","avatarUrl":"http://wx.qlogo.cn/mmopen/vi_32/1vZvI39NWFQ9XM4LtQpFrQJ1xlgZxx3w7bQxKARol6503Iuswjjn6nIGBiaycAjAtpujxyzYsrztuuICqIM5ibXQ/0"}HyVFkGl5F5OQWJZZaNzBBg==
 *
 * @author szx
 * @date 2019/07/19 14:33
 */
@Data
public class WxUserInfo implements Serializable {

    private static final long serialVersionUID = 6635641110057899498L;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

}
