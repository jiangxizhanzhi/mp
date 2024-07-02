package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.auth.util.EncryptUtils;
import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivCtlService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @ClassName SysCtlScheduleJob
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/2/14 18:07
 * @Version 1.0.0
 */
@Slf4j
@Service
public class SysCtlScheduleJob {

    @Autowired
    private SysActivCtlService sysActivCtlService;

    private volatile boolean isSystemTimestampRollback = false;

    /**
     * 定时任务调度，刷新系统时间，同时更新系统运行时长 sys_run_seconds
     * 每30分钟执行一次
     * @return
     */
    @MpScheduled(initialDelaySeconds = 60, cron = "0 0/30 * * * ?", description = "定时任务调度，刷新系统时间，同时更新系统运行时长 sys_run_seconds", id = "update_sys_ts")
    public boolean updateSysTs() {

//        if (isSystemTimestampRollback) {
//            // 发生时间回拨，停止变更
//            log.error("time rollback occured!");
//            return false;
//        }

        // update system timestamp
        SysActivCtlModel oldActivCtlModel = getValidCtlModel();
        log.info("updateSysTs, oldActivCtlModel:{}", oldActivCtlModel);
        // 判断系统是否已经注册，未激活不统计运行时长
        if (Objects.nonNull(oldActivCtlModel) && StringUtils.isNotBlank(oldActivCtlModel.getFirstActiveDate())) {
            // not null, to update  example: oldTs 1644778431000
            Long currTs = System.currentTimeMillis();
            Long oldTs = oldActivCtlModel.getSysTimestamp();
            if (Objects.nonNull(oldTs) && oldTs > 0 && currTs >= oldTs) {
                // 更新数据库系统时间戳，同时累加run_seconds
                Long diff = currTs - oldTs;
                Long diffSeconds = diff / 1000;
                String oldRunSecondsStr = oldActivCtlModel.getSysRunSeconds();
                Long oldRunSeconds = 0L;
                if (StringUtils.isNotBlank(oldRunSecondsStr)) {
                    oldRunSeconds = Long.valueOf(EncryptUtils.desDecrypt(oldRunSecondsStr));
                }
                SysActivCtlModel newActivCtlModel = new SysActivCtlModel();
                newActivCtlModel.setId(oldActivCtlModel.getId());
                newActivCtlModel.setSysTimestamp(currTs);

                Long newRunSeconds = diffSeconds + oldRunSeconds;
                log.info("newRunSeconds:{}, diffSeconds:{}, oldRunSeconds:{}", newRunSeconds, diffSeconds, oldRunSeconds);

                newActivCtlModel.setSysRunSeconds(EncryptUtils.desEncrypt(String.valueOf(newRunSeconds)));
                // update sysActivCtlModel
                sysActivCtlService.update(newActivCtlModel);
                log.info("update sysTs success! {}", newActivCtlModel);
                return true;
            } else {
                // 直接更新系统时间戳，不累加run_seconds
                SysActivCtlModel newActivCtlModel = new SysActivCtlModel();
                newActivCtlModel.setId(oldActivCtlModel.getId());
                newActivCtlModel.setSysTimestamp(currTs);
                sysActivCtlService.update(newActivCtlModel);
            }
        }

        return false;
    }

    /**
     * generate a default with current timestamp as system_ts and activ_ts;
     * @return
     */
    public SysActivCtlModel getValidCtlModel() {
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectValid();
        return sysActivCtlModel;
    }

}
