package com.lunar.common.core.utils;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author szx
 * @date 2022/02/22 18:46
 */
public class MathUtil {

    /**
     * 求和
     *
     * @param data
     * @return
     */
    public static double sum(double[] data) {
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum = sum + data[i];
        }

//        double sum = Arrays.stream(data).boxed().mapToDouble(Double::doubleValue).sum();
        return sum;
    }

    /**
     * 求和
     *
     * @param data
     * @return
     */
    public static double sum(List<Double> data) {
        return sum(data.stream().mapToDouble(Double::doubleValue).toArray());
    }

    /**
     * 平均数
     *
     * @param data
     * @return
     */
    public static double avg(double[] data) {
        return sum(data) / data.length;
    }

    /**
     * 平均数
     *
     * @param data
     * @return
     */
    public static double avg(List<Double> data) {
        return avg(data.stream().mapToDouble(Double::doubleValue).toArray());
    }

    /**
     * 样本方差
     *
     * @param data
     * @return
     */
    public static double variance(double[] data) {
        if (data.length <= 1) {
            return 0;
        }

        double avg = avg(data);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow((data[i] - avg), 2);
        }
        variance = variance / (data.length - 1);
        return variance;
    }

    /**
     * 样本方差
     *
     * @param data
     * @return
     */
    public static double variance(List<Double> data) {
        return variance(data.stream().mapToDouble(Double::doubleValue).toArray());
    }

    /**
     * 样本标准差
     *
     * @param data
     * @return
     */
    public static double deviation(double[] data) {
        return Math.sqrt(variance(data));
    }

    /**
     * 样本标准差
     *
     * @param data
     * @return
     */
    public static double deviation(List<Double> data) {
        return deviation(data.stream().mapToDouble(Double::doubleValue).toArray());
    }

    /**
     * 总体方差
     *
     * @param data
     * @return
     */
    public static double popVariance(double[] data) {
        double avg = avg(data);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += (Math.pow((data[i] - avg), 2));
        }
        variance = variance / data.length;
        return variance;
    }

    /**
     * 总体标准差
     *
     * @param data
     * @return
     */
    public static double popDeviation(double[] data) {
        return Math.sqrt(popVariance(data));
    }

    public static void main(String[] args) {
        System.out.println(sum(Lists.newArrayList(1.1d, 2.2d, 3.3d)));
        System.out.println(avg(Lists.newArrayList(1.1d, 2.2d, 3.3d)));
        System.out.println(variance(Lists.newArrayList(1d, 2d, 3d, 4d, 5d)));
        System.out.println(deviation(Lists.newArrayList(99d, 354d, 4523d)));
    }

}
