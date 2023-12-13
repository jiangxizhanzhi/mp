package cn.cyberpeace.mp.devopscluster.service.impl;


import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.cscene.service.UpdateReference;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.frontadmin.service.sys.LicenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class UpdateReferenceServiceImpl implements UpdateReference {

    @Resource
    private DeviceTargetMapper deviceTargetMapper;

    @Override
    public void update(List<String> image_uuids) {
        for (String image_uuid : image_uuids) {
            DeviceTargetPO deviceTargetPO = deviceTargetMapper.findByUuid(image_uuid);
            if (deviceTargetPO != null) {
                deviceTargetPO.setReferenceNums(deviceTargetPO.getReferenceNums() + 1);
                deviceTargetMapper.updateById(deviceTargetPO);
            } else {
                log.error("Unable to locate the target：{}", image_uuid);
            }
        }
    }
}
