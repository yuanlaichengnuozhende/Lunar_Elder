package com.lunar.system.service.impl;

import cn.hutool.core.convert.Convert;
import com.lunar.common.core.utils.AreaUtil;
import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.system.entity.LibAddress;
import com.lunar.system.mapper.LibAddressMapper;
import com.lunar.system.query.LibAddressQuery;
import com.lunar.system.service.LibAddressService;
import com.google.common.base.Joiner;
import com.lunar.common.core.utils.AreaUtil;
import com.lunar.system.service.LibAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LibAddressServiceImpl extends BaseServiceImpl<LibAddressMapper, LibAddress>
    implements LibAddressService {

    @Override
    public List<LibAddress> findAddressHierarchy(Integer code) {
        Set<Integer> codeSet = AreaUtil.getCodeSet(code);
        // 查询数据中所有code
        return this.findList(LibAddressQuery.builder().codeList(codeSet).build(), "address_code asc");
    }

    @Override
    public String findAddressHierarchyStr(Integer code, String separator) {
//        separator = StringUtils.defaultIfBlank(separator, "");
        separator = Convert.toStr(separator, "");
        List<String> orgHierarchy = findAddressHierarchy(code)
            .stream().map(LibAddress::getAddressName).collect(Collectors.toList());
        return Joiner.on(separator).join(orgHierarchy);
    }

}