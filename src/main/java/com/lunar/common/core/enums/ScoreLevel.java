package com.lunar.common.core.enums;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

/**
 * 数据质量等级。左闭右开
 */
public enum ScoreLevel implements BaseEnum {

    /** [0, 7) */
    LEVEL6(6, "不确定性极高，数据品质极差", 7),
    /** [7, 13) */
    LEVEL5(5, "不确定性高，数据品质差", 13),
    /** [13, 19) */
    LEVEL4(4, "不确定性略高，数据品质较差", 19),
    /** [19, 25) */
    LEVEL3(3, "不确定性偏高，数据品质不佳", 25),
    /** [25, 31) */
    LEVEL2(2, "不确定性低，数据品质佳", 31),
    /** [31, 37) */
    LEVEL1(1, "不确定性极低，数据品质极佳", 37),

    ;

    private static Map<Integer, ScoreLevel> CODE_MAP = Maps.newHashMap();

    static {
        for (ScoreLevel p : ScoreLevel.values()) {
            CODE_MAP.put(p.getCode(), p);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;
    private int score;

    ScoreLevel(int code, String name, int score) {
        this.code = code;
        this.name = name;
        this.score = score;
    }

    public static ScoreLevel get(Integer level) {
        return CODE_MAP.get(level);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCode() {
        return code;
    }

    public int getScore() {
        return score;
    }

    /**
     * 判断积分所处等级
     *
     * @param score
     * @return
     */
    public static ScoreLevel getByScore(double score) {
        if (score <= 0) {
            return LEVEL6;
        }

        for (ScoreLevel e : ScoreLevel.values()) {
            if (score < e.getScore()) {
                return e;
            }
        }

        return ScoreLevel.values()[ScoreLevel.values().length - 1];
    }

    public static void main(String[] args) {
        // LEVEL1
        System.out.println(getByScore(0).name());
        // LEVEL1
        System.out.println(getByScore(1).name());
        // LEVEL2
        System.out.println(getByScore(7).name());
        // LEVEL2
        System.out.println(getByScore(10).name());
        // LEVEL3
        System.out.println(getByScore(13).name());
        System.out.println(getByScore(36).name());
        System.out.println(getByScore(37).name());
        System.out.println(getByScore(38).name());
    }

}

