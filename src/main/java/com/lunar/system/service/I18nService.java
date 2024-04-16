package com.lunar.system.service;

import com.lunar.common.mybatis.service.BaseService;
import com.lunar.system.entity.I18n;
import com.lunar.system.request.I18nLangReq;

import java.util.Map;

public interface I18nService extends BaseService<I18n> {

    /**
     * 编辑
     *
     * @param i18n
     * @param req
     * @return
     */
    I18n edit(I18n i18n, I18nLangReq req);

    /**
     * 重建语言缓存
     *
     * @return
     */
    void reCache();

    /**
     * 查询语言缓存
     *
     * @param lang
     * @return
     */
    Map<String, String> getCache(String lang);

}
