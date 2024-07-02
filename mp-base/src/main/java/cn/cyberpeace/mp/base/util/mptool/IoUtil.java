package cn.cyberpeace.mp.base.util.mptool;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;

@Slf4j
public class IoUtil {

    /**
     * close
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
                log.error(e.getMessage());
            }
        }
    }
}
