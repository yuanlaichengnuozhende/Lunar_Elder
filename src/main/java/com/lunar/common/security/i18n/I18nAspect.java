package com.lunar.common.security.i18n;

/**
 * @author szx
 * @date 2022/09/13 17:45
 */
//@Slf4j
//@Aspect
//@Component
//@Order(1)
//public class I18nAspect {
//
//    /**
//     * 配置切点
//     */
//    @Pointcut("execution(* com.carbonstop..*.controller.*.*(..))")
//    public void logPointCut() {
//    }
//
//    /**
//     * 处理完请求后执行
//     *
//     * @param joinPoint 切点
//     */
//    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
//    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
//        handleResult(joinPoint, jsonResult);
//    }
//
//    /**
//     * 处理返回
//     *
//     * @param joinPoint
//     * @param jsonResult
//     */
//    protected void handleResult(final JoinPoint joinPoint, Object jsonResult) {
//        if (!(jsonResult instanceof ApiResult)) {
//            log.info("返回值不是ApiResult，不做处理");
//            return;
//        }
//
//        ApiResult apiResult = (ApiResult) jsonResult;
//
//        // 查询用户当前语言 200也处理
//        String lang = I18nUtil.getUserLang(SecurityUtils.getUserId());
//
//        // 查询错误码翻译
//        String msg = I18nUtil.getMsgDefaultIfBlank(lang, String.valueOf(apiResult.getCode()), apiResult.getMsg());
//
//        apiResult.setMsg(msg);
//    }
//
//}
