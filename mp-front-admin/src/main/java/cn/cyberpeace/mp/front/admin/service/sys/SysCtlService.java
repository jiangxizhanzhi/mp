package cn.cyberpeace.mp.front.admin.service.sys;

import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.front.admin.model.SysActivCtlModel;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    private volatile boolean isSystemTimestampRollback = false;

    /**
     * 定时任务调度，刷新系统时间，同时更新系统运行时长 sys_run_seconds
     * 每30分钟执行一次
     * @return
     */
    @Scheduled(initialDelay = 10 * 1000, fixedRate = 30 * 60 * 1000)
//    @Scheduled(initialDelay = 10 * 1000, fixedRate = 60 * 1000)
    public boolean updateSysTs() {

//        if (isSystemTimestampRollback) {
//            // 发生时间回拨，停止变更
//            log.error("time rollback occured!");
//            return false;
//        }

        // update system timestamp
        SysActivCtlModel oldActivCtlModel = getValidCtlModel();
        // 判断系统是否已经注册，未激活不统计运行时长
        if (Objects.nonNull(oldActivCtlModel) && StringUtils.isNotBlank(oldActivCtlModel.getFirstActiveDate())) {
            // not null, to update  example: oldTs 1644778431000
            Long currTs = System.currentTimeMillis();
            Long oldTs = oldActivCtlModel.getSysTimestamp();
            if (Objects.nonNull(oldTs) && oldTs > 0 && currTs >= oldTs) {
                // 更新数据库系统时间戳，同时累加run_seconds
                Long diff = currTs - oldTs;
                Long diffSeconds = diff / 1000;
                Long oldRunSeconds = oldActivCtlModel.getSysRunSeconds();
                SysActivCtlModel newActivCtlModel = new SysActivCtlModel();
                newActivCtlModel.setId(oldActivCtlModel.getId());
                newActivCtlModel.setSysTimestamp(currTs);

                if (Objects.nonNull(oldRunSeconds)) {
                    newActivCtlModel.setSysRunSeconds(diffSeconds + oldRunSeconds);
                } else {
                    // todo ?
                    newActivCtlModel.setSysRunSeconds(0L);
                }
                // update sysActivCtlModel
                sysActivCtlService.update(newActivCtlModel);
                log.info("update sysTs success! {}", newActivCtlModel);
                return true;
            } else {
                // 直接更新系统时间戳，不累加run_seconds
//                Long diff = oldTs - currTs;
//                if (diff > maxDiffMilliSecond) {
//                    isSystemTimestampRollback = true;
//                    log.error("Detected that system timestamp has rollback, currentTimestamp:{}, oldTimestamp:{}, diff:{}, maxDiff:{}", currTs, oldTs, diff, maxDiffMilliSecond);
//                }
                SysActivCtlModel newActivCtlModel = new SysActivCtlModel();
                newActivCtlModel.setId(oldActivCtlModel.getId());
                newActivCtlModel.setSysTimestamp(currTs);
                sysActivCtlService.update(newActivCtlModel);
            }
        }

        return false;
    }


    //public SysActivCtlModel getDefaultSysActivCtl() {
    //    return generateOrGetUnusedActivCtlModel();
    //}

    public boolean isSystemTimestampRollback() {
        return isSystemTimestampRollback;
    }

    public SysActivCtlModel getUnusedActivCtlModel() {
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectUnused();
        return sysActivCtlModel;
    }

    /**
     * generate a default with current timestamp as system_ts and activ_ts;
     * @return
     */
    synchronized public SysActivCtlModel generateOrGetUnusedActivCtlModel() {
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectUnused();
        if (Objects.isNull(sysActivCtlModel)) {
            sysActivCtlModel = new SysActivCtlModel();
            // 只有在一个新的license第一次激活的时候才会写入系统时间戳
//            Long currTs = System.currentTimeMillis();
//            sysActivCtlModel.setSysTimestamp(currTs);
            String suffix = RandomUtil.randomString(6);
            sysActivCtlModel.setSuffix(suffix);
            sysActivCtlModel.setStatus(2);

            ObjServiceResult<SysActivCtlModel> objResult  = sysActivCtlService.create(sysActivCtlModel);
            if (Objects.nonNull(objResult.getSource())) {
                log.info("Generate default activCtlModel success! {}", objResult.getSource().getObj());
                sysActivCtlModel = objResult.getSource().getObj();
            }
        } else {
            log.info("Default activCtlModel has already generated! {}", sysActivCtlModel);
        }

        return sysActivCtlModel;
    }


    /**
     * generate a default with current timestamp as system_ts and activ_ts;
     * @return
     */
    public SysActivCtlModel getValidCtlModel() {
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectValid();
//        if (Objects.isNull(sysActivCtlModel)) {
//            sysActivCtlModel = new SysActivCtlModel();
//            // 只有在一个新的license第一次激活的时候才会写入系统时间戳
////            Long currTs = System.currentTimeMillis();
////            sysActivCtlModel.setSysTimestamp(currTs);
//            String suffix = RandomUtil.randomString(6);
//            sysActivCtlModel.setSuffix(suffix);
//            sysActivCtlModel.setStatus(2);
//
//            ObjServiceResult<SysActivCtlModel> objResult  = sysActivCtlService.create(sysActivCtlModel);
//            if (Objects.nonNull(objResult.getSource())) {
//                log.info("Generate default activCtlModel success! {}", objResult.getSource().getObj());
//                sysActivCtlModel = objResult.getSource().getObj();
//            }
//        } else {
//            log.info("Default activCtlModel has already generated! {}", sysActivCtlModel);
//        }

        return sysActivCtlModel;
    }

}
