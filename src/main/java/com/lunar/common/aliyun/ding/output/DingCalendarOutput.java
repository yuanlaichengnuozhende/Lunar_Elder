package com.lunar.common.aliyun.ding.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author szx
 * @date 2022/08/17 13:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DingCalendarOutput implements Serializable {

    /**
     * 日程id
     */
    private String calendarId;

    /**
     * 日程标题
     */
    private String summary;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 时长（min）
     */
    private Integer duration;

//    /**
//     * 是否钉钉会议
//     */
//    private Boolean onlineMeetingInfo;

}
