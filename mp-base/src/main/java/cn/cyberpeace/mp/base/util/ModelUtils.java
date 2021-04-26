package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.model.stereotype.Table;

public class ModelUtils {
    public static <Model> String getTableName(Class<Model> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        return table != null ? table.value() : null;
    }
}
