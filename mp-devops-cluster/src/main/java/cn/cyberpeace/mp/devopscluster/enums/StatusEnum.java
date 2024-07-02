package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusEnum {

    NO,

    YES;

    /**
     * fromValue
     *
     * @param ordinal
     * @return
     */
    @JsonCreator
    public static StatusEnum fromValue(Integer ordinal) {
        return values()[ordinal];
    }

    /**
     * toValue
     *
     * @return
     */
    @JsonValue
    public Integer toValue() {
        return this.ordinal();
    }
}
