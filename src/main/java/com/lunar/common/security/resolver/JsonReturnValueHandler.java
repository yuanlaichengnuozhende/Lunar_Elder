package com.lunar.common.security.resolver;

import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.ApiResult;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * [ON-DEMAND]
 */
@Deprecated
public class JsonReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        Class<?> type = returnType.getParameterType();
        return ApiResult.class.equals(type);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
//        RequestMapping requestMapping = returnType.getMethod().getAnnotation(RequestMapping.class);
//        String contentType = null;
//        if (requestMapping != null) {
//            String[] produces = requestMapping.produces();
//            if (produces.length > 0) {
//                contentType = produces[0];
//            }
//        }
//        //使用json视图
//        mavContainer.setView(new JsonView(returnValue, contentType));

        mavContainer.setView(new JsonView(returnValue));
    }
}

