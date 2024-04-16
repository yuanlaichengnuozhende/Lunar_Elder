package com.lunar.common.core.utils;

import cn.hutool.core.lang.Assert;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author szx
 * @date 2019/11/19 15:58
 */
public class RestUtil {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(RestUtil.class);

    private static RestTemplate restTemplate;

    static {
        // 默认的是JDK提供http连接，可以通过setRequestFactory方法替换为例如Apache HttpComponents、Netty或OkHttp等其它HTTP library。
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 使用httpclient
        // 长连接保持30秒
        PoolingHttpClientConnectionManager pollingConnectionManager =
            new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS);
        // 总连接数
        pollingConnectionManager.setMaxTotal(1000);
        // 同路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(1000);

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(pollingConnectionManager);
        // 重试次数，默认是3次，没有开启
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(3, true));
        // 保持长连接配置，需要在头添加Keep-Alive
        httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());

        HttpClient httpClient = httpClientBuilder.build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        // 数据读取超时时间，从服务器获取响应数据的超时时间。即SocketTimeout
        factory.setReadTimeout(10000);
        // 与服务器连接超时时间：httpclient会创建一个异步线程用以创建socket连接，此处设置该socket的连接超时时间
        factory.setConnectTimeout(10000);
        // 从连接池中获取连接的超时时间，不宜过长，必须设置，比如连接不够用时，时间过长将出现问题
        factory.setConnectionRequestTimeout(10000);

        restTemplate = new RestTemplate(factory);

        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : converters) {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(StandardCharsets.UTF_8);
                break;
            }
        }
    }

    public static String doGet(String url) {
        Assert.notBlank(url, "url cannot be null");
        return doGet(castToURI(url));
    }

    public static String doGet(URI uri) {
        Assert.notNull(uri, "uri cannot be null");
        log.info("http get request={}", uri);

        String resp = restTemplate.getForObject(uri, String.class);
        log.info("http get response={}", resp);

        return resp;
    }

    public static String doGet(String url, HttpEntity<String> entity) {
        Assert.notBlank(url, "url cannot be null");

        log.info("http get request={}", url);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String body = exchange.getBody();
        log.info("http get response={}", body);
        return body;
    }

    public static String doPostByText(String url, String params) {
        Assert.notBlank(url, "url cannot be null");
        return doPostByJson(castToURI(url), params, MediaType.TEXT_PLAIN, null);
    }

    public static String doPostByJson(String url, String params) {
        Assert.notBlank(url, "url cannot be null");
        return doPostByJson(castToURI(url), params, MediaType.APPLICATION_JSON, null);
    }

    public static String doPostByJson(URI uri, String params) {
        Assert.notNull(uri, "uri cannot be null");
        return doPostByJson(uri, params, MediaType.APPLICATION_JSON, null);
    }

    public static String doPostByJson(URI uri, String params, Map<String, String> headers) {
        Assert.notNull(uri, "uri cannot be null");
        return doPostByJson(uri, params, MediaType.APPLICATION_JSON, headers);
    }

    public static String doPostByJson(String url, String params, Map<String, String> headers) {
        Assert.notBlank(url, "url cannot be null");
        return doPostByJson(castToURI(url), params, MediaType.APPLICATION_JSON, headers);
    }

    public static String doPostByJson(URI uri, String params, MediaType contentType, Map<String, String> headers) {
        Assert.notNull(uri, "uri cannot be null");
        log.info("http post request={}, params={}, contentType={}, headers={}", uri, params, contentType, headers);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(contentType);

        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach(httpHeaders::set);
        }

        HttpEntity<String> entity = new HttpEntity<>(params, httpHeaders);

        String resp = restTemplate.postForObject(uri, entity, String.class);

        log.info("http post response={}", resp);

        return resp;
    }

    public static byte[] doPostBytesByJson(String url, String params) {
        Assert.notBlank(url, "url cannot be null");
        Assert.notBlank(params, "params cannot be null");
        log.info("http post request={}, params={}", url, params);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(params, httpHeaders);

        ResponseEntity<byte[]> resp = restTemplate.postForEntity(url, entity, byte[].class);

        return resp.getBody();
    }

    public static String doPost(String url) {
        Assert.notBlank(url, "url cannot be null");
        return doPostByForm(castToURI(url), null, null);
    }

    public static String doPostByForm(String url) {
        Assert.notBlank(url, "url cannot be null");
        return doPostByForm(castToURI(url), null, null);
    }

    public static String doPostByForm(String url, Map<String, String> params) {
        Assert.notBlank(url, "url cannot be null");
        return doPostByForm(castToURI(url), params, null);
    }

    public static String doPostByForm(String url, Map<String, String> params, Map<String, String> headers) {
        Assert.notBlank(url, "url cannot be null");
        return doPostByForm(castToURI(url), params, headers);
    }

    public static String doPostByForm(URI uri, Map<String, String> params, Map<String, String> headers) {
        Assert.notNull(uri, "uri cannot be null");
        log.info("http post request={}, params={}, headers={}", uri, params, headers);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach(httpHeaders::set);
        }

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        if (MapUtils.isNotEmpty(params)) {
            params.forEach(map::set);
        }

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, httpHeaders);

        String resp = restTemplate.postForObject(uri, entity, String.class);

        log.info("http post response={}", resp);

        return resp;
    }

    @SneakyThrows
    private static URI castToURI(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw e;
        }
    }

    public static void main(String[] args) {
//        String resp = doGet("https://www.baidu.com");
//        System.out.println(resp);

        String url = "https://applications.icao.int/icec/Home/getCompute";

        String params = "{"
            + "\"userID\": \"Lu\","
            + "\"unitofMeasureTag\": \"1\","
            + "\"triptype\": \"One Way\","
            + "\"cabinclass\": \"Premium\","
            + "\"noofpassenger\": \"1\","
            + "\"noofArrAirport\": \"1\","
            + "\"depCode\": \"PEK\","
            + "\"arrCode1\": \"HKG\","
            + "\"arrCode2\": \"#\","
            + "\"arrCode3\": \"#\""
            + "}";

        String resp = doPostByJson(url, params);
        System.out.println(resp);
    }

}
