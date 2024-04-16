package com.lunar.common.aliyun.ding.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author szx
 * @date 2022/08/23 14:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DingMeetingOutput implements Serializable {

    private Boolean hasMore;
    private Long nextCursor;

    /** 会议id */
    private String confId;

    /** 会议时长，单位分钟 */
    private Integer confLenMin;

    private Date startTime;

    private Date endTime;

    /** 参与人数 */
    private Integer joinUserCount;

}
