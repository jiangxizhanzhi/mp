package cn.cyberpeace.mp.base.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class CheckUtils {

    private static final int DEFAULT_LOOP_CHECK_TIMEOUT = 300 * 1000;

    
    public static void loopCheck(Callable<Boolean> check, int stepTime, int timeout,
                                 Callable<Boolean> stopCheck, int stopCheckIntervalCount,
                                 Runnable callback, Runnable timeoutCallback, String targetName) {
        stepTime = stepTime <= 0 ? 2000 : stepTime;
        timeout = timeout <= 0 ? DEFAULT_LOOP_CHECK_TIMEOUT : timeout;
        stopCheckIntervalCount = stopCheckIntervalCount <= 0 ? 1 : stopCheckIntervalCount;

        var stopCheckCount = 0;
        var allTime = 0;
        while (true) {
            var enterMoment = System.currentTimeMillis();
            log.info("Loop check {} - {}s", targetName, allTime / 1000);

            var checkFlag = false;
            try {
                checkFlag = check.call();
            } catch (Exception e) {
                log.error("Loop check call check error: ", e);
            }
            
            if (Boolean.TRUE.equals(checkFlag)) {
                log.info("Loop check {} ok", targetName);
                if (callback != null) {
                    callback.run();
                }
                break;
            }

            
            var checkConsumeTime = System.currentTimeMillis() - enterMoment;
            var remainStepTime = stepTime - checkConsumeTime;
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

            
            if (allTime >= timeout) {
                log.info("Loop check {} timout", targetName);
                if (timeoutCallback != null) {
                    timeoutCallback.run();
                }
                break;
            } else if (stopCheck != null) {
                
                stopCheckCount += 1;
                if (stopCheckCount == stopCheckIntervalCount) {
                    var stopCheckFlag = false;
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
    }

}
