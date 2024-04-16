package com.lunar.common.localcache.utils;

import cn.hutool.core.util.ObjectUtil;
import com.lunar.system.entity.DictData;
import com.lunar.system.entity.DictEnum;
import com.lunar.system.query.DictDataQuery;
import com.lunar.system.service.DictDataService;
import com.lunar.system.service.DictEnumService;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.lunar.system.entity.DictData;
import com.lunar.system.entity.DictEnum;
import com.lunar.system.query.DictDataQuery;
import com.lunar.system.service.DictDataService;
import com.lunar.system.service.DictEnumService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author szx
 * @date 2023/02/23 11:02
 */
@Slf4j
public class LocalCacheUtil {

    /** 字典过期时间 6小时 */
    private static final long DICT_EXPIRED_HOUR = 6L;

    private static DictEnumService dictEnumService;
    private static DictDataService dictDataService;

    /**
     * 初始化
     *
     * @param dictEnumService
     */
    public static void init(DictEnumService dictEnumService, DictDataService dictDataService) {
        LocalCacheUtil.dictEnumService = dictEnumService;
        LocalCacheUtil.dictDataService = dictDataService;
        log.info("初始化LocalCacheUtil成功");
    }

    /**
     * 清空缓存
     */
    public static void flush() {
        DICT_ENUM_LIST_CACHE.invalidateAll();
        DICT_DATA_LIST_CACHE.invalidateAll();
    }

    /**
     * 按字典类型缓存enum
     */
    private static final LoadingCache<String, List<DictEnum>> DICT_ENUM_LIST_CACHE =
        CacheUtil.buildAsyncReloadingCache(
            Duration.ofHours(DICT_EXPIRED_HOUR),
            new CacheLoader<String, List<DictEnum>>() {
                @Override
                public List<DictEnum> load(String key) throws Exception {
                    return ObjectUtil.defaultIfNull(dictEnumService.listByDictType(key), Lists.newArrayList());
                }
            }
        );

    /**
     * 按字典类型缓存data
     */
    private static final LoadingCache<String, List<DictData>> DICT_DATA_LIST_CACHE =
        CacheUtil.buildAsyncReloadingCache(
            Duration.ofHours(DICT_EXPIRED_HOUR),
            new CacheLoader<String, List<DictData>>() {
                @Override
                public List<DictData> load(String key) throws Exception {
                    List<DictData> list = dictDataService.findList(
                        DictDataQuery.builder().dictType(key).status('1').build(),
                        "dict_sort asc, id asc");
                    return ObjectUtil.defaultIfNull(list, Lists.newArrayList());
                }
            }
        );

    /**
     * 枚举值列表
     *
     * @param dictType
     * @return
     */
    @SneakyThrows
    public static List<DictEnum> getDictEnumList(String dictType) {
        return DICT_ENUM_LIST_CACHE.get(dictType);
    }

    /**
     * 根据dictLabel（字典文本）查询enum
     *
     * @param dictType
     * @return
     */
    @SneakyThrows
    public static DictEnum getEnumByDictLabel(String dictType, String dictLabel) {
        if (StringUtils.isAnyBlank(dictType, dictLabel)) {
            return null;
        }
        List<DictEnum> list = DICT_ENUM_LIST_CACHE.get(dictType);
        return list.stream().filter(x -> dictLabel.equals(x.getDictLabel())).findFirst().orElse(null);
    }

    /**
     * 根据dictValue（字典值）查询enum
     *
     * @param dictType
     * @return
     */
    @SneakyThrows
    public static DictEnum getEnumByDictValue(String dictType, String dictValue) {
        if (StringUtils.isAnyBlank(dictType, dictValue)) {
            return null;
        }
        List<DictEnum> list = DICT_ENUM_LIST_CACHE.get(dictType);
        return list.stream().filter(x -> dictValue.equals(x.getDictValue())).findFirst().orElse(null);
    }

    /**
     * 根据dictValue（字典值）查询label
     *
     * @param dictType
     * @return
     */
    @SneakyThrows
    public static String getEnumLabelByDictValue(String dictType, String dictValue) {
        DictEnum dictEnum = getEnumByDictValue(dictType, dictValue);
        return Optional.ofNullable(dictEnum).map(DictEnum::getDictLabel).orElse("");
    }

    /**
     * data列表
     *
     * @param dictType
     * @return
     */
    @SneakyThrows
    public static List<DictData> getDictDataList(String dictType) {
        return DICT_DATA_LIST_CACHE.get(dictType);
    }

    /**
     * 根据dictLabel（字典文本）查询enum
     *
     * @param dictType
     * @return
     */
    @SneakyThrows
    public static DictData getDataByDictLabel(String dictType, String dictLabel) {
        if (StringUtils.isAnyBlank(dictType, dictLabel)) {
            return null;
        }
        List<DictData> list = DICT_DATA_LIST_CACHE.get(dictType);
        return list.stream().filter(x -> dictLabel.equals(x.getDictLabel())).findFirst().orElse(null);
    }

    /**
     * 根据dictValue（字典值）查询enum
     *
     * @param dictType
     * @return
     */
    @SneakyThrows
    public static DictData getDataByDictValue(String dictType, String dictValue) {
        if (StringUtils.isAnyBlank(dictType, dictValue)) {
            return null;
        }
        List<DictData> list = DICT_DATA_LIST_CACHE.get(dictType);
        return list.stream().filter(x -> dictValue.equals(x.getDictValue())).findFirst().orElse(null);
    }

    /**
     * 根据dictValue（字典值）查询label
     *
     * @param dictType
     * @return
     */
    @SneakyThrows
    public static String getDataLabelByDictValue(String dictType, String dictValue) {
        DictData dictData = getDataByDictValue(dictType, dictValue);
        return Optional.ofNullable(dictData).map(DictData::getDictLabel).orElse("");
    }

    /**
     * key缓存
     */
    private static final LoadingCache<String, Object> KEY_CACHE =
        CacheUtil.buildAsyncReloadingCache(
            Duration.ofHours(4L),
            new CacheLoader<String, Object>() {
                @Override
                public Object load(String key) throws Exception {
                    return "";
                }
            }
        );

    public static void setKey(String key, Object obj) {
        KEY_CACHE.put(key, obj);
    }

    public static Object getKey(String key) {
        try {
            return KEY_CACHE.get(key);
        } catch (Exception e) {
            log.error("读取key[{}]失败", key, e);
            return null;
        }
    }

    public static void delKey(String key) {
        KEY_CACHE.invalidate(key);
    }

    public static void main(String[] args) throws InterruptedException {
        setKey("a", "11111111111");
        TimeUnit.SECONDS.sleep(3L);
        setKey("b", "22222222222");

        while (true) {
            TimeUnit.SECONDS.sleep(1L);

            System.out.println("key=a, value=" + getKey("a"));
            System.out.println("key=b, value=" + getKey("b"));
        }
    }

//    private static final LoadingCache<Pair<String, String>, DictEnum> GET_DICT_DATA_CACHE =
//        CacheUtil.buildAsyncReloadingCache(
//            // 过期时间 1 分钟
//            Duration.ofMinutes(1L),
//            new CacheLoader<Pair<String, String>, DictEnum>() {
//                @Override
//                public DictEnum load(Pair<String, String> pair) {
//                    return ObjectUtil.defaultIfNull(
//                        dictFeignService.getByDictLabel(pair.getKey(), pair.getValue()),
//                        new DictEnum());
//                }
//            });
//
//    @SneakyThrows
//    public static DictEnum getDictDataLabel(String dictType, String value) {
//        return GET_DICT_DATA_CACHE.get(new Pair<>(dictType, value));
//    }

}
