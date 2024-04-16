package com.lunar.system.service;

import com.lunar.common.mybatis.service.BaseService;
import com.lunar.system.dto.UnitConvertPairDto;
import com.lunar.system.entity.LibUnitConversion;

import java.util.List;
import java.util.Map;

public interface LibUnitConversionService extends BaseService<LibUnitConversion> {

    /**
     * 单位换算，无换算值返回null
     *
     * @param unitFrom
     * @param unitTo
     * @return
     */
    String convert(String unitFrom, String unitTo);

    /**
     * 批量单位换算
     *
     * @param unitConvertPairList
     * @return key=fromCode-toCode  value=换算比例值
     */
    Map<String, String> batchConvert(List<UnitConvertPairDto> unitConvertPairList);

    /**
     * 所有单位换算
     *
     * @return
     */
    Map<String, String> allConvert();

    /**
     * 查询from-to或to-from换算
     *
     * @param unitFrom
     * @param unitTo
     * @return
     */
    LibUnitConversion selectConvert(String unitFrom, String unitTo);

}
