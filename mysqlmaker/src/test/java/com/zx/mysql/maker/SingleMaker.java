package com.zx.mysql.maker;

import com.zx.mysql.maker.consts.Consts;
import java.util.ArrayList;
import java.util.List;

/**
 * 单体模式
 */
public class SingleMaker {

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
        // 模块名
//        String module = "carbonstop-dct-single";
        String module = "";

        // 要逆向的表
        List<String> tableList = new ArrayList<>();
        tableList.add("sys_role");

        // 要删除的表前缀
        String tablePrefix = "sys_";
        // 要删除的表后缀
        String tablePostfix = "";

        Maker maker = new Maker(Consts.VM_DIR, MYBATIS_PACKAGE, MYSQL_ADDR, MYSQL_SCHEMA, MYSQL_USER, MYSQL_PASSWORD);

        String userDir = System.getProperty("user.dir");
        System.out.println("项目主目录:" + userDir);
        maker.makeSingle(basePackage, userDir, module, tableList, tablePrefix, tablePostfix);

        // 打印字段名
//        maker.printColumn("n.", tableList);


    }

}
