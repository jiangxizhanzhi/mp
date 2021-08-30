package cn.cyberpeace.mp.devops.cluster.enums;

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

    CONTAINER,

    ENTITY,

    SIMPLE;


    ImageTypeEnum() {

    }

    private static Map<String, ImageTypeEnum> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(ImageTypeEnum.values()).forEach(
                t -> map.put(t.name().toLowerCase(Locale.ROOT), t)
        );
    }

    @JsonCreator
    public static ImageTypeEnum fromValue(String desc) {
        return map.get(desc);
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
