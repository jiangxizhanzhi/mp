package cn.cyberpeace.mp.devops.log.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 站点枚举
 * @author wanggang
 */
public enum SiteTypeEnums {

    //MP中台
    MP;

    @JsonCreator
    public static SiteTypeEnums fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
