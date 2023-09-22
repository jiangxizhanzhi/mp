package cn.cyberpeace.mp.situationaware.constant;

import java.util.Objects;

/**
 * @ClassName BaseIdAndNameInterface
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/21 14:38
 **/
public interface BaseKeyAndName {
    Integer getKey();

    String getName();

    static <T extends BaseKeyAndName> T getByKey(Integer key, Class<T> enumClazz) {
        T[] enums = enumClazz.getEnumConstants();
        if (Objects.nonNull(enums)) {
            for (T anEnum : enums) {
                if (anEnum.getKey().equals(key)) {
                    return anEnum;
                }
            }
        }
        return null;
    }

    static <T extends BaseKeyAndName> T getByName(String name, Class<T> enumClazz) {
        T[] enums = enumClazz.getEnumConstants();
        if (Objects.nonNull(enums)) {
            for (T anEnum : enums) {
                if (anEnum.getName().equals(name)) {
                    return anEnum;
                }
            }
        }
        return null;
    }

}
