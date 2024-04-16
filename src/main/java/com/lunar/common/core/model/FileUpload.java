package com.lunar.common.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件上传
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("FileUpload")
public class FileUpload implements Serializable {

    /** 文件名 */
    @ApiModelProperty(value = "文件名")
    private String fileName;

    /** filePath */
    @ApiModelProperty(value = "filePath")
    private String filePath;

    /** 外网url */
    @ApiModelProperty(value = "外网url")
    private String url;

    /** 内网url */
    @ApiModelProperty(value = "内网url")
    private String internalUrl;

}
