package cn.cyberpeace.mp.base.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @ClassName CheckUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class CheckUtils {

    private static final int DEFAULT_LOOP_CHECK_TIMEOUT = 300 * 1000;

    /**
     * bool结果轮训耗时检查
     * @param check 检查方法
     * @param stepTime 检查间隔时间 默认 2
     * @param timeout 超时时间
     * @param stopCheck 停止检查方法
     * @param stopCheckIntervalCount 停止检查间隔次数：每隔一定次数进行一次停止检查
     */
    public static boolean loopCheck(Callable<Boolean> check, int stepTime, int timeout,
                                    Callable<Boolean> stopCheck, int stopCheckIntervalCount, String targetName) {
        stepTime = stepTime <= 0 ? 2000 : stepTime;
        timeout = timeout <= 0 ? DEFAULT_LOOP_CHECK_TIMEOUT : timeout;
        stopCheckIntervalCount = stopCheckIntervalCount <= 0 ? 1 : stopCheckIntervalCount;

        int stopCheckCount = 0;
        int allTime = 0;
        while (true) {
            long enterMoment = System.currentTimeMillis();
            log.info("Loop check {} - {}s", targetName, allTime / 1000);

            boolean checkFlag = false;
            try {
                checkFlag = check.call();
            } catch (Exception e) {
                log.error("Loop check call check error: ", e);
            }
            // 探测成功
            if (Boolean.TRUE.equals(checkFlag)) {
                log.info("Loop check {} ok", targetName);
                return true;
            }

            // 限制探测频率
            long checkConsumeTime = System.currentTimeMillis() - enterMoment;
            long remainStepTime = stepTime - checkConsumeTime;
            if (remainStepTime > 0) {
                try {
                    Thread.sleep(remainStepTime);
                } catch (InterruptedException e) {
                    log.error("Loop check sleep wait interrupted: ", e);
                }
                allTime += stepTime;
            } else {
                allTime += checkConsumeTime;
            }

            // 超时不再检查
            if (allTime >= timeout) {
                log.info("Loop check {} timout", targetName);
                break;
            } else if (stopCheck != null) {
                // 每隔一定次数进行停止检查
                stopCheckCount += 1;
                if (stopCheckCount == stopCheckIntervalCount) {
                    boolean stopCheckFlag = false;
                    try {
                        stopCheckFlag = stopCheck.call();
                    } catch (Exception e) {
                        log.error("Loop check call stop check error: ", e);
                    }

                    if (Boolean.TRUE.equals(stopCheckFlag)) {
                        break;
                    }
                    stopCheckCount = 0;
                }
            }
        }
        return false;
    }

}
