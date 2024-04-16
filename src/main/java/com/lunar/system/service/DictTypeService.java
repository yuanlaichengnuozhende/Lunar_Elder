package com.lunar.system.service;

import com.lunar.common.core.domain.IPage;
import com.lunar.system.entity.DictType;
import com.lunar.system.query.DictTypeQuery;
import com.lunar.system.request.DictTypeReq;
import com.lunar.system.response.DictTypeResp;
import com.lunar.common.core.domain.IPage;
import com.lunar.system.response.DictTypeResp;

import java.util.List;

/**
 * 字典Service接口
 * 
 * @author liff
 * @date 2021-12-17
 */
public interface DictTypeService
{
    /**
     * 查询字典
     * 
     * @param id 字典主键
     * @return 字典
     */
    public DictType selectDictTypeById(Long id);

    /**
     * 查询字典列表
     * 
     * @param cDictType 字典
     * @return 字典集合
     */
    public List<DictType> selectDictTypeList(DictType cDictType);

    /**
     * 新增字典
     * 
     * @param cDictType 字典
     * @return 结果
     */
    public int insertDictType(DictType cDictType) throws Exception;

    /**
     * 修改字典
     * 
     * @param cDictType 字典
     * @return 结果
     */
    public int updateDictType(DictType cDictType);

    /**
     * 批量删除字典
     * 
     * @param ids 需要删除的字典主键集合
     * @return 结果
     */
    public int deleteDictTypeByIds(Long[] ids);

    /**
     * 删除字典信息
     * 
     * @param id 字典主键
     * @return 结果
     */
    public int deleteDictTypeById(Long id);

    IPage<DictTypeResp> findDictTypePage(DictTypeQuery query, Integer pageNo, Integer pageSize, String create_time_desc);

    DictType add(DictTypeReq req);

    DictType edit(DictTypeReq req);
}
