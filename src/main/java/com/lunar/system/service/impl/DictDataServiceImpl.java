package com.lunar.system.service.impl;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.PageUtil;
import com.lunar.common.core.utils.ReturnFormat;
import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.DictData;
import com.lunar.system.mapper.DictDataMapper;
import com.lunar.system.query.DictDataQuery;
import com.lunar.system.request.DictDataReq;
import com.lunar.system.response.DictDataResp;
import com.lunar.system.service.DictDataService;
import com.github.pagehelper.Page;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.PageUtil;
import com.lunar.common.core.utils.ReturnFormat;
import com.lunar.system.response.DictDataResp;
import com.lunar.system.service.DictDataService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典数据Service业务层处理
 * 
 * @author liff
 * @date 2021-12-17
 */
@Service
public class DictDataServiceImpl extends BaseServiceImpl<DictDataMapper, DictData>
        implements DictDataService
{
    private static Logger log = LoggerFactory.getLogger(DictDataServiceImpl.class);

    @Autowired
    private DictDataMapper dictDataMapper;

    /**
     * 查询字典分类列表
     * @param query
     * @param pageNo
     * @param pageSize
     * @param order
     * @return
     */
    @Override
    public IPage<DictDataResp> findDictDataPage(DictDataQuery query, Integer pageNo, Integer pageSize, String order) {

        log.info("查询字典分类列表. query={}, pageNo={}, pageSize={}, order={}", query, pageNo, pageSize,
                order);

        PageUtil.startPage(pageNo, pageSize, order);
        List<DictDataResp> list = mapper.findDictDataPage(query);

        Page<DictDataResp> result = (Page<DictDataResp>) list;

        IPage<DictDataResp> page = new IPage<>(result);

        return page;
    }

    /**
     * 新增字典分类
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictData add(DictDataReq req) {
        log.info("新增字典分类. req={}", req);

        // 校验name
        int count = this.count(DictDataQuery.builder().dictLabel(req.getDictLabel()).dictType(req.getDictType()).build());
        if (count > 0) {
            throw new ServiceException(SystemCode.DICT_DATA_NAME_EXIST);
        }

        // 校验标识
        int countCode = this.count(DictDataQuery.builder().dictValue(req.getDictValue()).dictType(req.getDictType()).build());
        if (countCode > 0) {
            throw new ServiceException(SystemCode.DICT_DATA_EXIST);
        }

        Long userId = SecurityUtils.getUserId();

        DictData dictData = CopyUtil.copyObject(req, DictData.class);
        dictData.setId(null);
        dictData.setCreateBy(userId);
        dictData.setUpdateBy(userId);
        dictData.setCreateTime(new Date());
        dictDataMapper.insertDictData(dictData);
        log.info("新增字典分类完成");

        return dictData;
    }

    /**
     * 编辑字典分类
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictData edit(DictDataReq req) {
        log.info("编辑字典分类. req={}", req);

        DictData dictData = this.selectOne(DictDataQuery.builder().id(req.getId()).build());
        if (dictData == null) {
            throw new ServiceException(ApiCode.DATA_ERROR);
        }

        // 新名校验
        if (!StringUtils.equals(dictData.getDictLabel(), req.getDictLabel())) {
            int count = this.count(DictDataQuery.builder().dictLabel(req.getDictLabel()).dictType(req.getDictType()).build());
            if (count > 0) {
                throw new ServiceException(SystemCode.DICT_DATA_NAME_EXIST);
            }
        }

        // 新标识验
        if (!StringUtils.equals(dictData.getDictValue(), req.getDictValue())) {
            int count = this.count(DictDataQuery.builder().dictValue(req.getDictValue()).dictType(req.getDictType()).build());
            if (count > 0) {
                throw new ServiceException(SystemCode.DICT_DATA_EXIST);
            }
        }

        Long userId = SecurityUtils.getUserId();

        dictData.setDictLabel(req.getDictLabel());
        dictData.setDictValue(req.getDictValue());
        dictData.setDictSort(req.getDictSort());
        dictData.setUpdateBy(userId);
        dictDataMapper.updateDictData(dictData);

        log.info("编辑字典分类完成");

        return dictData;
    }

    /**
     * 查询字典数据
     * 
     * @param id 字典数据主键
     * @return 字典数据
     */
    @Override
    public DictData selectDictDataById(Long id)
    {
        return dictDataMapper.selectDictDataById(id);
    }

    /**
     * 查询字典数据列表
     * 
     * @param cDictData 字典数据
     * @return 字典数据
     */
    @Override
    public List<DictData> selectDictDataList(DictData cDictData)
    {
        return dictDataMapper.selectDictDataList(cDictData);
    }

    /**
     * 新增字典数据
     * 
     * @param cDictData 字典数据
     * @return 结果
     */
    @Override
    public int insertDictData(DictData cDictData) throws Exception {

        DictData cc = dictDataMapper.selectByDictValue(cDictData.getDictValue(),cDictData.getDictType());

        if(cc!=null){
            throw new ServiceException(SystemCode.DICT_DATA_EXIST);
        }

        cDictData.setCreateTime(new Date());
        cDictData.setStatus("1");
        return dictDataMapper.insertDictData(cDictData);
    }

    /**
     * 修改字典数据
     * 
     * @param cDictData 字典数据
     * @return 结果
     */
    @Override
    public int updateDictData(DictData cDictData)
    {
        cDictData.setUpdateTime(new Date());
        return dictDataMapper.updateDictData(cDictData);
    }

    /**
     * 批量删除字典数据
     * 
     * @param ids 需要删除的字典数据主键
     * @return 结果
     */
    @Override
    public int deleteDictDataByIds(Long[] ids)
    {
        return dictDataMapper.deleteDictDataByIds(ids);
    }

    /**
     * 删除字典数据信息
     * 
     * @param id 字典数据主键
     * @return 结果
     */
    @Override
    public int deleteDictDataById(Long id)
    {
        return dictDataMapper.deleteDictDataById(id);
    }

    /**
     * 查询字典数据列表
     *
     * @param dictType
     * @return 字典数据
     */
    @Override
    public List<DictData> selectDictDataListByDictType(String dictType)
    {
        return dictDataMapper.selectDictDataListByDictType(dictType);
    }

    @Override
    public int queryCount(String dictType) {
        return dictDataMapper.queryCount(dictType);
    }

    /**
     * 查询字典数据列表
     *
     * @param dictType
     * @return 字典数据
     */
    @Override
    public List<Map<String, Object>> selectDictDataListAllByDictType(String dictType)
    {

        List<DictData> dictdata = dictDataMapper.selectDictDataListAllByDictType(dictType);

        List<Map<String,Object>> returnMaps = ReturnFormat.arrayModelFormat(dictdata,"id", "dictLabel","dictType", "dictValue");

        return returnMaps;
    }

    /**
     * 查询字典数据
     *
     * @param dictValue 字典数据主键
     * @return 字典数据
     */
    @Override
    public DictData selectDictDataByDictValue(String dictType,String dictValue)
    {
        return dictDataMapper.selectDictDataByDictValue(dictType,dictValue);
    }

    /**
     * 根据字典标识查询字典所有分类-批量查询
     *
     * @param dictTypes
     * @return 字典数据
     */
    @Override
    public Map<String, Object> selectDictDatalistALLByDictTypeBatch(String dictTypes)
    {
        log.info("根据字典标识查询字典所有分类-批量查询开始dictTypes="+dictTypes);

        HashMap resultMap = new HashMap<>();

        String[] split = dictTypes.split(",");

        for(String dictType : split){

            List<DictData> dictdata = dictDataMapper.selectDictDataListAllByDictType(dictType);

            List<Map<String,Object>> returnMaps = ReturnFormat.arrayModelFormat(dictdata,"id", "dictLabel","dictType", "dictValue");

            resultMap.put(dictType,returnMaps);
        }

        return resultMap;
    }

    /**
     * 根据字典标识和字典分类标识查询字典分类值
     *
     * @param dictType
     * @return 字典数据
     */
    @Override
    public Map<String, Object> selectDictDatalistALLByDictTypeAndValue(String dictType, String dictValue)
    {
        log.info("根据字典标识查询字典所有分类-批量查询开始dictType="+dictType+"&&&dictValue="+dictValue);

        Map resultMap = new HashMap<>();

        if(dictValue.contains(",")){

            String[] split = dictValue.split(",");

            StringBuilder sb = new StringBuilder();
            for(String value : split){

                DictData data = dictDataMapper.selectByDictValue(value,dictType);

                if(data!=null){
                    if (sb.length() > 0) {
                        sb.append(",");
                    }

                    sb.append(data.getDictLabel());
                }
            }

            resultMap.put("dictLabel",sb.toString());
            resultMap.put("dictType",dictType);
            resultMap.put("dictValue",dictValue);

        }else{
            DictData data = dictDataMapper.selectByDictValue(dictValue,dictType);

            resultMap = ReturnFormat.oneModelFormat(data,"dictLabel","dictType", "dictValue");
        }

        return resultMap;
    }
}
