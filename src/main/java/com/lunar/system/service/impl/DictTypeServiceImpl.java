package com.lunar.system.service.impl;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.PageUtil;
import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.DictType;
import com.lunar.system.mapper.DictTypeMapper;
import com.lunar.system.query.DictTypeQuery;
import com.lunar.system.request.DictTypeReq;
import com.lunar.system.response.DictTypeResp;
import com.lunar.system.service.DictTypeService;
import com.github.pagehelper.Page;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.PageUtil;
import com.lunar.system.response.DictTypeResp;
import com.lunar.system.service.DictTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 字典 Service业务层处理
 * 
 * @author liff
 * @date 2023-02-05
 */
@Slf4j
@Service
public class DictTypeServiceImpl extends BaseServiceImpl<DictTypeMapper, DictType>
        implements DictTypeService
{
    @Autowired
    private DictTypeMapper cDictTypeMapper;

    /**
     * 查询字典列表
     * @param query
     * @param pageNo
     * @param pageSize
     * @param order
     * @return
     */
    @Override
    public IPage<DictTypeResp> findDictTypePage(DictTypeQuery query, Integer pageNo, Integer pageSize, String order) {

        log.info("查询字典列表. query={}, pageNo={}, pageSize={}, order={}", query, pageNo, pageSize,
                order);

        PageUtil.startPage(pageNo, pageSize, order);
        List<DictTypeResp> list = mapper.findDictTypePage(query);

        Page<DictTypeResp> result = (Page<DictTypeResp>) list;

        IPage<DictTypeResp> page = new IPage<>(result);

        return page;
    }

    /**
     * 新增字典
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictType add(DictTypeReq req) {
        log.info("新增字典. req={}", req);

        // 校验name
        int count = this.count(DictTypeQuery.builder().dictName(req.getDictName()).build());
        if (count > 0) {
            throw new ServiceException(SystemCode.DICT_TYPE_NAME_EXIST);
        }

        // 校验标识
        int countCode = this.count(DictTypeQuery.builder().dictType(req.getDictType()).build());
        if (countCode > 0) {
            throw new ServiceException(SystemCode.DICT_TYPE_EXIST);
        }

//        Long userId = SecurityUtils.getUserId();
        Long userId = SecurityUtils.getLoginUser().getUserId();

        DictType dictType = CopyUtil.copyObject(req, DictType.class);
        dictType.setId(null);
        dictType.setCreateTime(new Date());
        dictType.setCreateBy(userId);
        dictType.setUpdateBy(userId);
        cDictTypeMapper.insertDictType(dictType);
        log.info("新增字典完成");

        return dictType;
    }

    /**
     * 修改字典
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictType edit(DictTypeReq req) {
        log.info("编辑字典. req={}", req);

        DictType dictType = this.selectOne(DictTypeQuery.builder().id(req.getId()).build());
        if (dictType == null) {
            throw new ServiceException(ApiCode.DATA_ERROR);
        }

        // 新名校验
        if (!StringUtils.equals(dictType.getDictName(), req.getDictName())) {
            int count = this.count(DictTypeQuery.builder().dictName(req.getDictName()).build());
            if (count > 0) {
                throw new ServiceException(SystemCode.DICT_TYPE_NAME_EXIST);
            }
        }

        // 新标识验
        if (!StringUtils.equals(dictType.getDictType(), req.getDictType())) {
            int count = this.count(DictTypeQuery.builder().dictType(req.getDictType()).build());
            if (count > 0) {
                throw new ServiceException(SystemCode.DICT_TYPE_EXIST);
            }
        }

        Long userId = SecurityUtils.getUserId();

        dictType.setDictName(req.getDictName());
        dictType.setDictType(req.getDictType());
        dictType.setUpdateBy(userId);
        cDictTypeMapper.updateDictType(dictType);

        log.info("编辑字典完成");

        return dictType;
    }

    /**
     * 查询字典
     * 
     * @param id 字典主键
     * @return 字典
     */
    @Override
    public DictType selectDictTypeById(Long id)
    {
        return cDictTypeMapper.selectDictTypeById(id);
    }

    /**
     * 查询字典列表
     * 
     * @param cDictType 字典
     * @return 字典
     */
    @Override
    public List<DictType> selectDictTypeList(DictType cDictType)
    {

        return cDictTypeMapper.selectDictTypeList(cDictType);
    }

    /**
     * 新增字典
     * 
     * @param cDictType 字典
     * @return 结果
     */
    @Override
    public int insertDictType(DictType cDictType) throws Exception {

        DictType cc = cDictTypeMapper.selectByDictType(cDictType.getDictType());

        if(cc!=null){
            throw new ServiceException(SystemCode.DICT_TYPE_EXIST);
        }

        cDictType.setCreateTime(new Date());
        cDictType.setStatus("1");
        return cDictTypeMapper.insertDictType(cDictType);
    }

    /**
     * 修改字典
     * 
     * @param cDictType 字典
     * @return 结果
     */
    @Override
    public int updateDictType(DictType cDictType)
    {
        cDictType.setUpdateTime(new Date());
        return cDictTypeMapper.updateDictType(cDictType);
    }

    /**
     * 批量删除字典
     * 
     * @param ids 需要删除的字典主键
     * @return 结果
     */
    @Override
    public int deleteDictTypeByIds(Long[] ids)
    {
        return cDictTypeMapper.deleteDictTypeByIds(ids);
    }

    /**
     * 删除字典信息
     * 
     * @param id 字典主键
     * @return 结果
     */
    @Override
    public int deleteDictTypeById(Long id)
    {
        return cDictTypeMapper.deleteDictTypeById(id);
    }

}
