package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.model.EquipmentPositionStatusModel;
import cn.cyberpeace.mp.devops.cluster.repository.EquipmentPositionStatusMapper;
import cn.cyberpeace.mp.devops.cluster.service.EquipmentPositionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class EquipmentPositionStatusServiceImpl implements EquipmentPositionStatusService {

    @Autowired
    private EquipmentPositionStatusMapper equipmentPositionStatusMapper;
    @Override
    public List<EquipmentPositionStatusModel> findAll() {
        return equipmentPositionStatusMapper.selectAll();
    }
}
