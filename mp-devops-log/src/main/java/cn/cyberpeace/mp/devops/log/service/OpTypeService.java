package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.log.model.OpModuleModel;
import cn.cyberpeace.mp.devops.log.model.OpTypeModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface OpTypeService extends BaseModelService<OpTypeModel, QueryParams> {
    /**
     * 查询所有操作类型
     * @return
     */
    List<OpTypeModel> findAll();
}
