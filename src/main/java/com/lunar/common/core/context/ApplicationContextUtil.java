package com.lunar.common.core.context;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author szx
 * @date 2019/12/19 21:21
 */
@Slf4j
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    /**
     * Spring应用上下文环境
     */
    @Setter
    private static ApplicationContext ctx;

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) ctx.getBean(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class clz) {
        return (T) ctx.getBean(clz);
    }

    public static ApplicationContext getCtx() {
        return ctx;
    }

    /**
     * 实现了ApplicationContextAware 接口，必须实现该方法；
     * <p>
     * 通过传递applicationContext参数初始化成员变量applicationContext
     *
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        synchronized (ApplicationContextUtil.class) {
            if (ApplicationContextUtil.ctx == null) {
                setCtx(applicationContext);
            }
        }
    }


}
