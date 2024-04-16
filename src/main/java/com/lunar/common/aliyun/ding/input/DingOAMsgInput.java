package com.lunar.common.aliyun.ding.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 钉钉发送工作通知-oa消息
 *
 * @author szx
 * @date 2019/07/19 14:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DingOAMsgInput implements Serializable {

    private static final long serialVersionUID = -1536508181292951575L;

    // "oa":{
    //            "body":{
    //                "form":[
    //                    {
    //                        "value":"2022.10.22-2022.11.22",
    //                        "key":"活动时间："
    //                    },
    //                    {
    //                        "value":"保定长城汽车部件园保定热系统活动室激励理论",
    //                        "key":"活动地点："
    //                    },
    //                    {
    //                        "value":"2022.10.15-2022.10.20",
    //                        "key":"报名时间："
    //                    },
    //                    {
    //                        "value":"",
    //                        "key":"请通过员工碳账户-活动-进入活动列表  \n(2022.10.24 16:30:30)"
    //                    }
    //                ],
    //                "title":"活动水电费进来了大概已发布"
    //            },
    //            "head":{
    //                "bgcolor":"FFBBBBBB",
    //                "text":"员工碳账户"
    //            }
    //        }

    @JsonProperty("head")
    private Head head;

    @JsonProperty("body")
    private Body body;

    @NoArgsConstructor
    @Data
    public static class Body {

        @JsonProperty("form")
        private List<Form> formList;

        @JsonProperty("title")
        private String title;

        @NoArgsConstructor
        @Data
        public static class Form {

            @JsonProperty("value")
            private String value;

            @JsonProperty("key")
            private String key;
        }
    }

    @NoArgsConstructor
    @Data
    public static class Head {

        @JsonProperty("bgcolor")
        private String bgcolor;

        @JsonProperty("text")
        private String text;
    }

}
