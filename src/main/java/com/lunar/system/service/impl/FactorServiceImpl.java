package com.lunar.system.service.impl;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.enums.GasType;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.utils.ConvertUtil;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.localcache.utils.UnitUtil;
import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.Factor;
import com.lunar.system.entity.FactorGas;
import com.lunar.system.mapper.FactorMapper;
import com.lunar.system.query.FactorGasQuery;
import com.lunar.system.request.FactorReq;
import com.lunar.system.service.FactorGasService;
import com.lunar.system.service.FactorService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.enums.GasType;
import com.lunar.common.core.utils.ConvertUtil;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.system.service.FactorGasService;
import com.lunar.system.service.FactorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FactorServiceImpl extends BaseServiceImpl<FactorMapper, Factor>
    implements FactorService {

    @Autowired
    private FactorGasService factorGasService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Factor add(FactorReq req) {
        log.info("新增排放因子. req={}", req);

        Long userId = SecurityUtils.getUserId();

        Factor factor = CopyUtil.copyObject(req, Factor.class);
        factor.setId(null);
        factor.setCreateBy(userId);
        factor.setUpdateBy(userId);
        this.insertSelective(factor);
        log.info("因子新增成功");

        req.getGasList().forEach(x -> {
            x.setId(null);
            x.setFactorId(factor.getId());
            x.setCreateBy(userId);
            x.setUpdateBy(userId);
            x.setCreateTime(new Date());
        });
        factorGasService.batchInsert(req.getGasList());
        log.info("温室气体新增成");

        // 使用二氧化碳当量的分子/分母
        FactorGas gas = req.getGasList()
            .stream()
            .filter(x -> GasType.getByName(x.getGasType()) == GasType.CO2E)
            .findAny()
            .orElse(new FactorGas());
        factor.setFactorValue(gas.getFactorValue());
        factor.setUnit(getUnit(gas));
        this.updateSelective(factor);

        return factor;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Factor edit(FactorReq req) {
        log.info("编辑排放因子. req={}", req);

        Long userId = SecurityUtils.getUserId();

        Factor factor = this.selectByPrimaryKey(req.getId());
        if (factor == null) {
            throw new ServiceException(ApiCode.DATA_ERROR);
        }

        Map<String, FactorGas> gasMap = ConvertUtil.list2Map(req.getGasList(), FactorGas::getGasType);
        List<FactorGas> gasList = factorGasService.findList(FactorGasQuery.builder().factorId(factor.getId()).build());
        gasList.forEach(x -> {
            FactorGas reqGas = gasMap.get(x.getGasType());
            if (reqGas == null) {
                return;
            }
            BeanUtils.copyProperties(reqGas, x, "id", "factorId");
            x.setUpdateBy(userId);
            x.setUpdateTime(new Date());
            factorGasService.updateSelective(x);
            log.info("更新气体完成");
        });

        // 使用二氧化碳当量的分子/分母
        FactorGas gas = req.getGasList()
            .stream()
            .filter(x -> GasType.getByName(x.getGasType()) == GasType.CO2E)
            .findAny()
            .orElse(new FactorGas());
        factor.setFactorValue(gas.getFactorValue());
        factor.setUnit(getUnit(gas));

        BeanUtils.copyProperties(req, factor);
        factor.setUpdateBy(userId);
        factor.setUpdateTime(new Date());
        this.updateSelective(factor);
        log.info("因子更新成功");

        return factor;
    }

    /**
     * 使用二氧化碳当量的分子/分母
     *
     * @param gas
     * @return
     */
    private String getUnit(FactorGas gas) {
        return UnitUtil.formatCO2E(gas.getFactorUnitZ()) + "/" + UnitUtil.formatFactUnitM(gas.getFactorUnitM());
    }

}