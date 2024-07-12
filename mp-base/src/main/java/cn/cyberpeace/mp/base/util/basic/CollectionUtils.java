 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionUtils.java.
 // File size 1372.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @ClassName CollectionUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class CollectionUtils {

    /**
     * CollectionUtils
     */
    private CollectionUtils() {}

    /**
     * emptyNull
     *
     * @param value
     * @return
     */
    public static <T extends Collection<?>> T emptyNull(T value) {
        return (value == null || value.isEmpty()) ? null : value;
    }

    /**
     * batchHandle
     *
     * @param values
     * @param batchSize
     * @param batchHandler
     */
    public static <T> void batchHandle(List<T> values, int batchSize, Consumer<List<T>> batchHandler) {
        if (values.isEmpty() || batchSize <= 0) {
            return;
        }

        int allSize = values.size();
        int startIndex = 0;
        int endIndex = startIndex + batchSize;
        while (startIndex < allSize) {
            if (endIndex > allSize) {
                endIndex = allSize;
            }
            List<T> batchList = values.subList(startIndex, endIndex);
            batchHandler.accept(batchList);
            startIndex = endIndex;
            endIndex = startIndex + batchSize;
        }
    }

}
