package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.service.UserOnlineReportService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserOnlineReportTask implements Runnable {


    private Map<String, Integer> userHeartbeatCnt;

    private UserOnlineReportService userOnlineReportService;

    public UserOnlineReportTask(Map<String, Integer> userHeartbeatCnt, UserOnlineReportService userOnlineReportService) {
        this.userHeartbeatCnt = userHeartbeatCnt;
        this.userOnlineReportService = userOnlineReportService;
    }

    @Override
    public void run() {

        // 抽取每个user的数据，给每个user生成一条在线报告记录
        userHeartbeatCnt.forEach((uid, cnt) -> {
            log.info("uid:{}, cnt:{}", uid, cnt);

            if(cnt > 6) {
                userOnlineReportService.createOrUpdate(uid, true);
            } else {
                userOnlineReportService.createOrUpdate(uid, false);
            }
        });


    }
}
