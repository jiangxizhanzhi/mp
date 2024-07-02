package cn.cyberpeace.mp.auth.service;

import java.time.LocalDateTime;

/**
 * @ClassName ExpireRemindDateGetter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface ExpireRemindDateGetter {
    /**
     * get
     *
     * @return
     */
    LocalDateTime get();
}
