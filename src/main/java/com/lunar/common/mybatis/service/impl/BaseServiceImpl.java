package com.lunar.common.mybatis.service.impl;

import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.PageUtil;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.mybatis.mapper.BaseMapper;
import com.lunar.common.mybatis.query.Query;
import com.lunar.common.mybatis.service.BaseService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.PageUtil;
import com.lunar.common.mybatis.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class BaseServiceImpl<M extends BaseMapper<E>, E extends BaseEntity>
    implements BaseService<E> {

    @Autowired
    protected M mapper;

    @Override
    public E selectByPrimaryKey(Long id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public E insert(E entity) {
        mapper.insert(entity);
        return entity;
    }

    @Override
    public E insertSelective(E entity) {
        mapper.insertSelective(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSelective(E entity) {
        return mapper.updateSelective(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(E entity) {
        return mapper.update(entity);
    }

    @Override
    public E selectOne(Query query) {
        List<E> list = findList(query);
        if (list.isEmpty()) {
            return null;
        } else if (list.size() > 1) {
            String className = Arrays.stream(this.getClass().getInterfaces())
                .map(Class::getSimpleName)
                .filter(e -> e.toLowerCase().contains("service"))
                .findFirst()
                .orElse("未知");
            String msg = MessageFormat.format("{0}.selectOne 查询到大于1条的记录. 参数:{1}", className, query);
            throw new RuntimeException(msg);
        }

        return list.get(0);
    }

    @Override
    public E selectFirst(Query query) {
        return selectFirst(query, null);
    }

    @Override
    public E selectFirst(Query query, String order) {
        List<E> list = findList(query, order);
        if (list.isEmpty()) {
            return null;
        } else if (list.size() > 1) {
            String className = Arrays.stream(this.getClass().getInterfaces())
                .map(Class::getSimpleName)
                .filter(e -> e.toLowerCase().contains("service"))
                .findFirst()
                .orElse("未知");
            String msg = MessageFormat.format("{0}.selectFirst 查询到大于1条的记录. 参数:{1}", className, query);
            log.info(msg);
        }
        return list.get(0);
    }

    @Override
    public int count(Query query) {
        return mapper.count(query);
    }

    @Override
    public List<E> findList(Query query) {
        return mapper.findList(query);
    }

    @Override
    public List<E> findList(Query query, String order) {
        PageHelper.orderBy(order);
        return this.findList(query);
    }

    @Override
    public IPage<E> findPage(Query query, int pageNum, int pageSize) {
        return findPage(query, pageNum, pageSize, null);
    }

    @Override
    public IPage<E> findPage(Query query, int pageNum, int pageSize, String order) {
        PageUtil.startPage(pageNum, pageSize, order);
        List<E> list = mapper.findList(query);
        Page<E> result = (Page<E>) list;

        return new IPage<>(result.getPageNum(), result.getPageSize(), result.size(),
                           (int) result.getTotal(), result.getPages(), result);
    }

    @Override
    public IPage<E> findPage(Query query, PageParam pageParam) {
        return findPage(query, pageParam.getPageNum(), pageParam.getPageSize());
    }

    @Override
    public IPage<E> findPage(Query query, PageParam pageParam, String order) {
        return findPage(query, pageParam.getPageNum(), pageParam.getPageSize(), order);
    }

    @Override
    public int batchInsert(List<E> list) {
        return mapper.batchInsert(list);
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return mapper.deleteByPrimaryKey(id);
    }

    @Override
    public Map<Long, E> findIdMap(Query query) {
        List<E> list = mapper.findList(query);
        return list.stream()
            .collect(Collectors.toMap(BaseEntity::getId, a -> a, (k1, k2) -> k1));
    }

}
