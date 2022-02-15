package cn.cyberpeace.mp.front.admin.service.sys;

import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.front.admin.model.SysActivCtlModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 系统时间回拨阈值为12小时
    private static final long MAX_DIFF_MILLISECOND = 12 * 60 * 60 * 1000;

    private static volatile boolean IS_SYSTEM_TS_CALLBACK = false;

    /**
     * 定时任务调度，刷新系统时间，防止服务器时间回拨，阈值
     * @return
     */
    public boolean updateSysTs() {
        // update system timestamp
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectFirst();
        if (Objects.nonNull(sysActivCtlModel)) {
            // not null, to update
            Long currTs = System.currentTimeMillis();
            Long oldTs = sysActivCtlModel.getSysTimestamp();
            if (currTs >= oldTs) {
                sysActivCtlModel.setSysTimestamp(currTs);
                // update sysActivCtlModel
                sysActivCtlService.update(sysActivCtlModel);
                return true;
            } else {
                Long diff = oldTs - currTs;
                if (diff > MAX_DIFF_MILLISECOND) {
                    IS_SYSTEM_TS_CALLBACK = true;
                    log.error("Detected that system timestamp has callback, currentTimestamp:{}, oldTimestamp:{}", currTs, oldTs);
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
