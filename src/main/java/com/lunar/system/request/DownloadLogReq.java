package com.lunar.system.request;

import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.ModuleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("DownloadLogReq")
public class DownloadLogReq implements Serializable {

    @ApiModelProperty(value = "模块类型", required = true)
    @NotNull(message = "模块类型不能为空")
    private ModuleType moduleType;

    @ApiModelProperty(value = "日志内容", required = true)
    @NotBlank(message = "日志内容不能为空")
    private String content;

    @ApiModelProperty(value = "文件url")
    private String url;

}
