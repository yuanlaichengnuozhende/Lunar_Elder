package com.zx.mysql.maker.util;

import com.zx.mysql.maker.bean.Column;
import com.zx.mysql.maker.bean.Table;
import com.zx.mysql.maker.consts.Consts;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.*;

/**
 * @author zx
 */
public class DBUtil {

    public static Properties config;
    public static Connection conn;
    public static String schema;

    static {
        try {
            config = new Properties();
            String userDir = System.getProperty("user.dir");
            String path =
                userDir + File.separator + "mysqlmaker" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "config.properties";
            FileInputStream is = null;
            try {
                is = new FileInputStream(path);
                config.load(is);
            } catch (FileNotFoundException e) {
                FileOutputStream fos = new FileOutputStream(path);
                config.store(fos, null);
                if (fos != null) {
                    fos.close();
                }
                System.exit(1);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void buildConn(String mysqlAddr, String mysqlSchema, String mysqlUser, String mysqlPassword) {
        try {
            schema = mysqlSchema;

            String mysqlUrl = String.format(Consts.MYSQL_URL, mysqlAddr, mysqlSchema);

            Class.forName(config.getProperty("mysql.className"));
            conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword);
            if (conn.isClosed()) {
                System.out.println("closed connection");
            } else {
                System.out.println("open connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 解析表对应的java数据
     *
     * @param tableName
     * @return
     */
    public static Table getTable(String tableName) {
        Table table = null;
        String sql = "SELECT COLUMN_NAME,DATA_TYPE,COLUMN_KEY,COLUMN_COMMENT,COLUMN_TYPE \n" + "FROM " +
            "information_schema.COLUMNS \n" + "WHERE TABLE_SCHEMA=? \n" + "AND TABLE_NAME=? \n" + "ORDER BY " +
            "ordinal_position";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            //            ps.setString(1, config.getProperty("mysql.schema"));
            ps.setString(1, schema);
            ps.setString(2, tableName);
            ResultSet rs = ps.executeQuery();

            // 数据类型import
            Set<String> dataTypeSet = new HashSet<>();
            Column columnKey = null;
            List<Column> columnList = new ArrayList<>();

            // ## 额外引入到entity和query的包
            // t_test.ext.import=com.carbonstop.common.core.model.FilePojo,com.szx.base.common.model.WxUserData
            String extImport = config.getProperty(tableName + "." + "ext.import");
            if (StringUtils.isNotBlank(extImport)) {
                //                String[] split = extImport.split("\\|");
                String[] split = extImport.split(",");
                dataTypeSet.addAll(Arrays.asList(split));
            }

            // ## like字段
            // t_test.ext.like=source_code,source_name,facility
            Set<String> likeSet = new HashSet<>();
            String extLike = DBUtil.config.getProperty(tableName + "." + "ext.like");
            if (StringUtils.isNotBlank(extLike)) {
                //                String[] split = extLike.split("\\|");
                String[] split = extLike.split(",");
                likeSet.addAll(Arrays.asList(split));
            }

            // ## collection字段
            // t_test.ext.collection=org_id,source_code,source_name
            Set<String> collectionSet = new HashSet<>();
            String collectionLike = DBUtil.config.getProperty(tableName + "." + "ext.collection");
            if (StringUtils.isNotBlank(collectionLike)) {
                //                String[] split = extLike.split("\\|");
                String[] split = collectionLike.split(",");
                collectionSet.addAll(Arrays.asList(split));
            }

            while (rs.next()) {
                ResultSetMetaData data = rs.getMetaData();

                String columnName = rs.getString("COLUMN_NAME");
                String columnDataType = rs.getString("DATA_TYPE");
                String columnComment = rs.getString("COLUMN_COMMENT");
                String columnType = rs.getString("COLUMN_TYPE");
                String propertyName = getVariable(columnName);

                String typeHandler = null;
                String propertyDataType = config.getProperty(tableName + "." + columnName);

                if (propertyDataType == null) {
                    if (StringUtils.equals(columnType, "tinyint(1)")) {
                        // tinyint(1)处理为Boolean
                        propertyDataType = config.getProperty(columnType);
                    } else {
                        propertyDataType = config.getProperty(columnDataType);
                    }
                    if (propertyDataType == null) {
                        throw new RuntimeException("columnDataType:" + columnDataType + "在properties文件里找不到");
                    }
                } else {
                    String[] split = propertyDataType.split("\\|");
                    propertyDataType = split[0];
                    if (split.length > 1) {
                        typeHandler = split[1];
                    }
                }

                propertyDataType = propertyDataType.trim();

                // 是否需要截取
                int index = propertyDataType.lastIndexOf(".");
                if (index != -1) {
                    dataTypeSet.add(propertyDataType);
                    propertyDataType = propertyDataType.substring(index + 1);
                }

                if (propertyDataType.contains("List")) {
                    dataTypeSet.add("java.util.List");
                }

                Column column = new Column(columnName, columnDataType, columnComment, propertyName, propertyDataType,
                                           typeHandler);

                if (likeSet.contains(columnName)) {
                    //                    column.setLike(true);
                    //                    column.setFirstUpperPropertyName(StrUtil.upperFirst(propertyName));
                    column.setLikePropertyName("like" + StrUtil.upperFirst(propertyName));
                } else {
                    column.setLikePropertyName("");
                }

                if (collectionSet.contains(columnName)) {
                    column.setCollectionPropertyName(propertyName + "List");
                } else {
                    column.setCollectionPropertyName("");
                }

                if (columnKey == null && "PRI".equalsIgnoreCase(rs.getString("COLUMN_KEY"))
                    /*&& "auto_increment".equalsIgnoreCase(rs.getString("EXTRA"))*/) {
                    columnKey = column;
                } else {
                    columnList.add(column);
                }
            }
            /*if (columnKey == null) {
                throw new SQLException("columnKey is null");
            }*/
            if (columnList.isEmpty()) {
                throw new SQLException("columnList is empty");
            }
            if (dataTypeSet.isEmpty()) {
                dataTypeSet = null;
            }
            table = new Table(columnKey, columnList, dataTypeSet);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return table;
    }

    public static String getVariable(String columnName) {
        StringBuilder sb = new StringBuilder(columnName.toLowerCase());
        int fromIndex = 0;
        int index = -1;
        while ((index = sb.indexOf("_", fromIndex)) != -1) {
            sb.replace(index, index + 2, sb.substring(index + 1, index + 2).toUpperCase());
            fromIndex = index + 1;
        }
        return sb.toString();
    }

}
