package com.lunar.common.meta.mybatis;

import cn.hutool.core.util.ReflectUtil;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.security.utils.SecurityUtils;
import com.google.common.base.Splitter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.mybatis.entity.BaseEntity;
import com.lunar.common.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

/**
 * mybatis插件，自动填充companyId、createBy、updateBy、createTime、updateTime
 *
 * @author szx
 * @date 2023/03/20 20:02
 */
@Slf4j
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class MybatisMetaObjectHandler implements Interceptor {

    private static final String COMPANY_ID = "companyId";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            String sqlMethod = mappedStatement.getId();
            List<String> methodList = Splitter.on(".").splitToList(sqlMethod);
            if (CollectionUtils.size(methodList) < 1) {
                return invocation.proceed();
            }

            String method = methodList.get(methodList.size() - 1);
            Object parameter = invocation.getArgs()[1];

            log.debug("sqlMethod=[{}], method=[{}], parameter=[{}]", sqlMethod, method, parameter);

            Long userId = SecurityUtils.getUserId();
            Long companyId = SecurityUtils.getCompanyId();
            // 是否有companyId字段
            boolean hasCompanyId = ReflectUtil.hasField(parameter.getClass(), COMPANY_ID);

            // 1. insert：companyId、create_by、update_by、create_time、update_time，优先使用传入值，未传入则自动填充默认值
            // 2. batchInsert: companyId、create_by、update_by、create_time，优先使用传入值，未传入则自动填充默认值
            //    xml中，不接收updateTime
            // 3. update: update_by，优先使用默认值
            //    xml中，update和updateSelective默认不接收updateTime
            switch (method) {
                case "insert":
                case "insertSelective":
                    if (hasCompanyId) {
                        Object companyIdValue = ReflectUtil.getFieldValue(parameter, COMPANY_ID);
                        // obj中companyId没有值，则使用默认值
                        if (companyIdValue == null) {
                            try {
                                ReflectUtil.setFieldValue(parameter, COMPANY_ID, companyId);
                            } catch (Exception e) {
                                log.error("MetaObjectHandler ReflectUtil error");
                            }
                        }
                    }
                    BaseEntity insert = (BaseEntity) parameter;
                    insert.setId(null);
                    insert.setCreateBy(ObjectUtils.defaultIfNull(insert.getCreateBy(), userId));
                    insert.setUpdateBy(ObjectUtils.defaultIfNull(insert.getUpdateBy(), userId));
                    insert.setCreateTime(ObjectUtils.defaultIfNull(insert.getCreateTime(), new Date()));
                    insert.setUpdateTime(ObjectUtils.defaultIfNull(insert.getUpdateTime(), new Date()));
                    break;
                case "update":
                case "updateSelective":
                    BaseEntity update = (BaseEntity) parameter;
                    // 更新人。默认使用SecurityUtils.getUserId()
                    Long updateBy = (NumUtil.isNullOrZero(userId) ? update.getUpdateBy() : userId);
                    update.setUpdateBy(updateBy);
                    break;
                case "batchInsert":
                    Map paramMap = (Map) parameter;
                    List<BaseEntity> entityList = (List<BaseEntity>) paramMap.get("list");
                    if (CollectionUtils.isEmpty(entityList)) {
                        break;
                    }
                    entityList.forEach(x -> {
                        if (hasCompanyId) {
                            Object batchCompanyId = ReflectUtil.getFieldValue(x, COMPANY_ID);
                            // obj中companyId没有值，则使用默认值
                            if (batchCompanyId == null) {
                                try {
                                    ReflectUtil.setFieldValue(x, COMPANY_ID, companyId);
                                } catch (Exception e) {
                                    log.error("MetaObjectHandler ReflectUtil batchInsert error");
                                }
                            }
                        }
                        x.setId(null);
                        x.setCreateBy(ObjectUtils.defaultIfNull(x.getCreateBy(), userId));
                        x.setUpdateBy(ObjectUtils.defaultIfNull(x.getUpdateBy(), userId));
                        x.setCreateTime(ObjectUtils.defaultIfNull(x.getCreateTime(), new Date()));
                    });
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("MetaObjectHandler error", e);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        // 判断是否拦截这个类型对象（根据@Intercepts注解决定），然后决定是返回一个代理对象还是返回原对象。
        // 我们在实现plugin方法时，要判断一下目标类型，如果是插件要拦截的对象时才执行Plugin.wrap方法，否则的话，直接返回目标本身。
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }

}
