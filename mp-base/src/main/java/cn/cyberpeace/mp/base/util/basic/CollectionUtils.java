package cn.cyberpeace.mp.base.util.basic;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class CollectionUtils {

    private CollectionUtils() {}

    public static <T extends Collection<?>> T emptyNull(T value) {
        return (value == null || value.isEmpty()) ? null : value;
    }

    public static <T> void batchHandle(List<T> values, int batchSize, Consumer<List<T>> batchHandler) {
        if (values.isEmpty() || batchSize <= 0) {
            return;
        }

        var allSize = values.size();
        var startIndex = 0;
        var endIndex = startIndex + batchSize;
        while (startIndex < allSize) {
            if (endIndex > allSize) {
                endIndex = allSize;
            }
            var batchList = values.subList(startIndex, endIndex);
            batchHandler.accept(batchList);
            startIndex = endIndex;
            endIndex = startIndex + batchSize;
        }
    }

}
