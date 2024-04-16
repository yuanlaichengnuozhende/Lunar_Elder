package com.lunar.system.service.impl;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.PageUtil;
import com.lunar.common.core.utils.ReturnFormat;
import com.lunar.common.core.utils.StringUtils;
import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.DictData;
import com.lunar.system.entity.DictEnum;
import com.lunar.system.mapper.DictDataMapper;
import com.lunar.system.mapper.DictEnumMapper;
import com.lunar.system.query.DictDataQuery;
import com.lunar.system.query.DictEnumQuery;
import com.lunar.system.request.DictEnumReq;
import com.lunar.system.response.DictEnumResp;
import com.lunar.system.service.DictDataService;
import com.lunar.system.service.DictEnumService;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.PageUtil;
import com.lunar.common.core.utils.ReturnFormat;
import com.lunar.common.core.utils.StringUtils;
import com.lunar.system.response.DictEnumResp;
import com.lunar.system.service.DictDataService;
import com.lunar.system.service.DictEnumService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 字典枚举值数据Service业务层处理
 *
 * @author liff
 * @date 2023-02-05
 */
@Service
public class DictEnumServiceImpl extends BaseServiceImpl<DictEnumMapper, DictEnum>
    implements DictEnumService {

    private static final Logger log = LoggerFactory.getLogger(DictEnumServiceImpl.class);
    @Autowired
    private DictEnumMapper cDictEnumMapper;

    @Autowired
    private DictDataService iCDictDataService;

    @Autowired
    private DictDataMapper cDictDataMapper;

    /**
     * 查询字典枚举值列表
     *
     * @param query
     * @param pageNo
     * @param pageSize
     * @param order
     * @return
     */
    @Override
    public IPage<DictEnumResp> findDictEnumPage(DictEnumQuery query, Integer pageNo, Integer pageSize, String order) {

        log.info("查询字典枚举值列表. query={}, pageNo={}, pageSize={}, order={}", query, pageNo, pageSize,
            order);

        PageUtil.startPage(pageNo, pageSize, order);
        List<DictEnumResp> list = mapper.findDictEnumPage(query);

        Page<DictEnumResp> result = (Page<DictEnumResp>) list;

        IPage<DictEnumResp> page = new IPage<>(result);

        return page;
    }

    /**
     * 新增字典枚举值
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictEnum add(DictEnumReq req) {
        log.info("新增字典枚举值. req={}", req);

        // 校验name
        int count = this.count(
            DictEnumQuery.builder().dictLabel(req.getDictLabel()).dictType(req.getDictType()).build());
        if (count > 0) {
            throw new ServiceException(SystemCode.DICT_ENUM_NAME_EXIST);
        }

        // 校验标识
        int countCode = this.count(
            DictEnumQuery.builder().dictValue(req.getDictValue()).dictType(req.getDictType()).build());
        if (countCode > 0) {
            throw new ServiceException(SystemCode.DICT_ENUM_EXIST);
        }

        Long userId = SecurityUtils.getUserId();

        DictEnum dictEnum = CopyUtil.copyObject(req, DictEnum.class);
        dictEnum.setId(null);
        dictEnum.setCreateBy(userId);
        dictEnum.setUpdateBy(userId);
        dictEnum.setCreateTime(new Date());
        cDictEnumMapper.insertDictEnum(dictEnum);
        log.info("新增字典枚举值完成");

        return dictEnum;
    }

    /**
     * 编辑字典枚举值
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictEnum edit(DictEnumReq req) {
        log.info("编辑字典枚举值. req={}", req);

        DictEnum dictEnum = this.selectOne(DictEnumQuery.builder().id(req.getId()).build());
        if (dictEnum == null) {
            throw new ServiceException(ApiCode.DATA_ERROR);
        }

        // 新名校验
        if (!org.apache.commons.lang3.StringUtils.equals(dictEnum.getDictLabel(), req.getDictLabel())) {
            int count = this.count(
                DictEnumQuery.builder().dictLabel(req.getDictLabel()).dictType(req.getDictType()).build());
            if (count > 0) {
                throw new ServiceException(SystemCode.DICT_ENUM_NAME_EXIST);
            }
        }

        // 新标识验
        if (!org.apache.commons.lang3.StringUtils.equals(dictEnum.getDictValue(), req.getDictValue())) {
            int count = this.count(
                DictEnumQuery.builder().dictValue(req.getDictValue()).dictType(req.getDictType()).build());
            if (count > 0) {
                throw new ServiceException(SystemCode.DICT_ENUM_EXIST);
            }
        }

        Long userId = SecurityUtils.getUserId();

        dictEnum.setDictLabel(req.getDictLabel());
        dictEnum.setDictValue(req.getDictValue());
        dictEnum.setDictSort(req.getDictSort());
        dictEnum.setSourceType(req.getSourceType());
        dictEnum.setRelatedValue(req.getRelatedValue());
        dictEnum.setUpdateBy(userId);
        cDictEnumMapper.updateDictEnum(dictEnum);

        log.info("编辑字典枚举值完成");

        return dictEnum;
    }

    /**
     * 查询字典数据
     *
     * @param id 字典数据主键
     * @return 字典数据
     */
    @Override
    public DictEnum selectDictEnumById(Long id) {
        return cDictEnumMapper.selectDictEnumById(id);
    }

    /**
     * 查询字典数据列表
     *
     * @param cDictEnum 字典数据
     * @return 字典数据
     */
    @Override
    public List<DictEnum> selectDictEnumList(DictEnum cDictEnum) {
        return cDictEnumMapper.selectDictEnumList(cDictEnum);
    }

    /**
     * 新增字典数据
     *
     * @param cDictEnum 字典数据
     * @return 结果
     */
    @Override
    public int insertDictEnum(DictEnum cDictEnum) throws Exception {

        DictEnum cc = cDictEnumMapper.selectByDictValue(cDictEnum.getDictValue(), cDictEnum.getDictType());

        if (cc != null) {
            throw new ServiceException(SystemCode.DICT_ENUM_EXIST);
        }

        cDictEnum.setCreateTime(new Date());
        cDictEnum.setStatus("1");
        return cDictEnumMapper.insertDictEnum(cDictEnum);
    }

    /**
     * 修改字典数据
     *
     * @param cDictEnum 字典数据
     * @return 结果
     */
    @Override
    public int updateDictEnum(DictEnum cDictEnum) {
        cDictEnum.setUpdateTime(new Date());
        return cDictEnumMapper.updateDictEnum(cDictEnum);
    }

    /**
     * 批量删除字典数据
     *
     * @param ids 需要删除的字典数据主键
     * @return 结果
     */
    @Override
    public int deleteDictEnumByIds(Long[] ids) {
        return cDictEnumMapper.deleteDictEnumByIds(ids);
    }

    /**
     * 删除字典数据信息
     *
     * @param id 字典数据主键
     * @return 结果
     */
    @Override
    public int deleteDictEnumById(Long id) {
        return cDictEnumMapper.deleteDictEnumById(id);
    }

    /**
     * 查询字典数据列表
     *
     * @param dictType
     * @return 字典数据
     */
    @Override
    public List<DictEnum> selectDictEnumListByDictType(String dictType) {
        return cDictEnumMapper.selectDictEnumListByDictType(dictType);
    }

    @Override
    public int queryCount(DictEnum cDictEnum) {
        return cDictEnumMapper.queryCount(cDictEnum);
    }

    /**
     * 查询字典枚举值数据列表
     *
     * @param cDictEnum
     * @return 字典数据
     */
    @Override
    public List<Map<String, Object>> selectDictEnumListAll(DictEnum cDictEnum) {

        List<DictEnum> list = selectDictEnumList(cDictEnum);

        List<Map<String, Object>> returnMaps = ReturnFormat.arrayModelFormat(list, "id", "dictLabel", "dictType",
            "dictValue", "dictSort", "sourceType", "relatedValue");

        for (Map<String, Object> map : returnMaps) {

            DictData dd = iCDictDataService.selectDictDataByDictValue(map.get("dictType") + "",
                map.get("sourceType") + "");

            map.put("sourceName", dd.getDictLabel());
        }

        return returnMaps;
    }

    /**
     * 根据条件查询字典枚举值数据
     *
     * @param cDictEnum
     * @return 字典数据
     */
    @Override
    public List<Map<String, Object>> selectDictEnumListBylable(DictEnum cDictEnum) {

        List<DictEnum> list = cDictEnumMapper.selectDictEnumListBySource(cDictEnum);

        List<Map<String, Object>> returnMaps = ReturnFormat.arrayModelFormat(list, "id", "dictLabel", "dictType",
            "dictValue", "dictSort", "sourceType");

        for (Map<String, Object> map : returnMaps) {

            DictData dd = iCDictDataService.selectDictDataByDictValue(map.get("dictType") + "",
                map.get("sourceType") + "");

            map.put("sourceName", dd.getDictLabel());
        }

        return returnMaps;
    }

    /**
     * 根据字典标识查询字典枚举值-批量查询
     *
     * @param dictTypes
     * @return 字典数据
     */
    @Override
    public Map<String, Object> selectDictEnumlistALLByDictTypeBatch(String dictTypes) {
        log.info("根据字典标识查询字典枚举值-批量查询开始dictTypes=" + dictTypes);

        HashMap resultMap = new HashMap<>();

        String[] split = dictTypes.split(",");

        for (String dictType : split) {

            List<DictEnum> CDictEnum = cDictEnumMapper.selectDictEnumListByDictType(dictType);

            List<Map<String, Object>> returnMaps = ReturnFormat.arrayModelFormat(CDictEnum, "id", "dictLabel",
                "dictType", "dictValue", "dictSort", "sourceType", "relatedValue");

            for (Map<String, Object> mm : returnMaps) {

                Map<String, Object> map = iCDictDataService.selectDictDatalistALLByDictTypeAndValue(dictType,
                    mm.get("sourceType") + "");

                mm.put("sourceName", map.get("dictLabel"));
            }

            resultMap.put(dictType, returnMaps);
        }

        return resultMap;
    }

    /**
     * 根据字典标识和字典枚举值标识查询字典枚举值
     *
     * @param dictType
     * @return 字典数据
     */
    @Override
    public Map<String, Object> selectDictEnumlistALLByDictTypeAndValue(String dictType, String dictValue) {
        log.info("根据字典标识和字典枚举值标识查询字典枚举值开始dictTypes=" + dictType + "&&&dictValue=" + dictValue);

        Map resultMap = new HashMap<>();

        if (StringUtils.isEmpty(dictValue)) {
            return resultMap;
        }

        if (dictValue.contains(",")) {

            String[] split = dictValue.split(",");

            StringBuilder sb = new StringBuilder();
            for (String value : split) {

                DictEnum CDictEnum = cDictEnumMapper.selectByDictValue(value, dictType);

                if (CDictEnum != null) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }

                    sb.append(CDictEnum.getDictLabel());
                }
            }

            resultMap.put("dictLabel", sb.toString());
            resultMap.put("dictType", dictType);
            resultMap.put("dictValue", dictValue);

        } else {
            DictEnum CDictEnum = cDictEnumMapper.selectByDictValue(dictValue, dictType);

            resultMap = ReturnFormat.oneModelFormat(CDictEnum, "dictLabel", "dictType", "dictValue");
        }

        return resultMap;
    }

    /**
     * 根据字典标识字典分类查询字典枚举值
     *
     * @param dictType
     * @return 字典数据
     */
    @Override
    public List<Map<String, Object>> selectlistALLByDictTypeData(String dictType, String labelValue) {
        log.info("根据字典标识和字典分类标识查询字典枚举值开始dictTypes=" + dictType + "&&&labelValue=" + labelValue);

        List<DictEnum> CDictEnum = cDictEnumMapper.selectByDictTypeAndLabelValue(labelValue, dictType);

        List<Map<String, Object>> returnMaps = ReturnFormat.arrayModelFormat(CDictEnum, "dictLabel", "dictType",
            "dictValue", "relatedValue");

        return returnMaps;
    }

    /**
     * 根据字典标识查询字典分类字典枚举值
     *
     * @param dictType
     * @return 字典数据
     */
    @Override
    public List<Map<String, Object>> selectDictEnumlistALLByDictTypeData(String dictType) {
        log.info("根据字典标识查询字典分类字典枚举值开始dictTypes=" + dictType);

        List<DictData> dictdata = cDictDataMapper.selectDictDataListAllByDictType(dictType);

        List<Map<String, Object>> returnMaps = ReturnFormat.arrayModelFormat(dictdata, "id", "dictLabel", "dictType",
            "dictValue");

        for (Map<String, Object> data : returnMaps) {

            List<Map<String, Object>> mapList = selectlistALLByDictTypeData(dictType, data.get("dictValue") + "");

            data.put("enumList", mapList);
        }

        return returnMaps;
    }

    @Override
    public int queryCountBySource(DictEnum cDictEnum) {
        return cDictEnumMapper.queryCountBySource(cDictEnum);
    }

    /**
     * 根据字典标识和字典枚举值标识组合因子单位
     *
     * @param dictType
     * @return 字典数据
     */
    @Override
    public Map<String, Object> getFactorUnits(String dictType, String dictValue) {
        log.info("根据字典标识和字典枚举值标识组合因子单位开始dictTypes=" + dictType + "&&&dictValue=" + dictValue);

        Map resultMap = new HashMap<>();

        if (StringUtils.isEmpty(dictValue)) {
            return resultMap;
        }

        if (dictValue.contains(",")) {

            String[] split = dictValue.split(",");

            StringBuilder sb = new StringBuilder();

            DictEnum CDictEnum = cDictEnumMapper.selectByDictValue(split[split.length - 1], dictType);

            resultMap = ReturnFormat.oneModelFormat(CDictEnum, "dictLabel", "dictType", "dictValue");
        } else {
            DictEnum CDictEnum = cDictEnumMapper.selectByDictValue(dictValue, dictType);

            resultMap = ReturnFormat.oneModelFormat(CDictEnum, "dictLabel", "dictType", "dictValue");
        }

        return resultMap;
    }

    /**
     * 根据字典标识和字典枚举值标识查询字典枚举值
     *
     * @param dictType
     * @return 字典数据
     */
    @Override
    public DictEnum selectDictEnumByDictTypeAndValue(String dictType, String dictValue) {
        log.info("根据字典标识和字典枚举值标识查询字典枚举值开始dictType=" + dictType + "&&&dictValue=" + dictValue);

        DictEnum dictEnum = cDictEnumMapper.selectByDictValue(dictValue, dictType);

        if (dictEnum == null) {
            dictEnum = new DictEnum();
        }

        return dictEnum;
    }

    /**
     * 根据字典标识查询字典分类字典枚举值-批量查询
     *
     * @param dictTypes
     * @return 字典数据
     */
    @Override
    public Map<String, Object> selectDictEnumlistALLByDictTypeDataBatch(String dictTypes) {
        log.info("根据字典标识查询字典分类字典枚举值-批量查询开始dictTypes=" + dictTypes);

        HashMap resultMap = new HashMap<>();

        String[] split = dictTypes.split(",");

        for (String dictType : split) {

            List<Map<String, Object>> returnMaps = selectDictEnumlistALLByDictTypeData(dictType);

            resultMap.put(dictType, returnMaps);
        }

        return resultMap;
    }

    /**
     * 根据字典标识字典枚举值+分类查询字典翻译
     *
     * @param dictType
     * @return 字典数据
     */
    @Override
    public Map<String, Object> queryDictEnumDetail(String dictType, String dictValues) {
        log.info("根据字典标识字典枚举值+分类查询字典翻译开始dictType=" + dictType + "&&&dictValue=" + dictValues);

        Map resultMap = new HashMap<>();

        if (dictValues.contains(",")) {

            String[] split = dictValues.split(",");

            StringBuilder buffer = new StringBuilder();

            //分类
            DictData datas = cDictDataMapper.selectByDictValue(split[0], dictType);

            if (datas != null) {
                buffer.append(datas.getDictLabel());
            }

            //枚举值
            DictEnum enums = cDictEnumMapper.selectByDictValue(split[1], dictType);

            if (enums != null) {
                if (buffer.length() > 0) {
                    buffer.append(",");
                }

                buffer.append(enums.getDictLabel());
            }

            resultMap.put("dictType", dictType);
            resultMap.put("dictValues", dictValues);
            resultMap.put("dictLabels", buffer.toString());
        }

        return resultMap;
    }

    /**
     * 根据字典标识字典枚举值查询字典枚举值详情
     *
     * @param dictType
     * @return 字典数据
     */
    @Override
    public Map<String, Object> queryDictEnumInfo(String dictType, String dictValue, String dictLabel) {
        log.info("根据字典标识字典枚举值查询字典枚举值详情开始dictType=" + dictType + "&&&dictValue=" + dictValue
            + "&&&dictLabel=" + dictLabel);

        Map resultMap = new HashMap<>();

        //枚举值
        DictEnum enums = cDictEnumMapper.queryDictEnumInfo(dictType, dictValue, dictLabel);

        resultMap = ReturnFormat.oneModelFormat(enums, "dictLabel", "dictType", "dictValue", "sourceType",
            "relatedValue");

        return resultMap;
    }

    @Override
    public List<DictEnum> listByDictType(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return Lists.newArrayList();
        }
        List<DictEnum> list = this.findList(DictEnumQuery.builder().dictType(dictType).status('1').build(),
            "dict_sort asc");
        return list;
    }

    @Override
    public DictEnum getByDictLabel(String dictType, String dictLabel) {
        if (StringUtils.isAnyBlank(dictType, dictLabel)) {
            return null;
        }
        return this.selectFirst(
            DictEnumQuery.builder().dictType(dictType).dictLabel(dictLabel).status('1').build());
    }

    @Override
    public DictEnum getByDictValue(String dictType, String dictValue) {
        if (StringUtils.isAnyBlank(dictType, dictValue)) {
            return null;
        }
        return this.selectFirst(
            DictEnumQuery.builder().dictType(dictType).dictValue(dictValue).status('1').build());
    }

    @Override
    public Map<String, Object> listBatchByDictType(String dictTypes) {
        log.info("根据字典标识查询字典枚举值-批量查询. dictTypes={}", dictTypes);

        List<String> dictTypeList = Arrays.asList(dictTypes.split(","));

        List<DictEnum> list = this.findList(DictEnumQuery.builder().dictTypeList(dictTypeList).status('1').build());
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }

        List<DictData> dataList = iCDictDataService.findList(
            DictDataQuery.builder().dictTypeList(dictTypeList).status('1').build());

        Map<String, Object> resultMap = Maps.newHashMapWithExpectedSize(list.size() * 2);

        Map<String, List<DictEnum>> groupMap = list.stream().collect(Collectors.groupingBy(DictEnum::getDictType));

        for (Entry<String, List<DictEnum>> entry : groupMap.entrySet()) {
            String dictType = entry.getKey();
            List<DictEnum> dictEnumList = entry.getValue()
                .stream()
                .sorted(Comparator.comparing(DictEnum::getDictSort))
                .collect(Collectors.toList());
            List<Map<String, Object>> returnMaps = ReturnFormat.arrayModelFormat(dictEnumList,
                "id", "dictLabel", "dictType", "dictValue", "dictSort", "sourceType", "relatedValue");

            for (Map<String, Object> map : returnMaps) {
                DictData dictData = dataList.stream()
                    .filter(
                        x -> dictType.equals(x.getDictType())
                            && (map.get("sourceType") + "").equals(x.getDictValue()))
                    .findFirst()
                    .orElse(new DictData());
                map.put("sourceName", dictData.getDictLabel());
            }

            resultMap.put(dictType, returnMaps);
        }

        return resultMap;
    }

}
