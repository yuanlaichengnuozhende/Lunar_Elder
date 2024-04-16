package com.lunar.system.service;

import com.lunar.common.core.domain.IPage;
import com.lunar.common.mybatis.service.BaseService;
import com.lunar.system.entity.DictData;
import com.lunar.system.query.DictDataQuery;
import com.lunar.system.request.DictDataReq;
import com.lunar.system.response.DictDataResp;
import com.lunar.common.core.domain.IPage;
import com.lunar.system.response.DictDataResp;

import java.util.List;
import java.util.Map;

/**
 * 字典数据Service接口
 * 
 * @author liff
 * @date 2021-12-17
 */
public interface DictDataService extends BaseService<DictData>
{
    /**
     * 查询字典数据
     * 
     * @param id 字典数据主键
     * @return 字典数据
     */
    public DictData selectDictDataById(Long id);

    /**
     * 查询字典数据列表
     * 
     * @param cDictData 字典数据
     * @return 字典数据集合
     */
    public List<DictData> selectDictDataList(DictData cDictData);

    /**
     * 新增字典数据
     * 
     * @param cDictData 字典数据
     * @return 结果
     */
    public int insertDictData(DictData cDictData) throws Exception;

    /**
     * 修改字典数据
     * 
     * @param cDictData 字典数据
     * @return 结果
     */
    public int updateDictData(DictData cDictData);

    /**
     * 批量删除字典数据
     * 
     * @param ids 需要删除的字典数据主键集合
     * @return 结果
     */
    public int deleteDictDataByIds(Long[] ids);

    /**
     * 删除字典数据信息
     * 
     * @param id 字典数据主键
     * @return 结果
     */
    public int deleteDictDataById(Long id);

    List<DictData> selectDictDataListByDictType(String dictType);

    int queryCount(String dictType);

    List<Map<String, Object>> selectDictDataListAllByDictType(String dictType);

    DictData selectDictDataByDictValue(String dictType, String dictValue);

    Map<String, Object> selectDictDatalistALLByDictTypeBatch(String dictTypes);

    Map<String, Object> selectDictDatalistALLByDictTypeAndValue(String dictType, String dictValue);

    IPage<DictDataResp> findDictDataPage(DictDataQuery query, Integer pageNum, Integer pageSize, String create_time_desc);

    DictData add(DictDataReq req);

    DictData edit(DictDataReq req);
}
