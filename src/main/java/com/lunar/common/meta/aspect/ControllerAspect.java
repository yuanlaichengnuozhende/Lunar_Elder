package com.lunar.common.meta.aspect;

import cn.hutool.core.util.ReflectUtil;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.meta.annotation.ControllerMeta;
import com.lunar.common.meta.enums.ControllerMetaType;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.service.UserService;
import com.google.common.collect.Lists;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.meta.annotation.ControllerMeta;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * controller元数据处理 自动填充createByName、updateByName、orgName
 *
 * @author szx
 * @date 2023/03/21 20:02
 */
@Slf4j
@Aspect
@Component
public class ControllerAspect {

    private static final String CREATE_BY_NAME = "createByName";
    private static final String UPDATE_BY_NAME = "updateByName";
    private static final String ORG_NAME = "orgName";
    private static final String ORG_ID = "orgId";

    @Autowired
    private UserService userFeignService;

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @SuppressWarnings("rawtypes")
    @AfterReturning(value = "@annotation(controllerMeta)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, ControllerMeta controllerMeta, Object jsonResult) {
        if (!(jsonResult instanceof ApiResult)) {
            return;
        }

        ApiResult apiResult = (ApiResult) jsonResult;
        if (apiResult.getSuccessData() == null) {
            return;
        }

        try {
            List<? extends BaseEntity> list = null;

            if (apiResult.getSuccessData() instanceof IPage) {
                IPage page = (IPage) apiResult.getSuccessData();
                list = page.getList();
            }
            if (apiResult.getSuccessData() instanceof List) {
                list = (List<? extends BaseEntity>) apiResult.getSuccessData();
            }
            if (apiResult.getSuccessData() instanceof BaseEntity) {
                list = Lists.newArrayList((BaseEntity) apiResult.getSuccessData());
            }

            if (CollectionUtils.isEmpty(list)) {
                return;
            }

            Class<? extends BaseEntity> entityClz = list.get(0).getClass();

            for (ControllerMetaType type : controllerMeta.value()) {
                switch (type) {
                    case CREATE_BY:
                        if (!ReflectUtil.hasField(entityClz, CREATE_BY_NAME)) {
                            break;
                        }
                        Map<Long, String> createByMap = userFeignService.getCreateByMap(list);
                        list.forEach(x -> {
                            String username = createByMap.getOrDefault(x.getCreateBy(), "");
                            ReflectUtil.setFieldValue(x, CREATE_BY_NAME, username);
                        });
                        break;
                    case UPDATE_BY:
                        if (!ReflectUtil.hasField(entityClz, UPDATE_BY_NAME)) {
                            break;
                        }
                        Map<Long, String> updateByMap = userFeignService.getUpdateByMap(list);
                        list.forEach(x -> {
                            String username = updateByMap.getOrDefault(x.getUpdateBy(), "");
                            ReflectUtil.setFieldValue(x, UPDATE_BY_NAME, username);
                        });
                        break;
                    case ORG_NAME:
                        if (!ReflectUtil.hasField(entityClz, ORG_NAME)
                            || !ReflectUtil.hasField(entityClz, ORG_ID)) {
                            break;
                        }
                        Map<Long, String> orgMap = SecurityUtils.getOrgMap();
                        list.forEach(x -> {
                            Long orgId = (Long) ReflectUtil.getFieldValue(x, ORG_ID);
                            if (orgId == null) {
                                return;
                            }
                            String orgName = orgMap.getOrDefault(orgId, "");
                            ReflectUtil.setFieldValue(x, ORG_NAME, orgName);
                        });
                        break;
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            log.error("controllerMeta error", e);
        }
    }

}
