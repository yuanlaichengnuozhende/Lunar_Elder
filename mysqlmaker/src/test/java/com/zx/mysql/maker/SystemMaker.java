package com.zx.mysql.maker;

import com.zx.mysql.maker.consts.Consts;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 微服务架构使用
 */
@Deprecated
public class SystemMaker {

    /**
     * jdbc:mysql://localhost:3306/tao_mysqlmaker?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&transformedBitIsBoolean=true&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
     */
    private static final String MYSQL_ADDR = "192.168.1.165:3306";
    private static final String MYSQL_SCHEMA = "dct_system";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "PY5WE%!xHUpum9p1";

    private static final String MYBATIS_PACKAGE = "com.carbonstop.common.mybatis";

    public static void main(String[] args) {
        // 基础包名
        String basePackage = "com.carbonstop.system";
        // 业务模块名
        String module = "dct-module-system";
        // entity是否放在biz包
        boolean oneDir = false;

        // 要逆向的表
        List<String> tableList = new ArrayList<>();
        tableList.add("audit_data");

        // 要删除的表前缀
        String tablePrefix = "";
        // 要删除的表后缀
        String tablePostfix = "";

        Maker maker = new Maker(Consts.VM_DIR, MYBATIS_PACKAGE, MYSQL_ADDR, MYSQL_SCHEMA, MYSQL_USER, MYSQL_PASSWORD);

        String userDir = System.getProperty("user.dir") + File.separator + module;
        System.out.println("模块主目录:" + userDir);
        maker.make(oneDir, basePackage, userDir, module, tableList, tablePrefix, tablePostfix);

        // 打印字段名
//        maker.printColumn("n.", tableList);

//        // ######################  单体架构  ######################
//        String userDir = System.getProperty("user.dir");
//        System.out.println("项目主目录:" + userDir);
//        maker.makeSingle(basePackage, userDir, module, tableList, tablePrefix, tablePostfix);
    }

}
