package com.lunar.system.controller;

import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.mybatis.model.Order;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.system.entity.Company;
import com.lunar.system.query.CompanyQuery;
import com.lunar.system.service.CompanyService;
import com.lunar.common.core.domain.ApiResult;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "租户")
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * 全量租户列表
     */
    @ApiIgnore
    @InnerAuth
    @GetMapping("/inner/allList")
    public ApiResult<List<Company>> innerAllList() {
        List<Company> list = companyService.findList(null, Order.idAsc());
        return ApiResult.ok(list);
    }

    /**
     * 通过uscc获取租户
     */
    @InnerAuth
    @ApiIgnore
    @GetMapping("/inner/uscc/{uscc}")
    public ApiResult<Company> getByUscc(@PathVariable("uscc") String uscc) {
        Company company = companyService.selectOne(CompanyQuery.builder().uscc(uscc).build());
        return ApiResult.ok(company);
    }

}