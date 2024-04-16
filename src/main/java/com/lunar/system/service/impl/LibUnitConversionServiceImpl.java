package com.lunar.system.service.impl;

import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.system.dto.UnitConvertPairDto;
import com.lunar.system.entity.LibUnitConversion;
import com.lunar.system.mapper.LibUnitConversionMapper;
import com.lunar.system.query.LibUnitConversionQuery;
import com.lunar.system.service.LibUnitConversionService;
import com.google.common.collect.Maps;
import com.lunar.system.service.LibUnitConversionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class LibUnitConversionServiceImpl extends BaseServiceImpl<LibUnitConversionMapper, LibUnitConversion>
    implements LibUnitConversionService {

    @Override
    public String convert(String unitFrom, String unitTo) {
        log.info("单位换算. unitFrom={}, unitTo={}", unitFrom, unitTo);

        if (StringUtils.isAnyBlank(unitFrom, unitTo)) {
            return null;
        }

        if (unitFrom.equalsIgnoreCase(unitTo)) {
            return "1";
        }

        LibUnitConversion entity = selectConvert(unitFrom, unitTo);
        if (entity == null) {
            return null;
        }

        if (unitFrom.equals(entity.getUnitFrom()) && Objects.equals(unitTo, entity.getUnitTo())) {
            return entity.getUnitValue().trim();
        } else {
            return BigDecimal.ONE.divide(new BigDecimal(entity.getUnitValue().trim()), 20, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
        }
    }

    @Override
    public Map<String, String> batchConvert(List<UnitConvertPairDto> unitConvertPairList) {
        if (CollectionUtils.isEmpty(unitConvertPairList)) {
            return Maps.newHashMap();
        }

        Map<String, String> map = Maps.newHashMap();
        for (UnitConvertPairDto pair : unitConvertPairList) {
            String value = convert(pair.getUnitFrom(), pair.getUnitTo());
            String key = pair.getUnitFrom() + "-" + pair.getUnitTo();
            map.put(key, value);
        }

        return map;
    }

    @Override
    public Map<String, String> allConvert() {
        List<LibUnitConversion> list = this.findList(LibUnitConversionQuery.builder().deleted(false).build());

        Map<String, String> map = Maps.newHashMap();

        for (LibUnitConversion entity : list) {
            String key1 = entity.getUnitFrom() + "-" + entity.getUnitTo();
            String value1 = entity.getUnitValue().trim();
            map.put(key1, value1);

            String key2 = entity.getUnitTo() + "-" + entity.getUnitFrom();
            String value2 = BigDecimal.ONE.divide(
                    new BigDecimal(entity.getUnitValue().trim()), 20, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
            map.put(key2, value2);
        }

        return map;
    }

    @Override
    public LibUnitConversion selectConvert(String unitFrom, String unitTo) {
        return mapper.selectConvert(unitFrom, unitTo);
    }
}