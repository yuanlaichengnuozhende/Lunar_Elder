package com.lunar.common.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

/**
 * 包装HttpServletRequest对象，缓存body里面的数据，再次读取的时候从缓存里面读取
 *
 * @author szx
 * @date 2019/08/01 18:37
 */
@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {

    /** get方法 */
    private Map<String, String[]> parameterMap;
    /** post 方法body数据 */
    private byte[] requestBody = null;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        //缓存请求body
        try {
            parameterMap = request.getParameterMap();
            //复制request中的bufferedReader中的值
            requestBody = StreamUtils.copyToByteArray(request.getInputStream());
        } catch (IOException e) {
            log.error("RequestWrapper error", e);
        }
    }

    /**
     * 获取所有参数名, get相关方法重写
     *
     * @return 返回所有参数名
     */
    @Override
    public Enumeration<String> getParameterNames() {
        Vector<String> vector = new Vector<>(parameterMap.keySet());
        return vector.elements();
    }

    /**
     * 获取指定参数名的值，如果有重复的参数名，则返回第一个的值 接收一般变量 ，如text类型
     *
     * @param name 指定参数名
     * @return 指定参数名的值
     */
    @Override
    public String getParameter(String name) {
        String[] results = parameterMap.get(name);
        if (results == null || results.length <= 0) {
            return null;
        } else {
            return modify(results[0]);
        }
    }

    /**
     * 获取指定参数名的所有值的数组，如：checkbox的所有数据 接收数组变量 ，如checkbox类型
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] results = parameterMap.get(name);
        if (results == null || results.length <= 0) {
            return null;
        } else {
            int length = results.length;
            for (int i = 0; i < length; i++) {
                results[i] = modify(results[i]);
            }
            return results;
        }
    }

    /**
     * 自定义的一个简单修改原参数的方法，即：给原来的参数值前面添加了一个修改标志的字符串
     *
     * @param string 原参数值
     * @return 修改之后的值 ,这里并不进行改变
     */
    private String modify(String string) {
        return string;
    }

    /**
     * 重写 getReader()
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * 重写 getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (requestBody == null) {
            requestBody = new byte[0];
        }
        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
        CustomServletInputStream customServletInputStream = new CustomServletInputStream(bais);
        return customServletInputStream;
    }

    // 自定义ServletInputStream
    class CustomServletInputStream extends ServletInputStream {

        private InputStream inputStream;

        public CustomServletInputStream(InputStream inputStream) {
            super();
            this.inputStream = inputStream;
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            return;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}