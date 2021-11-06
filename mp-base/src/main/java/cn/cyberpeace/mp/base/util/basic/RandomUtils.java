package cn.cyberpeace.mp.base.util.basic;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    private static final Random RANDOM = new Random();

    private RandomUtils() {}

    public static <T> List<T> sample(List<T> pool, int count) {
        int poolSize = pool.size();
        if (poolSize < count) {
            throw new MpRuntimeException("No enough resource for random pick");
        }

        List<T> tmpPool = new ArrayList<>(pool);
        if (poolSize == count) {
            return tmpPool;
        }

        List<T> sample = new ArrayList<>();
        for (var i = 0; i < count; i++) {
            var nextIndex = RANDOM.nextInt(tmpPool.size());
            sample.add(tmpPool.remove(nextIndex));
        }

        return sample;
    }

}
