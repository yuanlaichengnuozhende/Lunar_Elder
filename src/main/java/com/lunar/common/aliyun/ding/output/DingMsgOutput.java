package com.lunar.common.aliyun.ding.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 钉钉发送工作通知
 *
 * @author szx
 * @date 2022/08/17 13:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DingMsgOutput implements Serializable {

    private static final long serialVersionUID = -4686624049730910606L;

    private Long errcode;

    private String errmsg;

    private Long taskId;

    private String requestId;

}
