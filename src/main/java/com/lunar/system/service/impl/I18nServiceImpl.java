package com.lunar.system.service.impl;

import com.lunar.common.core.consts.Consts;
import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.common.redis.utils.I18nUtil;
import com.lunar.common.redis.utils.RedisUtil;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.I18n;
import com.lunar.system.entity.I18nLang;
import com.lunar.system.mapper.I18nLangMapper;
import com.lunar.system.mapper.I18nMapper;
import com.lunar.system.model.I18nLangModel;
import com.lunar.system.query.I18nLangQuery;
import com.lunar.system.query.I18nQuery;
import com.lunar.system.request.I18nLangReq;
import com.lunar.system.service.I18nService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lunar.common.core.consts.Consts;
import com.lunar.system.service.I18nService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class I18nServiceImpl extends BaseServiceImpl<I18nMapper, I18n>
    implements I18nService {

    @Autowired
    private I18nLangMapper i18nLangMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public I18n edit(I18n i18n, I18nLangReq req) {
        log.info("编辑字段语言. i18n={}, req={}", i18n, req);
        Long userId = SecurityUtils.getUserId();

        List<I18nLangModel> langList =
            CollectionUtils.isEmpty(req.getLangList()) ? Lists.newArrayList() : req.getLangList();

        // 英语
        String en = langList.stream()
            .filter(x -> Consts.EN_KEY.equals(x.getLangDict()))
            .findFirst()
            .orElse(I18nLangModel.builder().langValue("").build())
            .getLangValue();
        if (!StringUtils.equals(i18n.getFieldNameEn(), en)) {
            i18n.setFieldNameEn(en);
        }
        i18n.setUpdateBy(userId);
        this.updateSelective(i18n);
        log.info("语言配置表更新完成");

        // 其他语言
        List<I18nLangModel> otherLang = langList.stream()
            .filter(x -> !StringUtils.isAnyBlank(x.getLangDict(), x.getLangValue())
                && !Consts.EN_KEY.equals(x.getLangDict()))
            .collect(Collectors.toList());
        // 删除
        i18nLangMapper.markDelete(i18n.getFieldKey());
        Date now = new Date();
        if (CollectionUtils.isNotEmpty(otherLang)) {
            List<I18nLang> entityList = otherLang.stream()
                .map(x -> I18nLang.builder()
                    .fieldKey(i18n.getFieldKey())
                    .langDict(x.getLangDict())
                    .langValue(x.getLangValue())
                    .deleted(false)
                    .createBy(userId)
                    .createTime(now)
                    .build())
                .collect(Collectors.toList());
            i18nLangMapper.batchInsert(entityList);
            log.info("批量插入语言完成");
        }

        // 重建语言缓存
        reCache();

        return i18n;
    }

    @Override
    public void reCache() {
        log.info("重建语言缓存");

        // 查询所有字段
        List<I18n> list = this.findList(I18nQuery.builder().deleted(false).build());

        Map<String, String> zhMap = Maps.newHashMap();
        Map<String, String> enMap = Maps.newHashMap();
        list.forEach(x -> {
            zhMap.put(x.getFieldKey(), x.getFieldName());
            enMap.put(x.getFieldKey(), x.getFieldNameEn());
        });

        // 中文key
        String zhRedisKey = I18nUtil.getRedisKey(Consts.ZH_KEY);
        // 英文key
        String enRedisKey = I18nUtil.getRedisKey(Consts.EN_KEY);

        RedisUtil.hmset(zhRedisKey, zhMap);
        RedisUtil.hmset(enRedisKey, enMap);
        log.info("缓存中英文成功");

        // 其他语言
        List<I18nLang> otherLangList = i18nLangMapper.findList(
            I18nLangQuery.builder().deleted(false).build());
        if (CollectionUtils.isEmpty(otherLangList)) {
            return;
        }

        Map<String, List<I18nLang>> otherMap = otherLangList.stream()
            .collect(Collectors.groupingBy(I18nLang::getLangDict));
        otherMap.forEach((k, v) -> {
            String redisKey = I18nUtil.getRedisKey(k);
            Map<String, String> langMap = Maps.newHashMap();
            v.forEach(x -> {
                langMap.put(x.getFieldKey(), x.getLangValue());
            });
            RedisUtil.hmset(redisKey, langMap);
        });

        log.info("缓存其他语言成功");
    }

    @Override
    public Map<String, String> getCache(String lang) {
        log.info("查询语言缓存. lang={}", lang);

        String redisKey = I18nUtil.getRedisKey(lang);
        Map<String, String> map = RedisUtil.hgetall(redisKey);

        return map;
//        if (MapUtils.isEmpty(map)) {
//            return Lists.newArrayList();
//        }
//
//        List<I18nCache> list = map.entrySet()
//            .stream()
//            .map(x -> I18nCache.builder().fieldKey(x.getKey()).langValue(x.getValue()).build())
//            .collect(Collectors.toList());
//
//        return list;
    }

}