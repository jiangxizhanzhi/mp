package cn.cyberpeace.mp.devopscluster.enums;

import java.util.Arrays;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/11/1 15:25
 */
public enum TimeSlotEnums {

    HOUR("hour", 24),
    DAY("day", 30),
    // 暂不支持周，Java跟mysql中对周日属于前一周还是后一周判断不一
    //WEEK("week", 10),
    MONTH("month", 12),
    YEAR("year", 10);

    private String unit;

    private Integer count;

    /**
     * getUnit
     *
     * @return
     */
    public String getUnit() {
        return unit;
    }

    /**
     * getCount
     *
     * @return
     */
    public Integer getCount() {
        return count;
    }

    /**
     * TimeSlotEnums
     *
     * @param unit
     * @param count
     */
    TimeSlotEnums(String unit, Integer count) {
        this.unit = unit;
        this.count = count;
    }

    /**
     * fromUnit
     *
     * @param unit
     * @return
     */
    public static TimeSlotEnums fromUnit(String unit) {
        for (TimeSlotEnums e : values()) {
            if (e.getUnit().equals(unit)) {
                return e;
            }
        }
        return null;
    }
}
