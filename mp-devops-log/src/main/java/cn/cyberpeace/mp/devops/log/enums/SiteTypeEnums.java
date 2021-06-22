package cn.cyberpeace.mp.devops.log.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 站点枚举
 * @author wanggang
 */
public enum SiteTypeEnums {

    //平台
    PLATFORM,

    //中台
    MP,

    //应用
    APPLICATION;

    @JsonCreator
    public static SiteTypeEnums fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
