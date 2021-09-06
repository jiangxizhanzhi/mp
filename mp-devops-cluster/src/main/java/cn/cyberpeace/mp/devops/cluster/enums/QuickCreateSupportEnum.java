package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuickCreateSupportEnum {

    FALSE,

    TRUE;

    @JsonCreator
    public static QuickCreateSupportEnum fromValue(Integer ordinal) {
        return values()[ordinal];
    }

    @JsonValue
    public Integer toValue() {
        return this.ordinal();
    }
}
