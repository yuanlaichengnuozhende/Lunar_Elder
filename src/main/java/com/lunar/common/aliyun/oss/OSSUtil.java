package com.lunar.common.aliyun.oss;

import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.lunar.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * oss工具
 *
 * @author szx
 * @date 2022/06/10 10:51
 */
@Slf4j
@Component
public class OSSUtil {

    /** endpoint */
    @Value("${oss.endpoint:}")
    private String endpoint;

    /** accessKeyId */
    @Value("${oss.accessKeyId:}")
    private String accessKeyId;

    /** accessKeySecret */
    @Value("${oss.accessKeySecret:}")
    private String accessKeySecret;

    /** bucketName */
    @Value("${oss.bucketName:}")
    private String bucketName;

    @Value("${oss.hostAddr:}")
    private String hostAddr;

    private static String ENDPOINT;
    private static String ACCESS_KEY_ID;
    private static String ACCESS_KEY_SECRET;
    private static String BUCKET_NAME;
    private static String HOST_ADDR;

    @PostConstruct
    public void init() {
        ENDPOINT = endpoint;
        ACCESS_KEY_ID = accessKeyId;
        ACCESS_KEY_SECRET = accessKeySecret;
        BUCKET_NAME = bucketName;
        HOST_ADDR = hostAddr;
    }

    /**
     * 上传文件
     *
     * @param name
     * @param file
     * @param ossPath
     * @return
     * @throws FileNotFoundException
     */
    public static String uploadFile(String name, File file, OSSPath ossPath) throws FileNotFoundException {
        String path = doUploadFile(name, BUCKET_NAME, new FileInputStream(file), ossPath);
        return getUrl(path);
    }

    /**
     * 上传文件流
     *
     * @param name
     * @param is
     * @param ossPath
     * @return
     */
    public static String uploadFile(String name, InputStream is, OSSPath ossPath) {
        String path = doUploadFile(name, BUCKET_NAME, is, ossPath);
        return getUrl(path);
    }

    /**
     * 获取文件路径
     *
     * @param path       不带最前面的"/" eg: test/2022/06/10/xxx.png
     * @param expiration
     * @return
     */
    public static String getAccessUrl(String path, Date expiration) {
        return getOSSClient().generatePresignedUrl(BUCKET_NAME, path, expiration, HttpMethod.GET).toString();
    }

    private static OSSClient getOSSClient() {
        DefaultCredentialProvider cred = new DefaultCredentialProvider(ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        // 创建OSSClient实例。
        return new OSSClient(ENDPOINT, cred, null);
    }

    public static String ossFilename(String name, OSSPath ossPath) {
        String dateStr = DateUtil.format(new Date(), "yyyy/MM/dd");
        return ossPath.getName() + "/" + dateStr + "/" + name;
    }

    private static String doUploadFile(String name, String bucketName, InputStream is, OSSPath ossPath) {
//        checkNotNull(ossPath, "ossPath can't be null");
        ossPath = (ossPath == null ? OSSPath.TEST : ossPath);
        checkArgument(StringUtils.isNotBlank(name), "file name can't be blank");
        checkNotNull(is, "is does not exist");

        OSSClient ossClient = getOSSClient();
        try {
            String fileName = ossFilename(name, ossPath);
            ossClient.putObject(bucketName, fileName, is);
            return fileName;
        } catch (Exception e) {
            log.error("oss上传文件失败", e);
            throw new ServiceException("oss上传文件失败");
        } finally {
            ossClient.shutdown();
            try {
                is.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private static String getUrl(String path) {
        return HOST_ADDR + path;
    }

}
