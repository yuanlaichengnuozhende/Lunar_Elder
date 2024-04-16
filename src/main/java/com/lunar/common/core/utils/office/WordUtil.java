package com.lunar.common.core.utils.office;

//import com.deepoove.poi.XWPFTemplate;
//import com.deepoove.poi.config.Configure;
//import com.deepoove.poi.config.ConfigureBuilder;
//import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
//import com.deepoove.poi.xwpf.NiceXWPFDocument;

import lombok.extern.slf4j.Slf4j;

/**
 * word工具
 *
 * @author szx
 * @date 2022/02/21 17:22
 */
@Slf4j
public class WordUtil {

//    static {
//        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//        loggerContext.getLogger("org.apache.poi.util.XMLHelper").setLevel(Level.ERROR);
//    }
//
//    /**
//     * 写入doc模板
//     *
//     * @param tpl
//     * @param destPath
//     * @param datas
//     * @throws IOException
//     */
//    public static void writeToFile(InputStream tpl, String destPath, Map<String, Object> datas) throws IOException {
//        ConfigureBuilder builder = Configure.builder().useSpringEL(true);
//        XWPFTemplate template = XWPFTemplate.compile(tpl, builder.build());
//        template.render(datas);
//
//        template.writeToFile(destPath);
//    }
//
//    /**
//     * 写入doc模板，包含行循环
//     *
//     * @param tpl
//     * @param destPath
//     * @param datas
//     * @throws IOException
//     */
//    public static void writeToFileLoopRow(InputStream tpl, String destPath, Map<String, Object> datas, String... tags) throws IOException {
//        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
//
//        ConfigureBuilder builder = Configure.builder().useSpringEL(true);
//        for (String tag : tags) {
//            builder.bind(tag, policy);
//        }
//
//        XWPFTemplate template = XWPFTemplate.compile(tpl, builder.build());
//        template.render(datas);
//
//        template.writeToFile(destPath);
//    }
//
//    /**
//     * 写入doc模板
//     *
//     * @param tpl
//     * @param os
//     * @param datas
//     * @throws IOException
//     */
//    public static void write(InputStream tpl, OutputStream os, Map<String, Object> datas) throws IOException {
//        XWPFTemplate template = XWPFTemplate.compile(tpl);
//        template.render(datas);
//
//        template.write(os);
//    }
//
//    /**
//     * 合并文件
//     *
//     * @param mainPath 主文件
//     * @param subPath  要合并的文件
//     * @param destPath 文件路径
//     */
//    public static void merge(String mainPath, String subPath, String destPath) {
//        try (
//            FileInputStream mainFile = new FileInputStream(mainPath);
//            FileInputStream subFile = new FileInputStream(subPath);
//            FileOutputStream out = new FileOutputStream(destPath);
//        ) {
//            NiceXWPFDocument main = new NiceXWPFDocument(mainFile);
//            NiceXWPFDocument sub = new NiceXWPFDocument(subFile);
//            // 合并两个文档
//            NiceXWPFDocument newDoc = main.merge(sub);
//
//            newDoc.write(out);
//        } catch (Exception e) {
//            log.error("合并文件失败", e);
//        }
//    }
//
//    public static void main(String[] args) {
//        Map<String, Object> datas = Maps.newHashMap();
//        datas.put("projectName", "测试项目");
//
//        try (
//            FileOutputStream os = new FileOutputStream("/Users/szx/Desktop/out.docx")
//        ) {
//            InputStream is = new FileInputStream("/Users/szx/Desktop/a.docx");
//            WordUtil.write(is, os, datas);
//        } catch (Exception e) {
//            log.error("导出模板doc错误", e);
//        }
//    }

}
