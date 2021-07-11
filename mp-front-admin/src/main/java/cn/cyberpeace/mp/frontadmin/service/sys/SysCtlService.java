package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;
import cn.cyberpeace.mp.frontadmin.util.EncryptUtils;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class SysCtlService {

    @Autowired
    private SysActivCtlService sysActivCtlService;

    private volatile boolean isSystemTimestampRollback = false;

    
    @Scheduled(initialDelay = 1, fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    public boolean updateSysTs() {







        
        SysActivCtlModel oldActivCtlModel = getValidCtlModel();
        log.info("updateSysTs, oldActivCtlModel:{}", oldActivCtlModel);
        
        if (Objects.nonNull(oldActivCtlModel) && StringUtils.isNotBlank(oldActivCtlModel.getFirstActiveDate())) {
            
            Long currTs = System.currentTimeMillis();
            Long oldTs = oldActivCtlModel.getSysTimestamp();
            if (Objects.nonNull(oldTs) && oldTs > 0 && currTs >= oldTs) {
                
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
                
                sysActivCtlService.update(newActivCtlModel);
                log.info("update sysTs success! {}", newActivCtlModel);
                return true;
            } else {
                
                SysActivCtlModel newActivCtlModel = new SysActivCtlModel();
                newActivCtlModel.setId(oldActivCtlModel.getId());
                newActivCtlModel.setSysTimestamp(currTs);
                sysActivCtlService.update(newActivCtlModel);
            }
        }

        return false;
    }


    public boolean isSystemTimestampRollback() {
        return isSystemTimestampRollback;
    }

    public SysActivCtlModel getUnusedActivCtlModel() {
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectUnused();
        return sysActivCtlModel;
    }

    
    synchronized public SysActivCtlModel generateOrGetUnusedActivCtlModel() {
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectUnused();
        if (Objects.isNull(sysActivCtlModel)) {
            sysActivCtlModel = new SysActivCtlModel();
            
            String suffix = RandomUtil.randomString(6);
            sysActivCtlModel.setSuffix(suffix);
            sysActivCtlModel.setStatus(SysActivCtlModel.STATUS_UNUSED);

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


    
    public SysActivCtlModel getValidCtlModel() {
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectValid();
        return sysActivCtlModel;
    }

}
