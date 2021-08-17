package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.service.RealEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RealEquipmentConverterImpl implements RealEquipmentConverter {

    private RealEquipmentService realEquipmentService;

    @Autowired
    public RealEquipmentConverterImpl(RealEquipmentService realEquipmentService) {
        this.realEquipmentService = realEquipmentService;
    }

    @Override
    public Integer getTotalRealEquipment() {
        return realEquipmentService.getTotalRealEquipment();
    }
}
