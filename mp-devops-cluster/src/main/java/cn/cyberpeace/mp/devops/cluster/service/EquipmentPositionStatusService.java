package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.cluster.model.EquipmentPositionStatusModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface EquipmentPositionStatusService extends BaseModelService<EquipmentPositionStatusModel, QueryParams> {
    /**
     * 查询所有位置状态
     * @return
     */
    List<EquipmentPositionStatusModel> findAll();
}
