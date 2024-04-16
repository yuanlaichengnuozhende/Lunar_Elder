package com.lunar.common.aliyun.ding.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author szx
 * @date 2022/08/23 14:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DingMeetingUser implements Serializable {

    /** 会议id */
    private String confId;

    /** 参会时长（s） */
    public Integer attendDuration;

    public String staffId;

    public String unionId;

}
