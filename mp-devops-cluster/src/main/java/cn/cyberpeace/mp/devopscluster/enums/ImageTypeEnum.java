 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImageTypeEnum.java.
 // File size 1275.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ImageTypeEnum
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
@SwaggerDisplayEnum
public enum ImageTypeEnum {

    VM,

    DYNAMIPS,

    CONTAINER,

    ENTITY,

    SIMPLE;


    /**
     * ImageTypeEnum
     */
    ImageTypeEnum() {

    }

    private static Map<String, ImageTypeEnum> map = new ConcurrentHashMap<>();

    static {
        for (ImageTypeEnum t : ImageTypeEnum.values()) {
            map.put(t.name().toLowerCase(Locale.ROOT), t);
        }
    }

    /**
     * fromValue
     *
     * @param desc
     * @return
     */
    @JsonCreator
    public static ImageTypeEnum fromValue(String desc) {
        return map.get(desc);
    }

    /**
     * toValue
     *
     * @return
     */
    @JsonValue
    public String toValue() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
