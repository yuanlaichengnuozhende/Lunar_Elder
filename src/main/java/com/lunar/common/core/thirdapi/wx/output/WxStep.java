package com.lunar.common.core.thirdapi.wx.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 微信步数
 * <p>
 * { "stepInfoList": [ { "timestamp": 1445866601, "step": 100 }, { "timestamp": 1445876601, "step": 120 } ] }
 *
 * @author szx
 * @date 2019/07/19 14:33
 */
@Data
public class WxStep implements Serializable {

    private static final long serialVersionUID = -4592719359703763902L;

    /**
     * 步数列表
     */
    private List<StepInfo> stepInfoList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StepInfo {

        /**
         * 时间戳，表示数据对应的时间
         */
        private long countryCode;

        /**
         * 微信运动步数
         */
        private int step;
    }

}
