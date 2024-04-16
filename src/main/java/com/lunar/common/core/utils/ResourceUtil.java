package com.lunar.common.core.utils;

import com.lunar.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author szx
 * @date 2020/08/21 17:16
 */
@Slf4j
public class ResourceUtil {

    /**
     * 获取resource文件夹下的文件
     *
     * @return
     */
    public static InputStream getFileInputStream(String fileName) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(fileName);
            InputStream is = classPathResource.getInputStream();
            return is;
        } catch (IOException e) {
            log.error("读取文件失败", e);
        }
        return null;
    }

    /**
     * 获取resource文件夹下的文件的路径
     *
     * @return
     */
    public static URL getFilePath(String fileName) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(fileName);
            URL urlFile = classPathResource.getURL();
            return urlFile;
        } catch (IOException e) {
            log.error("读取文件失败", e);
        }
        return null;
    }

    /**
     * 从资源文件读取图片
     *
     * @param fileName
     * @return
     */
    public static BufferedImage getBufferedImage(String fileName) {
        InputStream baseIs = getFileInputStream(fileName);
        if (baseIs == null) {
            throw new ServiceException("读取资源文件错误");
        }

        try {
            return ImageIO.read(baseIs);
        } catch (IOException e) {
            log.error("读取图片失败", e);
        }
        return null;
    }

    public static void main(String[] args) {
        InputStream is = ResourceUtil.getFileInputStream("template/a.docx");
        System.out.println(is);
    }
}
