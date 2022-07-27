package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.cluster.model.EquipmentUseStatusModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface EquipmentUseStatusService extends BaseModelService<EquipmentUseStatusModel, QueryParams> {
    /**
     * 查询所有使用状态
     * @return
     */
    List<EquipmentUseStatusModel> findAll();
}
