package com.zx.mysql.maker;

import com.zx.mysql.maker.bean.Column;
import com.zx.mysql.maker.bean.Table;
import com.zx.mysql.maker.util.CodeUtil;
import com.zx.mysql.maker.util.DBUtil;
import com.zx.mysql.maker.util.FileUtil;
import com.zx.mysql.maker.util.StrUtil;
import com.zx.mysql.maker.util.VelocityUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zx
 */
public class Maker {

    private static final String ENC = "UTF-8";

//    public Maker() {
//        new Maker("mysqlmaker/src/main/java/com/zx/mysql/maker/resource");
//    }

    private String mybatisPackage;
    private String mysqlAddr;
    private String mysqlSchema;
    private String mysqlUser;
    private String mysqlPassword;

//    public Maker(String vmDir) {
//        try {
//            String userDir = System.getProperty("user.dir");
//            VelocityUtil.initVelocityEngine(userDir + File.separator + vmDir);
//        } catch (Exception e) {
//        }
//    }

    public Maker(String vmDir, String mybatisPackage, String mysqlAddr, String mysqlSchema, String mysqlUser,
                 String mysqlPassword) {
        try {
            this.mysqlAddr = mysqlAddr;
            this.mysqlSchema = mysqlSchema;
            this.mysqlUser = mysqlUser;
            this.mysqlPassword = mysqlPassword;
            this.mybatisPackage = mybatisPackage;

            String userDir = System.getProperty("user.dir");
            VelocityUtil.initVelocityEngine(userDir + File.separator + vmDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印字段名
     *
     * @param prefix    前缀
     * @param tableList
     */
    public void printColumn(String prefix, List<String> tableList) {
        for (String tableName : tableList) {
            DBUtil.buildConn(mysqlAddr, mysqlSchema, mysqlUser, mysqlPassword);

            // 解析表对应的java数据
            Table table = DBUtil.getTable(tableName);

            // id
//            System.out.println(table.getColumnKey().getColumnName());

            String str = table.getColumnList().stream()
                .map(x -> prefix + x.getColumnName()).collect(Collectors.joining(","));
            System.out.println(str);
        }
    }

    /**
     * 生成文件-api和-biz
     *
     * @param oneDir       entity是否放在biz包
     * @param basePackage
     * @param outputRoot
     * @param module
     * @param tableList
     * @param tablePrefix
     * @param tablePostfix
     */
    public void make(boolean oneDir,
                     String basePackage,
                     String outputRoot,
                     String module,
                     List<String> tableList,
                     String tablePrefix,
                     String tablePostfix) {
        // tao-mysqlmaker/dct-module-system/dct-module-system-api/src/main/java
        String apiDir = oneDir ? "-biz" : "-api";
        String outputApiDir = outputRoot + File.separator + module + apiDir + File.separator +
            "src" + File.separator + "main" + File.separator + "java";
        // tao-mysqlmaker/dct-module-system/dct-module-system-biz/src/main/java
        String outputRootDir = outputRoot + File.separator + module + "-biz" + File.separator +
            "src" + File.separator + "main" + File.separator + "java";

        for (String tableName : tableList) {
            DBUtil.buildConn(mysqlAddr, mysqlSchema, mysqlUser, mysqlPassword);

            // 解析表对应的java数据
            Table table = DBUtil.getTable(tableName);

            // 通用参数
            Map<String, Object> data = new HashMap<>();

            data.put("mybatisPackage", mybatisPackage);
            data.put("util", new CodeUtil());
            data.put("table", table);
            data.put("tableName", tableName.substring(0, tableName.length() - tablePostfix.length()));

            String str = table.getColumnList().stream()
                .map(Column::getColumnName).collect(Collectors.joining(","));
            // 所有列
            String baseColumnList = table.getColumnKey().getColumnName() + "," + str;
            String wrap = StrUtil.wrap(baseColumnList);
            data.put("Base_Column_List", wrap);

//                // dto
//                makeJava(data, basePackage, group, tableName, tablePrefix, tablePostfix, "dto", "Dto", outputRootApi,
//                    "dto.vm");

            // entity
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "entity", "",
                outputApiDir, "entity.vm");

            // query
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "query", "Query",
                outputRootDir, "query.vm");

            // service
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "service", "Service",
                outputRootDir, "service.vm");

            // mapper
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "mapper", "Mapper",
                outputRootDir, "mapper.vm");

            // ServiceImpl
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "service.impl", "ServiceImpl",
                outputRootDir, "service.impl.vm");

            // controller
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "controller", "Controller",
                outputRootDir, "controller.vm");

            // resource/mapper路径
            String resourceMapperPath =
                outputRootDir + File.separator + ".." + File.separator + "resources" + File.separator + "mapper";
            // xml
            makeFile(data, "xml-insert-select.vm", resourceMapperPath,
                CodeUtil.convertClassNameToPath((String) data.get("mapperName"), "xml"));
        }
    }

    /**
     * 生成文件在同一个文件夹（单体模式）
     *
     * @param basePackage
     * @param outputRoot
     * @param module
     * @param tableList
     * @param tablePrefix
     * @param tablePostfix
     */
    public void makeSingle(String basePackage,
                           String outputRoot,
                           String module,
                           List<String> tableList,
                           String tablePrefix,
                           String tablePostfix) {
        // tao-mysqlmaker/tao-single/src/main/java
        String outputRootDir = outputRoot + File.separator + module + File.separator +
            "src" + File.separator + "main" + File.separator + "java";

        for (String tableName : tableList) {
            DBUtil.buildConn(mysqlAddr, mysqlSchema, mysqlUser, mysqlPassword);
            Table table = DBUtil.getTable(tableName);

            Map<String, Object> data = new HashMap<>();
            data.put("mybatisPackage", mybatisPackage);
            data.put("util", new CodeUtil());
            data.put("table", table);
            data.put("tableName", tableName.substring(0, tableName.length() - tablePostfix.length()));

            String str = table.getColumnList().stream()
                .map(Column::getColumnName).collect(Collectors.joining(","));
            // 所有列
            String baseColumnList = table.getColumnKey().getColumnName() + "," + str;
            String wrap = StrUtil.wrap(baseColumnList);
            data.put("Base_Column_List", wrap);

//                // dto
//                makeJava(data, basePackage, group, tableName, tablePrefix, tablePostfix, "dto", "Dto", outputRootApi,
//                    "dto.vm");

            // entity
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "entity", "",
                outputRootDir, "entity.vm");

            // query
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "query", "Query",
                outputRootDir, "query.vm");

            // service
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "service", "Service",
                outputRootDir, "service.vm");

            // mapper
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "mapper", "Mapper",
                outputRootDir, "mapper.vm");

            // ServiceImpl
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "service.impl", "ServiceImpl",
                outputRootDir, "service.impl.vm");

            // controller
            makeJava(data, basePackage, tableName, tablePrefix, tablePostfix, "controller", "Controller",
                outputRootDir, "controller.vm");

            // resource/mapper路径
            String resourceMapperPath =
                outputRootDir + File.separator + ".." + File.separator + "resources" + File.separator + "mapper";
            // xml
            makeFile(data, "xml-insert-select.vm", resourceMapperPath,
                CodeUtil.convertClassNameToPath((String) data.get("mapperName"), "xml"));
        }
    }

    protected void makeJava(Map<String, Object> data,
                            String basePackage,
                            String tableName,
                            String tablePrefix,
                            String tablePostfix,
                            String common,
                            String variableSuffix,
                            String outputRoot,
                            String template) {
        String classPackage = CodeUtil.getPackage(basePackage, common);
        String variable = CodeUtil.getVariable(
            tableName.substring(tablePrefix.length(), tableName.length() - tablePostfix.length())) + variableSuffix;
        String name = CodeUtil.getUpperCaseVariable(variable);
        String className = CodeUtil.getClassName(classPackage, name);
        common = CodeUtil.standard(common);
        data.put(common + "Package", classPackage);
        data.put(common + "Variable", variable);
        //        name = getName(common, name);
        data.put(common + "Name", name);
        //        className = getName(common, className);
        data.put(common + "ClassName", className);

//        Table table = (Table) data.get("table");
//        List<Column> columnList = table.getColumnList();
//        boolean hasList = columnList.stream().anyMatch(x -> x.getPropertyDataType().contains("List"));
//        data.put("hasList", hasList);

        makeFile(data, template, outputRoot, CodeUtil.convertClassNameToPath(className, "java"));
    }

    //    private String getName(String common, String name) {
    //        if (common.equals("service")) {
    //            name = name.substring(0, name.lastIndexOf("Service")) + "CrudService";
    //        } else if (common.equals("serviceImpl")) {
    //            name = name.substring(0, name.lastIndexOf("ServiceImpl")) + "CrudServiceImpl";
    //        }
    //        return name;
    //    }

    protected void makeFile(Map<String, Object> data, String resource, String outputRoot, String path) {
        BufferedWriter bw = null;
        try {
            File file = FileUtil.getFile(outputRoot, path);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), ENC));
            VelocityUtil.merge(data, resource, bw, ENC);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
