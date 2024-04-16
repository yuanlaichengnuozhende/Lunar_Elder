package com.lunar.system.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.model.FileUpload;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.system.service.FileService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.model.FileUpload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/fileapi")
@Api(tags = "文件")
public class FileController {

    private final FileService fileService;

    /**
     * 后缀名白名单
     */
    private final static List<String> SUFFIX_WHITELIST =
        Arrays.asList("jpg", "png", "jpeg", "gif", "bmp", "ico", "doc", "docx", "xls", "xlsx", "pdf");

    /**
     * MIME黑名单
     */
    private final static List<String> MIME_BALCKLIST =
        Arrays.asList("text/html",
                      "text/javascript",
                      "application/javascript",
                      "application/ecmascript",
                      "text/xml",
                      "application/xml");

    /**
     * 路径黑名单
     */
    private final static List<String> PATH_BALCKLIST =
        Arrays.asList("..", "/");

    /**
     * 域名或本机访问地址
     */
    @Value("${file.domain}")
    public String domain;

    /**
     * 内网地址
     */
    @Value("${file.internalDomain}")
    public String internalDomain;

    /**
     * 上传文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;

    /**
     * 上传文件-指定文件名
     *
     * @param file
     * @param fileName 文件名。无值则使用上传文件自身文件名
     * @return
     */
    @ApiIgnore
    @InnerAuth
    @PostMapping("/inner/upload")
    public ApiResult<FileUpload> innerUpload(@RequestPart("file") MultipartFile file, String fileName) {
        FileUpload fileUpload = uploadFile(file, fileName, false);
        return ApiResult.ok(fileUpload);
    }

    /**
     * 外网地址转内网地址
     *
     * @param url
     * @return
     */
    @ApiIgnore
    @InnerAuth
    @GetMapping("/inner/internalUrl")
    public ApiResult<String> internalUrl(@RequestParam("url") String url) {
        String internalUrl = fileService.internalUrl(url);
        return ApiResult.ok(internalUrl);
    }

    @ApiOperation("上传文件")
    @ApiImplicitParams({
//        @ApiImplicitParam(name = "file", value = "file", required = true, dataType = "file", paramType = "query"),
        @ApiImplicitParam(name = "fileName", value = "文件名。无值则使用上传文件自身文件名", required = false, dataType = "数据类型", paramType = "query")
    })
    @PostMapping("/upload")
    public ApiResult<FileUpload> upload(@RequestPart("file") MultipartFile file, String fileName) {
        FileUpload fileUpload = uploadFile(file, fileName, false);
        fileUpload.setFilePath(null);
        return ApiResult.ok(fileUpload);
    }

    @ApiOperation("上传文件-随机文件名")
    @PostMapping("/upload/randomName")
    public ApiResult<FileUpload> uploadRandomName(@RequestPart("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileName = RandomUtil.randomString(16) + "." + FileUtil.extName(originalFilename);

        FileUpload fileUpload = uploadFile(file, fileName, false);
        fileUpload.setFilePath(null);
        return ApiResult.ok(fileUpload);
    }

    @ApiOperation("上传文件-path增加今日时间")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "fileName", value = "文件名。无值则使用上传文件自身文件名", required = false, dataType = "数据类型", paramType = "query")
    })
    @PostMapping(value = "/upload/today")
    public ApiResult<FileUpload> uploadToday(@RequestPart("file") MultipartFile file, String fileName) {
        FileUpload fileUpload = uploadFile(file, fileName, true);
        fileUpload.setFilePath(null);
        return ApiResult.ok(fileUpload);
    }

    /**
     * 上传文件
     *
     * @param file
     * @param fileName
     * @param createTodayDir 添加今日目录
     * @return
     */
    private FileUpload uploadFile(MultipartFile file, String fileName, boolean createTodayDir) {
        if (!checkMIME(file)) {
            throw new ServiceException(ApiCode.FILE_CHECK_FAILED);
        }

        String realFileName = getAndCheckFileName(file, fileName);
        log.info("上传文件[{}]", realFileName);

        try {
//            filePath = UUID.fastUUID().toString(true) + File.separator + originalFilename;
            String filePath = realFileName;
            if (createTodayDir) {
                filePath = DateUtil.today() + File.separator + realFileName;
            }

            String fullPath = localFilePath + filePath;
            FileUtil.touch(fullPath);

            File localFile = new File(fullPath);

            return saveFile(file, localFile, filePath, realFileName);
        } catch (Exception e) {
            log.error("uploadFile error", e);
            throw new ServiceException(ApiCode.FILE_UPLOAD_FAIL);
        }
    }

    /**
     * 保存文件
     *
     * @param file
     * @param localFile
     * @return
     */
    private FileUpload saveFile(MultipartFile file, File localFile, String filePath, String fileName) {
        try {
            InputStream in = file.getInputStream();
            FileUtils.copyInputStreamToFile(in, localFile);

            // http://localhost/dct/2022-02-02/a.jpg
            String url = domain + filePath;
            String internalUrl = internalDomain + filePath;

            return FileUpload.builder()
                .fileName(fileName)
                .filePath(localFile.getPath())
                .url(url)
                .internalUrl(internalUrl)
                .build();
        } catch (Exception e) {
            log.error("saveFile error", e);
            throw new ServiceException(ApiCode.FILE_UPLOAD_FAIL);
        }
//        try (FileOutputStream fos = new FileOutputStream(localFile)) {
//            byte[] bytes = file.getBytes();
//            fos.write(bytes);
//
//            // http://localhost/dct/2022-02-02/a.jpg
//            String url = domain + filePath;
//            String internalUrl = internalDomain + filePath;
//
//            return FileUpload.builder()
//                .fileName(fileName)
//                .filePath(localFile.getPath())
//                .url(url)
//                .internalUrl(internalUrl)
//                .build();
//        } catch (Exception e) {
//            log.error("saveFile error", e);
//            throw new ServiceException(ApiCode.FILE_UPLOAD_FAIL);
//        }
    }

    /**
     * 获取并校验文件名
     *
     * @param file
     * @param fileName
     * @return
     */
    private String getAndCheckFileName(MultipartFile file, String fileName) {
        String originalFilename = file.getOriginalFilename();
        log.info("originalFilename={}, fileName={}", originalFilename, fileName);

        // 文件名
        if (StringUtils.isBlank(fileName)) {
            fileName = file.getOriginalFilename();
        }

        if (!checkFileName(fileName)) {
            throw new ServiceException(ApiCode.FILE_CHECK_FAILED);
        }

        return fileName;
    }

    /**
     * 文件MIME校验
     *
     * @param file
     * @return
     */
    private boolean checkMIME(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            log.info("文件contentType={}", contentType);

            // MIME过滤
            for (String mime : MIME_BALCKLIST) {
                if (contentType.toLowerCase().contains(mime)) {
                    log.error("mime校验失败");
                    return false;
                }
            }

            // 文件头检测 读取文件前50个字节，判断文件类型
//            常见文件头：
//
//            文件类型	文件头
//            JPEG (jpg)	FFD8FF
//            PNG (png)	89504E47
//            GIF (gif)	47494638
//            ZIP Archive (zip)	504B0304
//            RAR Archive (rar)	52617221

            // ImageIO判断上传的是否图片文件

            return true;
        } catch (Exception e) {
            log.error("文件校验MIME失败", e);
            return false;
        }
    }

    /**
     * 文件名校验
     *
     * @param fileName
     * @return
     */
    private static boolean checkFileName(String fileName) {
        log.info("fileName:{}", fileName);
        if (StringUtils.isBlank(fileName)) {
            return true;
        }

        // 后缀名白名单
        String extName = FileUtil.extName(fileName).toLowerCase();
        if (!SUFFIX_WHITELIST.contains(extName)) {
            log.error("文件后缀名校验失败");
            return false;
        }

        // 路径穿越过滤
        for (String path : PATH_BALCKLIST) {
            if (fileName.contains(path)) {
                log.error("文件路径校验失败");
                return false;
            }
        }

        return true;
    }

}