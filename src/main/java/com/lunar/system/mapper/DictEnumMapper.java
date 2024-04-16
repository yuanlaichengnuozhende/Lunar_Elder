package com.lunar.system.mapper;

import com.lunar.common.mybatis.mapper.BaseMapper;
import com.lunar.system.entity.DictEnum;
import com.lunar.system.query.DictEnumQuery;
import com.lunar.system.response.DictEnumResp;
import com.lunar.system.response.DictEnumResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典数据Mapper接口
 * 
 * @author liff
 * @date 2021-12-17
 */
public interface DictEnumMapper extends BaseMapper<DictEnum>
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
    public int insertDictEnum(DictEnum cDictEnum);

    /**
     * 修改字典数据
     * 
     * @param cDictEnum 字典数据
     * @return 结果
     */
    public int updateDictEnum(DictEnum cDictEnum);

    /**
     * 删除字典数据
     * 
     * @param id 字典数据主键
     * @return 结果
     */
    public int deleteDictEnumById(Long id);

    /**
     * 批量删除字典数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDictEnumByIds(Long[] ids);

    List<DictEnum> selectDictEnumListByDictType(String dictType);

    int queryCount(DictEnum cDictEnum);

    DictEnum selectByDictValue(@Param("dictValue") String dictValue, @Param("dictType") String dictType);

    DictEnum selectByDictTypeAndValue(@Param("dictType") String dictType, @Param("dictValue") String dictValue);

    List<DictEnum> selectByDictTypeAndLabelValue(@Param("labelValue") String labelValue, @Param("dictType") String dictType);

    List<DictEnum> selectDictEnumListBySource(DictEnum cDictEnum);

    int queryCountBySource(DictEnum cDictEnum);

    String queryDictEnums(@Param("dictValue") String dictValue, @Param("dictType") String dictType);

    DictEnum queryDictEnumInfo(@Param("dictType") String dictType, @Param("dictValue") String dictValue, @Param("dictLabel") String dictLabel);

    List<DictEnumResp> findUserPage(DictEnumQuery query);

    List<DictEnumResp> findDictEnumPage(DictEnumQuery query);
}
