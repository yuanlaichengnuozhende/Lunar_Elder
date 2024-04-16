package com.lunar.common.mybatis.service;

import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.mybatis.query.Query;

import java.util.List;
import java.util.Map;

public interface BaseService<E> {

    E selectByPrimaryKey(Long id);

    E insert(E entity);

    E insertSelective(E entity);

    int updateSelective(E entity);

    int update(E entity);

    E selectOne(Query query);

    E selectFirst(Query query);

    E selectFirst(Query query, String order);

    int count(Query query);

    List<E> findList(Query query);

    List<E> findList(Query query, String order);

    IPage<E> findPage(Query query, int pageNum, int pageSize);

    IPage<E> findPage(Query query, int pageNum, int pageSize, String order);

    IPage<E> findPage(Query query, PageParam pageParam);

    IPage<E> findPage(Query query, PageParam pageParam, String order);

    int batchInsert(List<E> list);

    int deleteByPrimaryKey(Long id);

    Map<Long, E> findIdMap(Query query);

}