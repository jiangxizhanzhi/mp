package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.cluster.model.EquipmentPositionStatusModel;
import cn.cyberpeace.mp.devops.cluster.model.EquipmentUseStatusModel;
import cn.cyberpeace.mp.devops.cluster.repository.EquipmentPositionStatusMapper;
import cn.cyberpeace.mp.devops.cluster.repository.EquipmentUseStatusMapper;
import cn.cyberpeace.mp.devops.cluster.service.EquipmentPositionStatusService;
import cn.cyberpeace.mp.devops.cluster.service.EquipmentUseStatusService;
import cn.cyberpeace.mp.devops.cluster.service.fields.EquipmentUseStatusFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class EquipmentUseStatusServiceImpl extends BaseModelServiceImpl<EquipmentUseStatusModel, EquipmentUseStatusMapper, EquipmentUseStatusFields, QueryParams>
        implements EquipmentUseStatusService {

    @Autowired
    public EquipmentUseStatusServiceImpl(EquipmentUseStatusMapper equipmentUseStatusMapper,
                                         EquipmentUseStatusFields equipmentUseStatusFields) {
        setModelMapper(equipmentUseStatusMapper);
        setModelFields(equipmentUseStatusFields);
    }

    @Override
    public List<EquipmentUseStatusModel> findAll() {
        return getModelMapper().list(new HumanCondition());
    }
}
