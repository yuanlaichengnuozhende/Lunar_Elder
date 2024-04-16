package com.lunar.common.core.utils;

import cn.hutool.core.util.StrUtil;
import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.core.helper.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 多个值组合，redis zset排行榜使用
 *
 * @author szx
 * @date 2022/05/12 16:07
 */
public class ScoreUtil {

    private final static Long SCORE_BASE = 10000000000L;

    /**
     * 拼接分数
     *
     * @param score     最大支持900000
     * @param reduction 最大支持99999999.99
     * @return 10000000000L * score + 100 * reduction
     */
    public static String join(Integer score, Double reduction) {
        return String.valueOf(score * SCORE_BASE + toLong(reduction));
    }

    /**
     * 计算分数
     *
     * @param score
     * @param reduction
     * @return
     */
    public static double score(Integer score, Double reduction) {
        return score * SCORE_BASE + toLong(reduction);
    }

    public static Model split(String str) {
        long val = Long.parseLong(str);

        // 积分上限：900000
        // 减碳量上限：9999999999
        val = Math.min(val, 9000009999999999L);

        int score = (int) (val / SCORE_BASE);
        long reduction = (val % SCORE_BASE);

        return Model.builder().score(score).reduction(reduction / 100.0d).build();
    }

    private static long toLong(Double reduction) {
        return new BigDecimal(String.valueOf(reduction)).multiply(new BigDecimal(100)).longValue();
    }

    public static String getField(long id) {
        return StrUtil.fillBefore(String.valueOf(id), '0', 20);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Model {

        private Integer score;

        private Double reduction;
    }

    public static void main(String[] args) {
        // Long.MAX_VALUE = 9223372036854775807
        //        8633.05
//        8633.05
//        863304
//        double reduction = 8633.05d;
////        System.out.println(String.valueOf(reduction));
//        System.out.println(new BigDecimal(String.valueOf(reduction)).multiply(new BigDecimal(100)).longValue());
//        System.out.println((long) (reduction * 100));
//
//        System.out.println(toLong(reduction));
//
//        System.out.println(toLong(reduction) / 100d);

        int score = 820005;
//        double reduction = 99999999.05d;
        double reduction = 8633.05d;
        String s1 = join(score, reduction);
        System.out.println(s1);

        double s2 = score(score, reduction);
        System.out.println(s2);

//        System.out.println(Long.MAX_VALUE);

        Model split = split(s1);
//        System.out.println(split.getScore());
//        System.out.println(split.getReduction());

        System.out.println(JsonHelper.toJson(split));
//
//        Model model1 = split(s1);
//        System.out.println(model1);
//
//        String s2 = join(1, 0d);
//        System.out.println(s2);
//
//        Model model2 = split(s2);
//        System.out.println(model2);
//
//        Double s = 234234353453453d;
//        System.out.println(new BigDecimal(Double.toString(s)).toPlainString());
//        System.out.println(NumberUtil.toIntStr(s));
    }

//    public static void aa(double a, double b) {
//        BigDecimal changeVal = new BigDecimal(a);
//        double rate = b;
//        // 变动减碳量
//        double changeReduction = changeVal.multiply(BigDecimal.valueOf(rate))
//            .setScale(2, RoundingMode.HALF_UP).doubleValue();
//
//        System.out.println(changeReduction);
//
//        Double reduction = Convert.toDouble(changeReduction, 0d);
//        System.out.println(reduction);
//
//        System.out.println((long) (reduction * 100));
//    }

}
