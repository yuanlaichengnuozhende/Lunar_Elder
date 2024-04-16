package com.lunar.common.security.config;

import com.lunar.common.security.utils.FileUploadUtil;
import com.lunar.system.service.FileService;
import com.lunar.system.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {

    @Autowired
    private FileService fileService;

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Bean
    public FileUploadUtil fileUploadUtil() {
        FileUploadUtil.init(fileService);
        return new FileUploadUtil();
    }

}
