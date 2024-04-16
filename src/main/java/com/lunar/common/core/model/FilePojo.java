package com.lunar.common.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件-与前端交互
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilePojo implements Serializable {

    /** 文件名 */
    private String name;

    /** uid */
    private String uid;

    /** 文件url */
    private String url;

}
