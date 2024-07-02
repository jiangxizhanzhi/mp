package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivCtlService;
import cn.cyberpeace.mp.frontadmin.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @ClassName SysCtlService
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/2/14 18:07
 * @Version 1.0.0
 */
@Slf4j
@Service
public class SysCtlService {

    @Autowired
    private SysActivCtlService sysActivCtlService;

    private volatile boolean isSystemTimestampRollback = false;

    public boolean isSystemTimestampRollback() {
        return isSystemTimestampRollback;
    }

    /**
     * getUnusedActivCtlModel
     *
     * @return
     */
    public SysActivCtlModel getUnusedActivCtlModel() {
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectUnused();
        return sysActivCtlModel;
    }

    /**
     * generate a default with current timestamp as system_ts and activ_ts;
     *
     * @return
     */
    synchronized public SysActivCtlModel generateOrGetUnusedActivCtlModel() {
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectUnused();
        if (Objects.isNull(sysActivCtlModel)) {
            sysActivCtlModel = new SysActivCtlModel();
            // 只有在一个新的license第一次激活的时候才会写入系统时间戳
            String suffix = RandomUtils.randomString(6);
            sysActivCtlModel.setSuffix(suffix);
            sysActivCtlModel.setStatus(SysActivCtlModel.STATUS_UNUSED);

            ObjServiceResult<SysActivCtlModel> objResult  = sysActivCtlService.create(sysActivCtlModel);

            // todo 将其他状态为1的记录，全更新为0
            //sysActivCtlService.invalidOtherExceptOne("-1");

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
     *
     * @return
     */
    public SysActivCtlModel getValidCtlModel() {
        SysActivCtlModel sysActivCtlModel = sysActivCtlService.selectValid();
        return sysActivCtlModel;
    }

}
