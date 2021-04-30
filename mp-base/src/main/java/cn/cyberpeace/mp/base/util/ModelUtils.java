package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.model.stereotype.Table;

public class ModelUtils {
    private ModelUtils() {}

    public static <M> String getTableName(Class<M> clazz) {
        var table = clazz.getAnnotation(Table.class);
        return table != null ? table.value() : null;
    }
}
