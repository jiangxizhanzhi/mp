package cn.cyberpeace.mp.front.admin.service.sys;

import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.front.admin.model.SysActivCtlModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/14 18:07
 */
@Slf4j
@Service
public class SysCtlService {

    @Autowired
    private SysActivCtlService sysActivCtlService;

    // 系统时间回拨默认阈值为12小时 12 * 60 * 60 * 1000;
    @Value("${mp.admin.max-millisecond-rollback:43200000}")
    private Long maxDiffMilliSecond;

    private volatile boolean isSystemTimestampRollback = false;

    /**
     * 定时任务调度，刷新系统时间，防止服务器时间回拨，阈值
     * 每20分钟执行一次
     * @return
     */
    @Scheduled(initialDelay = 10 * 1000, fixedRate = 20 * 60 * 1000)
    public boolean updateSysTs() {

        if (isSystemTimestampRollback) {
            // 发生时间回拨，停止变更
            log.error("time rollback occured!");
            return false;
        }

        // update system timestamp
        SysActivCtlModel oldActivCtlModel = sysActivCtlService.selectFirst();
        if (Objects.nonNull(oldActivCtlModel)) {
            // not null, to update  example: oldTs 1644778431000
            Long currTs = System.currentTimeMillis();
            Long oldTs = oldActivCtlModel.getSysTimestamp();
            if (currTs >= oldTs) {
                SysActivCtlModel newActivCtlModel = new SysActivCtlModel();
                newActivCtlModel.setId(oldActivCtlModel.getId());
                newActivCtlModel.setSysTimestamp(currTs);
                // update sysActivCtlModel
                sysActivCtlService.update(newActivCtlModel);
                log.info("update sysTs success! {}", newActivCtlModel);
                return true;
            } else {
                Long diff = oldTs - currTs;
                if (diff > maxDiffMilliSecond) {
                    isSystemTimestampRollback = true;
                    log.error("Detected that system timestamp has rollback, currentTimestamp:{}, oldTimestamp:{}, diff:{}, maxDiff:{}", currTs, oldTs, diff, maxDiffMilliSecond);
                }
            }
        } else {
            // null, insert a default sysActivCtlModel
            generateOrGetDefaultActivCtlModel();
        }

        return false;
    }


    public SysActivCtlModel getDefaultSysActivCtl() {
        return generateOrGetDefaultActivCtlModel();
    }

    public boolean isSystemTimestampRollback() {
        return isSystemTimestampRollback;
    }


    /**
     * generate a default with current timestamp as system_ts and activ_ts;
     * @return
     */
    synchronized private SysActivCtlModel generateOrGetDefaultActivCtlModel() {
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectFirst();
        if (Objects.isNull(sysActivCtlModel)) {
            sysActivCtlModel = new SysActivCtlModel();
            Long currTs = System.currentTimeMillis();
            sysActivCtlModel.setId("1");
            sysActivCtlModel.setSysTimestamp(currTs);
            sysActivCtlModel.setActiveTimestamp(currTs);

            ObjServiceResult<SysActivCtlModel> objResult  = sysActivCtlService.create(sysActivCtlModel);
            if (Objects.nonNull(objResult.getSource())) {
                log.info("Generate default activCtlModel success! {}", objResult.getSource().getObj());
//                sysActivCtlModel = sysActivCtlMapper.selectFirst();
                sysActivCtlModel = objResult.getSource().getObj();
            }
        } else {
            log.info("Default activCtlModel has already generated! {}", sysActivCtlModel);
        }

        return sysActivCtlModel;
    }


}
