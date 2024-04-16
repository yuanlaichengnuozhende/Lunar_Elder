package com.lunar.common.mybatis.mapper;

import com.lunar.common.mybatis.query.Query;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseMapper<E> {

    E selectByPrimarszxey(Long id);

    int insert(E entity);

    int insertSelective(E entity);

    int updateSelective(E entity);

    int update(E entity);

    int count(Query query);

    List<E> findList(Query query);

    int batchInsert(@Param("list") List<E> list);

    int deleteByPrimarszxey(Long id);

}
