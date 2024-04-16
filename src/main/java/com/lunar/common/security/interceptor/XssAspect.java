package com.lunar.common.security.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * 防止xss入侵，在方法调用之前，对String类型的参数替换掉带script的各种标签
 *
 * @date 2019/07/25 17:55
 */
@Aspect
@Configuration
public class XssAspect {

    private static final Logger logger = LoggerFactory.getLogger(XssAspect.class);

    private static final Pattern PATT_SCRIPT_1 =
        Pattern.compile("<[\r\n| | ]*script[\r\n| | ]*>(.*?)</[\r\n| | ]*script[\r\n| | " + "]*>",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern PATT_SCR = Pattern.compile("src[\r\n| | ]*=[\r\n| | ]*[\\\"|\\\'](.*?)[\\\"|\\\']",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern PATT_SCRIPT_2 = Pattern.compile("</[\r\n| | ]*script[\r\n| | ]*>",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern PATT_SCRIPT_3 = Pattern.compile("<[\r\n| | ]*script(.*?)>",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern PATT_EVAL = Pattern.compile("eval\\((.*?)\\)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern PATT_EXPRESSION = Pattern.compile("e-xpression\\((.*?)\\)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern PATT_JAVASCRIPT = Pattern.compile("javascript[\r\n| | ]*:[\r\n| | ]*",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern PATT_VBSCRIPT = Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern PATT_ONLOAD = Pattern.compile("onload(.*?)=",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    @Pointcut("execution(* com.carbonstop..*.controller.*.*(..))")
    public void executeService() {
    }

    @Around("executeService()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String) {
                    args[i] = xssEncode((String) args[i]);
                } else if (args[i] instanceof HttpServletRequest || args[i] instanceof HttpServletResponse) {
                    continue;
                } else if (args[i] instanceof Object) {
                    //得到类对象
                    Class userCla = (Class) args[i].getClass();
                    Field[] fs = userCla.getDeclaredFields();
                    for (Field f : fs) {
                        try {
                            f.setAccessible(true); //设置些属性是可以访问的
                            Object val = f.get(args[i]);//得到此属性的值
                            if (val instanceof String) {
                                f.set(args[i], xssEncode((String) val));//给属性设值
                            }
                        } catch (Exception e) {
                            logger.warn("xss过滤，有不合法属性" + e.getMessage());
                        }
                    }
                }
            }
        }
        Object result = point.proceed(args);
        return result;
    }

    /**
     * 将容易引起xss & sql漏洞的半角字符直接替换成全角字符
     */
    private static String xssEncode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        } else {
            s = stripXSSAndSql(s);
            return s;
        }
        //StringBuilder sb = new StringBuilder(s.length() + 16);
        //for (int i = 0; i < s.length(); i++) {
        //    char c = s.charAt(i);
        //    switch (c) {
        //        case '>':
        //            sb.append("＞");// 转义大于号
        //            break;
        //        case '<':
        //            sb.append("＜");// 转义小于号
        //            break;
        //        case '\'':
        //            sb.append("＇");// 转义单引号
        //            break;
        //        case '\"':
        //            sb.append("＂");// 转义双引号
        //            break;
        //        case '&':
        //            sb.append("＆");// 转义&
        //            break;
        //        case '#':
        //            sb.append("＃");// 转义#
        //            break;
        //        default:
        //            sb.append(c);
        //            break;
        //    }
        //}
        //return sb.toString();
    }

    /**
     * 防止xss跨脚本攻击（替换，根据实际情况调整）
     */
    public static String stripXSSAndSql(String value) {
        if (value != null) {
            value = PATT_SCRIPT_1.matcher(value).replaceAll("");
            value = PATT_SCR.matcher(value).replaceAll("");
            value = PATT_SCRIPT_2.matcher(value).replaceAll("");
            value = PATT_SCRIPT_3.matcher(value).replaceAll("");
            value = PATT_EVAL.matcher(value).replaceAll("");
            value = PATT_EXPRESSION.matcher(value).replaceAll("");
            value = PATT_JAVASCRIPT.matcher(value).replaceAll("");
            value = PATT_VBSCRIPT.matcher(value).replaceAll("");
            value = PATT_ONLOAD.matcher(value).replaceAll("");
        }
        return value;
    }
}
