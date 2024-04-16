package com.lunar.system.mapper;

import com.lunar.common.mybatis.mapper.BaseMapper;
import com.lunar.system.entity.LibUnitConversion;
import org.apache.ibatis.annotations.Param;

public interface LibUnitConversionMapper extends BaseMapper<LibUnitConversion> {

    /**
     * 查询from-to或to-from换算
     *
     * @param unitFrom
     * @param unitTo
     * @return
     */
    LibUnitConversion selectConvert(@Param("unitFrom") String unitFrom, @Param("unitTo") String unitTo);

}