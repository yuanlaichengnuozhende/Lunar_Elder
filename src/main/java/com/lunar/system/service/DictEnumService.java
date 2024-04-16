package com.lunar.system.service;

import com.lunar.common.core.domain.IPage;
import com.lunar.system.entity.DictEnum;
import com.lunar.system.query.DictEnumQuery;
import com.lunar.system.request.DictEnumReq;
import com.lunar.system.response.DictEnumResp;
import com.lunar.common.core.domain.IPage;
import com.lunar.system.response.DictEnumResp;

import java.util.List;
import java.util.Map;

/**
 * 字典数据Service接口
 * 
 * @author liff
 * @date 2021-12-17
 */
public interface DictEnumService
{
    /**
     * 查询字典数据
     * 
     * @param id 字典数据主键
     * @return 字典数据
     */
    public DictEnum selectDictEnumById(Long id);

    /**
     * 查询字典数据列表
     * 
     * @param cDictEnum 字典数据
     * @return 字典数据集合
     */
    public List<DictEnum> selectDictEnumList(DictEnum cDictEnum);

    /**
     * 新增字典数据
     * 
     * @param cDictEnum 字典数据
     * @return 结果
     */
    public int insertDictEnum(DictEnum cDictEnum) throws Exception;

    /**
     * 修改字典数据
     * 
     * @param cDictEnum 字典数据
     * @return 结果
     */
    public int updateDictEnum(DictEnum cDictEnum);

    /**
     * 批量删除字典数据
     * 
     * @param ids 需要删除的字典数据主键集合
     * @return 结果
     */
    public int deleteDictEnumByIds(Long[] ids);

    /**
     * 删除字典数据信息
     * 
     * @param id 字典数据主键
     * @return 结果
     */
    public int deleteDictEnumById(Long id);

    List<DictEnum> selectDictEnumListByDictType(String dictType);

    int queryCount(DictEnum cDictEnum);

    List<Map<String, Object>> selectDictEnumListAll(DictEnum cDictEnum);

    List<Map<String, Object>> selectDictEnumListBylable(DictEnum cDictEnum);

    Map<String, Object> selectDictEnumlistALLByDictTypeBatch(String dictTypes);

    Map<String, Object> selectDictEnumlistALLByDictTypeAndValue(String dictType, String dictValue);

    List<Map<String, Object>> selectlistALLByDictTypeData(String dictType, String labelValue);

    List<Map<String, Object>> selectDictEnumlistALLByDictTypeData(String dictType);

    int queryCountBySource(DictEnum cDictEnum);

    Map<String, Object> getFactorUnits(String dictType, String dictValue);

    DictEnum selectDictEnumByDictTypeAndValue(String dictType, String dictValue);

    Map<String, Object> selectDictEnumlistALLByDictTypeDataBatch(String dictTypes);

    Map<String, Object> queryDictEnumDetail(String dictType, String dictValues);

    Map<String, Object> queryDictEnumInfo(String dictType, String dictValue, String dictLabel);

    IPage<DictEnumResp> findDictEnumPage(DictEnumQuery query, Integer pageNum, Integer pageSize, String create_time_desc);

    DictEnum add(DictEnumReq req);

    DictEnum edit(DictEnumReq req);

    /**
     * 字典枚举值列表
     *
     * @param dictType
     * @return
     */
    List<DictEnum> listByDictType(String dictType);

    /**
     * 根据dictLabel查询
     *
     * @param dictType
     * @param dictLabel
     * @return
     */
    DictEnum getByDictLabel(String dictType, String dictLabel);

    /**
     * 根据dictValue查询
     *
     * @param dictType
     * @param dictValue
     * @return
     */
    DictEnum getByDictValue(String dictType, String dictValue);

    /**
     * 根据字典标识查询字典枚举值-批量查询
     *
     * @param dictTypes
     * @return
     */
    Map<String, Object> listBatchByDictType(String dictTypes);

}
