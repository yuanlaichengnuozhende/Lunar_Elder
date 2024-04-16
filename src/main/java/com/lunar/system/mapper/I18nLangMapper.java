package com.lunar.system.mapper;

import com.lunar.common.mybatis.mapper.BaseMapper;
import com.lunar.system.entity.I18nLang;

public interface I18nLangMapper extends BaseMapper<I18nLang> {

    /**
     * 标记删除
     *
     * @param fieldKey
     * @return
     */
    int markDelete(String fieldKey);

}