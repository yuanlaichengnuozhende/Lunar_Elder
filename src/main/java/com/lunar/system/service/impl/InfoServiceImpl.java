package com.lunar.system.service.impl;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.consts.DictConsts;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.localcache.utils.LocalCacheUtil;
import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.Info;
import com.lunar.system.entity.InfoContent;
import com.lunar.system.enums.InfoStatus;
import com.lunar.system.mapper.InfoMapper;
import com.lunar.system.query.InfoContentQuery;
import com.lunar.system.query.InfoQuery;
import com.lunar.system.request.InfoReq;
import com.lunar.system.service.InfoContentService;
import com.lunar.system.service.InfoService;
import com.lunar.system.service.UserService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.consts.DictConsts;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.system.service.InfoContentService;
import com.lunar.system.service.InfoService;
import com.lunar.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InfoServiceImpl extends BaseServiceImpl<InfoMapper, Info>
    implements InfoService {

    @Autowired
    private InfoContentService infoContentService;
    @Autowired
    private UserService userService;

    @Override
    public IPage<Info> page(PageParam pageParam, String likeTitle, String infoType, InfoStatus infoStatus,
                            String order) {
        log.info("资讯分页列表. pageParam={}, likeTitle={}, infoType={}, infoStatus={}, order={}", pageParam, likeTitle,
            infoType, infoStatus, order);

        IPage<Info> page = this.findPage(
            InfoQuery.builder()
                .companyId(SecurityUtils.getCompanyId())
                .infoType(infoType)
                .likeTitle(likeTitle)
                .infoStatus(infoStatus)
                .deleted(false)
                .build()
            , pageParam, order);

        // 发布人
        List<Long> publidByList = page.getList()
            .stream()
            .map(Info::getPublishBy)
            .filter(NumUtil::notNullOrZero)
            .distinct()
            .collect(Collectors.toList());
        Map<Long, String> userMap = userService.getUserIdMap(publidByList);

        page.getList().forEach(x -> {
            // 反显字典
            x.setInfoType(LocalCacheUtil.getEnumLabelByDictValue(DictConsts.infoType, x.getInfoType()));

            x.setPublishByName(userMap.getOrDefault(x.getPublishBy(), ""));
        });

        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Info add(InfoReq req) {
        log.info("新增资讯");

        Info info = Info.builder()
            .companyId(SecurityUtils.getCompanyId())
            .infoType(req.getInfoType())
            .title(req.getTitle())
            .infoStatus(InfoStatus.WAIT_PUBLISH)
            .deleted(false)
            .build();
        this.insertSelective(info);

        InfoContent infoContent = InfoContent.builder()
            .infoId(info.getId())
            .content(req.getContent())
            .attachment(req.getAttachment())
            .build();
        infoContentService.insertSelective(infoContent);

        log.info("新增资讯完成");

        return info;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Info edit(InfoReq req) {
        log.info("编辑资讯");

        Info info = this.selectOne(
            InfoQuery.builder().id(req.getId()).companyId(SecurityUtils.getCompanyId()).build());
        if (info == null) {
            throw new ServiceException(ApiCode.DATA_ERROR);
        }

        BeanUtils.copyProperties(req, info);
        this.updateSelective(info);

        InfoContent infoContent = infoContentService.selectOne(InfoContentQuery.builder().infoId(info.getId()).build());
        infoContent.setAttachment(req.getAttachment());
        infoContent.setContent(req.getContent());
        infoContentService.update(infoContent);

        log.info("编辑资讯完成");

        return info;
    }

}