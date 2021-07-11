package cn.cyberpeace.mp.devopscluster.enums;

import java.util.Arrays;


public enum TimeSlotEnums {

    HOUR("hour", 24),
    DAY("day", 30),
    
    
    MONTH("month", 12),
    YEAR("year", 10);

    private String unit;

    private Integer count;

    public String getUnit() {
        return unit;
    }

    public Integer getCount() {
        return count;
    }

    TimeSlotEnums(String unit, Integer count) {
        this.unit = unit;
        this.count = count;
    }

    public static TimeSlotEnums fromUnit(String unit) {
        return Arrays.stream(values()).filter(e -> e.getUnit().equals(unit)).findFirst().orElse(null);
    }
}
