package com.lunar.common.security.utils;

import com.lunar.system.service.FileService;
import com.lunar.system.service.FileService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author szx
 * @date 2023/04/13 10:31:37
 */
@Slf4j
public class FileUploadUtil {

    private static FileService fileService;

    /**
     * 初始化
     *
     * @param fileFeignService
     */
    public static void init(FileService fileFeignService) {
        FileUploadUtil.fileService = fileFeignService;
        log.info("初始化FileUtil成功");
    }

    public static String internalUrl(String url) {
        return fileService.internalUrl(url);
    }

}
