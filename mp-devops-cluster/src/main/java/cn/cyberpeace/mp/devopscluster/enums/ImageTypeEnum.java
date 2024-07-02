package cn.cyberpeace.mp.devopscluster.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
