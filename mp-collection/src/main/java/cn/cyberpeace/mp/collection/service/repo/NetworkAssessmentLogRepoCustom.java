package cn.cyberpeace.mp.collection.service.repo;

import cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo;

/**
 * @ClassName NetworkAssessmentLogRepoCustom
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/28 20:08
 **/
public interface NetworkAssessmentLogRepoCustom {

    /**
     *  获取某类设备最新的弹性日志
     * @author
     * @date 20:18 2023/11/28
     * @param deviceType
     * @return cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo
     **/
    NetworkAssessmentLogPo findLatestNetworkLogByDeviceType(String deviceType);
}
