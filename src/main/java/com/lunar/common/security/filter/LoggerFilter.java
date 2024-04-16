package com.lunar.common.security.filter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.ContentType;
import com.lunar.common.core.consts.SwitchConsts;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lunar.common.core.consts.SwitchConsts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author szx
 * @date 2019/07/21 14:46
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "logFilter", urlPatterns = "/*")
public class LoggerFilter implements Filter {

    @Value("${spring.profiles.active:prod}")
    private String env;

    private static final String REQUEST_ID = "request-id";
    private static final String REQUEST_TIME = "request-time";
    private static final String OPERATION_COST_TIME = "cost-time";
    private static final String URL = "url";
    private static final String CONTENT_TYPE = "content-type";
    private static final String RESPONSE_URL = "request-url";
    private static final String METHOD = "method";
    private static final String HEAD = "head";
    private static final String COOKIES = "cookies";
    private static final String PARAMS = "params";
    private static final String BODY = "body";
    private static final String STATUS = "status";

    private static final List<String> PASSWORD_KEYS =
        Lists.newArrayList("passwd", "password", "oldPassword", "newPassword");

    /**
     * entity最大打印长度
     */
    private static final int BODY_PRINT_LENGTH = 20000;

//    private SerializerFeature[] features = new SerializerFeature[]{SerializerFeature.WriteNullStringAsEmpty,
//        SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.QuoteFieldNames};

    private static final List<String> HEAD_NOT_INCLUDE =
        Lists.newArrayList("Accept", "Accept-Encoding", "Accept-Charset",
                           "Accept-Language", "Connection",
                           "Content-Encoding", "Content-Type", "Vary",
                           "Cache-Control", "Cookie", "Host", "accept",
                           "accept-encoding", "accept-charset",
                           "accept-language", "connection",
                           "content-encoding", "content-type", "vary",
                           "cache-control", "cookie", "host",
                           "Content-Length", "SLB-IP", "User-Agent",
                           "Sec-Fetch-Dest", "Sec-Fetch-Mode",
                           "Sec-Fetch-User", "Postman-Token",
                           "X-Forwarded-Proto", "Sec-Fetch-Site", "SLB-ID",
                           "sec-ch-ua", "X-Tag", "Transfer-Encoding", "Request-Origion", "sec-ch-ua-mobile",
                           "sec-ch-ua-platform", "content-length");

    static {
        if (!SwitchConsts.LOG_TOKEN_SWITCH) {
            // 不打印token
            HEAD_NOT_INCLUDE.add("Authorization");
        }
    }

    private final AtomicLong id = new AtomicLong(1);

    /**
     * request log
     *
     * @param request
     * @return
     */
    private Map<String, Object> requestLogger(RequestWrapper request) {
        //请求参数
        Map<String, Object> param = Maps.newHashMap();
        //打印参数，有序
        Map<String, Object> map = Maps.newLinkedHashMap();

        Long requestId = id.getAndIncrement();
        param.put(REQUEST_ID, requestId);
        map.put(REQUEST_ID, requestId);

        param.put(REQUEST_TIME, System.currentTimeMillis());
        map.put(REQUEST_TIME, DateUtil.now());

        StringBuffer url = request.getRequestURL();
        param.put(URL, url);
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            url.append("?").append(request.getQueryString());
        }
        map.put(URL, url);

//        if (!EnvType.PROD.getActive().equalsIgnoreCase(env)) {
        StringBuilder sb = new StringBuilder("curl --location --request ");
        // url
        sb.append(request.getMethod()).append(" '").append(url).append("' \\\n");

        // content-type
        String contentType = StringUtils.isBlank(
            request.getContentType()) ? HttpMethod.GET.name() : request.getContentType();
        sb.append("--header 'Content-Type: ").append(contentType).append("' \\\n");

        // header
        Enumeration<String> e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            String headName = e.nextElement();
            if (!HEAD_NOT_INCLUDE.contains(headName)) {
                sb.append("--header '")
                    .append(headName)
                    .append(": ")
                    .append(request.getHeader(headName))
                    .append("' \\\n");
            }
        }

        // cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            StringBuilder cookie = new StringBuilder();
            for (Cookie c : cookies) {
                cookie.append(c.getName()).append("=").append(c.getValue()).append("; ");
            }
            sb.append("--header 'Cookie: ").append(cookie).append("' \\\n");
        }

        RequestWrapper wrapper = WebUtils.getNativeRequest(request, RequestWrapper.class);
        if (wrapper != null) {
            // application/json
            if (contentType.startsWith(ContentType.JSON.getValue())) {
                String body = getRequestBody(wrapper);

                if (body.startsWith("data:image")) {
                    body = "data:image" + "...";
                }
                //替换密码为******   PASSWORD_KEYS为预设定的密码字段keys
                String desensitize = desensitize(body);
                sb.append("--data-raw '").append(desensitize).append("'");
            }
            // params
            String prefix = "";
            // application/x-www-form-urlencoded
            if (contentType.startsWith(ContentType.FORM_URLENCODED.getValue())) {
                prefix = "--data-urlencode '";
            }
            // multipart/form-data
            if (contentType.startsWith(ContentType.MULTIPART.getValue())) {
                prefix = "--form '";
            }
            if (StringUtils.isNotBlank(prefix)) {
                Enumeration<String> enumeration = wrapper.getParameterNames();
                while (enumeration.hasMoreElements()) {
                    String paramName = enumeration.nextElement();
                    sb.append(prefix)
                        .append(paramName)
                        .append("=")
                        .append(wrapper.getParameter(paramName))
                        .append("' \\\n");
                }
            }
        }

        log.info("\n[request]\nrequest-id={}\nrequest-time={}\n{}", requestId, DateUtil.now(), sb.toString());

        return param;
//        }
//
//        param.put(METHOD, request.getMethod());
//        map.put(METHOD, request.getMethod());
//
//        map.put(CONTENT_TYPE, request.getContentType());
//
//        Enumeration<String> e = request.getHeaderNames();
//        Map<String, Object> headers = Maps.newHashMap();
//        while (e.hasMoreElements()) {
//            String headName = e.nextElement();
//            if (!HEAD_NOT_INCLUDE.contains(headName)) {
//                headers.put(headName, request.getHeader(headName));
//            }
//        }
//        map.put(HEAD, headers);
//
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null && cookies.length > 0) {
//            Map<String, Object> m = Maps.newHashMap();
//            for (Cookie c : cookies) {
//                m.put(c.getName(), c.getValue());
//            }
//            map.put(COOKIES, m);
//        }
//
//        String body = "";
//        RequestWrapper wrapper = WebUtils.getNativeRequest(request, RequestWrapper.class);
//        if (wrapper != null) {
//            // params
//            Map<String, Object> paramEntity = Maps.newHashMap();
//            Enumeration<String> enumeration = wrapper.getParameterNames();
//            while (enumeration.hasMoreElements()) {
//                String paramName = enumeration.nextElement();
//                paramEntity.put(paramName, wrapper.getParameter(paramName));
//            }
//            map.put(PARAMS, paramEntity);
//
//            // body
//            // 文件不打印内容
//            if (!Optional.of(request)
//                .map(RequestWrapper::getContentType)
//                .orElse("")
//                .contains(ContentType.MULTIPART.getValue())) {
//                body = getRequestBody(wrapper);
//            }
//
//            if (body.startsWith("data:image")) {
//                body = "data:image" + "...";
//            }
//            //替换密码为******   PASSWORD_KEYS为预设定的密码字段keys
//            body = PASSWORD_KEYS.stream()
//                .reduce(body, (result, next) -> result.replaceAll("(\"" + next + "\":\")(.*?)(\")",
//                    "$1******$3"));
//
//            if (Optional.of(request)
//                .map(RequestWrapper::getContentType)
//                .orElse("")
//                .contains(ContentType.JSON.getValue())) {
//                try {
//                    Object jsonObject = JSONObject.parse(body);
//                    map.put(BODY, jsonObject);
//                } catch (Exception ex) {
//                    log.error("json parse error. msg={}", ex.getMessage());
//                }
//            } else {
//                map.put(BODY, body);
//            }
//
//            //map.put(BODY, body);
//        }
//
//        log.info(JSON.toJSONString(map, features));
//
//        return param;
    }

    /**
     * 获取body
     *
     * @param wrapper
     */
    private String getRequestBody(RequestWrapper wrapper) {
        try {
            StringBuilder body = new StringBuilder();
            BufferedReader reader = wrapper.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line.trim());
            }
            reader.close();
            return body.toString();
        } catch (IOException e) {
            log.error("body reader error");
        }

        //byte[] buf = wrapper.getContentAsByteArray();
        //if (buf.length > 0) {
        //    String payload;
        //    try {
        //        payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
        //    } catch (UnsupportedEncodingException e) {
        //        payload = "[unknown]";
        //    }
        //    return payload.replaceAll("\\n", "").replaceAll(" ", "");
        //}
        return "";
    }

    /**
     * response log
     *
     * @param param
     * @param response
     */
    private void responseLogger(Map<String, Object> param, HttpServletResponse response) {
        try {
            ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response,
                ContentCachingResponseWrapper.class);
            //打印参数，有序
            Map<String, Object> map = Maps.newLinkedHashMap();

            Long requestId = (Long) param.get(REQUEST_ID);
            map.put(REQUEST_ID, requestId);

            map.put(STATUS, wrapper.getStatusCode());

            map.put(CONTENT_TYPE, response.getContentType());

            StringBuffer url = (StringBuffer) param.get(URL);
            map.put(RESPONSE_URL, url);

            Long requestTime = (Long) param.get(REQUEST_TIME);
            if (requestTime == null) {
                requestTime = 0L;
            }
            long costTime = System.currentTimeMillis() - requestTime;
            map.put(OPERATION_COST_TIME, costTime + "ms");

            byte[] body = wrapper.getContentAsByteArray();
            String entity = "";
            if (body != null) {
                //只打印json日志
                if (response.getContentType() != null && response.getContentType()
                    .contains(ContentType.JSON.getValue())) {
                    entity = new String(body);
                    if (entity.length() >= BODY_PRINT_LENGTH) {
                        String left = StringUtils.left(entity, BODY_PRINT_LENGTH) + "...";
                        map.put(BODY, left);
                    } else {
//                        Object object = JSON.parse(entity);
//                        map.put(BODY, object);
                        map.put(BODY, entity);
                    }
                } else {
                    entity = response.getContentType();
                    map.put(BODY, entity);
                }
            }

//            if (!EnvType.PROD.getActive().equalsIgnoreCase(env)) {
            StringBuilder sb = new StringBuilder("\n[response]");
            map.forEach((k, v) -> {
                sb.append("\n").append(k).append("=").append(v);
            });
            log.info(sb.toString());
//            } else {
//                log.info(JSON.toJSONString(map, features));
//            }
        } catch (Exception e) {
            log.error("responseLogger error, msg={}", e.getMessage());
        }
    }

    /**
     * json字符串脱敏
     *
     * 遍历PASSWORD_KEYS列表，并使用正则表达式来匹配和替换敏感字段的值。
     * 正则表达式中的(.*?)表示非贪婪匹配，即尽可能少的匹配字符。
     * 使用replaceAll方法将匹配到的敏感字段的值替换为"****"。
     *
     * @param jsonString
     * @return
     */
    private String desensitize(String jsonString) {
        for (String passwordKey : PASSWORD_KEYS) {
            String regex = "(\"" + passwordKey + "\"\\s*:\\s*\")(.*?)(\")";
            jsonString = jsonString.replaceAll(regex, "$1****$3");
        }
        return jsonString;
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response,
            ContentCachingResponseWrapper.class);
        responseWrapper.copyBodyToResponse();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        // 转为wrapper，使流信息可重复读
        RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(
            (HttpServletResponse) response);

        //过滤swagger
        String uri = requestWrapper.getRequestURI();
        String referer = requestWrapper.getHeader("referer");
        String str = uri + referer;
        boolean isSwagger = str.contains("swagger") || str.contains("api-docs");
        if (isSwagger) {
            log.info("过滤swagger. uri={}, referer={}", uri, referer);
            chain.doFilter(requestWrapper, response);
            return;
        }

        if ("/health".equals(uri)) {
            // health接口，不打印日志
            chain.doFilter(requestWrapper, response);
            return;
        }

        //处理request
        Map<String, Object> reqParam = requestLogger(requestWrapper);
        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            //处理response
            responseLogger(reqParam, responseWrapper);
            updateResponse(responseWrapper);
        }
    }

}