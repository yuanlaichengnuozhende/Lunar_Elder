package com.lunar.system.mapper;

import com.lunar.common.mybatis.mapper.BaseMapper;
import com.lunar.system.entity.DictType;
import com.lunar.system.query.DictTypeQuery;
import com.lunar.system.response.DictTypeResp;
import com.lunar.system.response.DictTypeResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author liff
 * @date 2021-12-17
 */
public interface DictTypeMapper extends BaseMapper<DictType>
{

    /**
     * 查询用户列表-带组织及角色信息
     *
     * @param query
     * @return
     */
    List<DictTypeResp> findUserPage(DictTypeQuery query);

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public DictType selectDictTypeById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param cDictType 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<DictType> selectDictTypeList(DictType cDictType);

    /**
     * 新增【请填写功能名称】
     * 
     * @param cDictType 【请填写功能名称】
     * @return 结果
     */
    public int insertDictType(DictType cDictType);

    /**
     * 修改【请填写功能名称】
     * 
     * @param cDictType 【请填写功能名称】
     * @return 结果
     */
    public int updateDictType(DictType cDictType);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDictTypeById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDictTypeByIds(Long[] ids);

    List<DictType> selectDictTypeList(@Param("dictType") String dictType, @Param("dictName") String dictName, @Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);

    DictType selectByDictType(String dictType);

    List<DictTypeResp> findDictTypePage(DictTypeQuery query);
}
