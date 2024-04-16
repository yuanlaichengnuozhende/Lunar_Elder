package com.lunar.system.mapper;

import com.lunar.common.mybatis.mapper.BaseMapper;
import com.lunar.system.entity.DictData;
import com.lunar.system.query.DictDataQuery;
import com.lunar.system.response.DictDataResp;
import com.lunar.system.response.DictDataResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典数据Mapper接口
 * 
 * @author liff
 * @date 2021-12-17
 */
public interface DictDataMapper extends BaseMapper<DictData>
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
    public int insertDictData(DictData cDictData);

    /**
     * 修改字典数据
     * 
     * @param cDictData 字典数据
     * @return 结果
     */
    public int updateDictData(DictData cDictData);

    /**
     * 删除字典数据
     * 
     * @param id 字典数据主键
     * @return 结果
     */
    public int deleteDictDataById(Long id);

    /**
     * 批量删除字典数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDictDataByIds(Long[] ids);

    List<DictData> selectDictDataListByDictType(String dictType);

    int queryCount(String dictType);

    List<DictData> selectDictDataListAllByDictType(String dictType);

    DictData selectDictDataByDictValue(@Param("dictType") String dictType, @Param("dictValue") String dictValue);

    DictData selectByDictValue(@Param("dictValue") String dictValue, @Param("dictType") String dictType);

    DictData selectByDictTypeAndValue(@Param("dictType") String dictType, @Param("value") String value);

    List<DictDataResp> findDictDataPage(DictDataQuery query);
}
