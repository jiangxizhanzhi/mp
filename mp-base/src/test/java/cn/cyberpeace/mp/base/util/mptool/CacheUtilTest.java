package cn.cyberpeace.mp.base.util.mptool;

import com.google.common.cache.Cache;
import org.junit.jupiter.api.Test;

class CacheUtilTest {

    @Test
    void newTimedCache() {
        Cache<String, String> stringStringCache = CacheUtil.newTimedCache(1000);
        stringStringCache.put("key", "1");
            String key = "";
            try {
                System.out.println(stringStringCache.getIfPresent("key"));
                Thread.sleep(5000);
                System.out.println(stringStringCache.getIfPresent("key"));
            } catch (Exception e) {
            }
    }

}