package com.zx.mysql.maker.util;

/**
 * @author zx
 * @date 2023/02/27 18:11
 */
public class StrUtil {

    /**
     * 首字母大写
     *
     * @param str
     * @return
     */
    public static String upperFirst(String str) {
        char[] cs = str.toCharArray();

        // a-z
        if (cs[0] >= 97 && cs[0] <= 122) {
            cs[0] -= 32;
            return String.valueOf(cs);
        }

        return str;
    }

    /**
     * 首字母小写
     *
     * @param str
     * @return
     */
    public static String lowerFirst(String str) {
        char[] cs = str.toCharArray();

        // A-Z
        if (cs[0] >= 65 && cs[0] <= 90) {
            cs[0] += 32;
            return String.valueOf(cs);
        }

        return str;
    }

    public static String wrap(String str) {
        String[] split = str.split(",");

        StringBuilder all = new StringBuilder();
        StringBuilder curr = new StringBuilder();

        for (int i = 0; i < split.length; i++) {
            String s = split[i];

            if (i < split.length - 1) {
                all.append(s).append(",");
            } else {
                all.append(s);
                break;
            }

            curr.append(s).append(",");
            String next = split[i + 1];
            // 前面有4个空格
            if (curr.length() + next.length() + 4 > 120) {
                curr = new StringBuilder();

                all.append("\n    ");
            }
        }

        return all.toString();
    }

    public static void main(String[] args) {
//        String s = "id,company_id,org_id,supplier_name,supplier_code,uscc,contact_name,contact_mobile,contact_email,remark,supplier_status,deleted,create_by,update_by,create_time,update_time,contact_name,contact_mobile,contact_email,remark,supplier_status,supplier_code,uscc,contact_name,contact_mobile";
        String s = "id,company_id,org_id,supplier_name,supplier_code,uscc,contact_name,contact_mobile,contact_email,remark,supplier_statusab";
//        String s = "id";
        String wrap = wrap(s);
        System.out.println(wrap);
    }

}
