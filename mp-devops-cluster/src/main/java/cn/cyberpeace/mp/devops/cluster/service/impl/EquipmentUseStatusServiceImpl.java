package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.model.EquipmentPositionStatusModel;
import cn.cyberpeace.mp.devops.cluster.model.EquipmentUseStatusModel;
import cn.cyberpeace.mp.devops.cluster.repository.EquipmentPositionStatusMapper;
import cn.cyberpeace.mp.devops.cluster.repository.EquipmentUseStatusMapper;
import cn.cyberpeace.mp.devops.cluster.service.EquipmentPositionStatusService;
import cn.cyberpeace.mp.devops.cluster.service.EquipmentUseStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class EquipmentUseStatusServiceImpl implements EquipmentUseStatusService {

    @Autowired
    private EquipmentUseStatusMapper equipmentUseStatusMapper;

    @Override
    public List<EquipmentUseStatusModel> findAll() {
        return equipmentUseStatusMapper.selectAll();
    }
}
