package com.lunar.system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author szx
 * @date 2023/05/29 20:19
 */
@Slf4j
@Service
public class FileService {

    /**
     * 域名或本机访问地址
     */
    @Value("${file.domain}")
    private String domain;

    /**
     * 内网地址
     */
    @Value("${file.internalDomain}")
    private String internalDomain;

    /**
     * 外网地址转内网地址
     *
     * @param url
     * @return
     */
    public String internalUrl(String url) {
        return url.replaceFirst(domain, internalDomain);
    }

}
