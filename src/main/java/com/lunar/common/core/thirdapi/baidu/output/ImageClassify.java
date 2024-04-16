package com.lunar.common.core.thirdapi.baidu.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author szx
 * @date 2022/07/06 11:21
 */
@NoArgsConstructor
@Data
public class ImageClassify {

    /**
     * 错误码
     */
    @JsonProperty("error_code")
    private int errcode;

    /**
     * 错误提示信息
     */
    @JsonProperty("error_msg")
    private String errmsg;

    /**
     * 唯一的log id，用于问题定位
     */
    @JsonProperty("log_id")
    private Long logId;

    /**
     * 返回结果数目，及result数组中的元素个数，最多返回5个结果
     */
    @JsonProperty("result_num")
    private Integer resultNum;

    /**
     * 标签结果数组
     */
    @JsonProperty("result")
    private List<Result> result;

    @NoArgsConstructor
    @Data
    public static class Result {

        /**
         * 图片中的物体或场景名称
         */
        @JsonProperty("keyword")
        private String keyword;

        /**
         * 置信度，0-1
         */
        @JsonProperty("score")
        private Double score;

        /**
         * 识别结果的上层标签，有部分钱币、动漫、烟酒等tag无上层标签
         */
        @JsonProperty("root")
        private String root;
    }
}
