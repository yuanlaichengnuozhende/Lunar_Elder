package com.lunar.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class I18nCache implements Serializable {

    /**
     * 字段标识
     */
    private String fieldKey;

    /**
     * 翻译
     */
    private String langValue;

}