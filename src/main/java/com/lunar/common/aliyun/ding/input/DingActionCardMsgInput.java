package com.lunar.common.aliyun.ding.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 钉钉发送工作通知-卡片消息-整体跳转ActionCard样式
 *
 * @author szx
 * @date 2019/07/19 14:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DingActionCardMsgInput implements Serializable {

    private static final long serialVersionUID = -8409392582964473337L;

//    {
//        "markdown":"新的活动内容时代峰峻凉快圣诞节飞力达手机发啦收到垃圾分类时代峰峻",
//            "single_url":"http://dev.xxx.net/?corpId=$CORPID$",
//            "title":"碳账户活动通知",
//            "single_title":"跳转到碳账户应用"
//    }

    @JsonProperty("markdown")
    private String markdown;

    @JsonProperty("single_url")
    private String singleUrl;

    @JsonProperty("title")
    private String title;

    @JsonProperty("single_title")
    private String singleTitle;

}
