package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.cluster.model.EquipmentPositionStatusModel;
import cn.cyberpeace.mp.devops.cluster.model.EquipmentUseStatusModel;
import cn.cyberpeace.mp.devops.cluster.repository.EquipmentPositionStatusMapper;
import cn.cyberpeace.mp.devops.cluster.repository.EquipmentUseStatusMapper;
import cn.cyberpeace.mp.devops.cluster.service.EquipmentPositionStatusService;
import cn.cyberpeace.mp.devops.cluster.service.fields.EquipmentPositionStatusFields;
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
public class EquipmentPositionStatusServiceImpl extends BaseModelServiceImpl<EquipmentPositionStatusModel, EquipmentPositionStatusMapper, EquipmentPositionStatusFields, QueryParams>
        implements EquipmentPositionStatusService {

    public EquipmentPositionStatusServiceImpl(EquipmentPositionStatusMapper equipmentPositionStatusMapper,
                                              EquipmentPositionStatusFields equipmentPositionStatusFields) {
        setModelMapper(equipmentPositionStatusMapper);
        setModelFields(equipmentPositionStatusFields);
    }

    @Override
    public List<EquipmentPositionStatusModel> findAll() {
        return getModelMapper().list(new HumanCondition());
    }
}
