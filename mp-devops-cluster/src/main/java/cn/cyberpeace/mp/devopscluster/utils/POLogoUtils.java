package cn.cyberpeace.mp.devopscluster.utils;

import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Slf4j
public class POLogoUtils {

    /**
     * PO对象path转fdfs地址
     * @param deviceTargetPOS
     */
    public static void changePOPathToUrl(List<DeviceTargetPO> deviceTargetPOS) {
        if (CollectionUtils.isNotEmpty(deviceTargetPOS)) {
            List<LocalFileMappingModel> mappingModelList = DefaultImgUtil.listByGroups(Collections.singletonList(DefaultImgDirEnum.TARGET.getGroup()));
            deviceTargetPOS.forEach(targetPO -> targetPO.setLogo(DefaultImgUtil.pickPathToFdfsUrl(targetPO.getLogo(), mappingModelList)));
        }
    }

    /**
     * device对象path转fdfs地址
     * @param deviceList
     */
    public static void changeDevicePathToUrl(List<Device> deviceList) {
        if (CollectionUtils.isNotEmpty(deviceList)) {
            List<LocalFileMappingModel> mappingModelList = DefaultImgUtil.listByGroups(Collections.singletonList(DefaultImgDirEnum.TARGET.getGroup()));
            deviceList.forEach(device -> device.setLogo(DefaultImgUtil.pickPathToFdfsUrl(device.getLogo(), mappingModelList)));
        }
    }
    
}
