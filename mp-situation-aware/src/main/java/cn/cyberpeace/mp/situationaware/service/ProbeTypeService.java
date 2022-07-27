package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.situationaware.model.ProbeTypeModel;
import cn.cyberpeace.mp.situationaware.repository.ProbeTypeMapper;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface ProbeTypeService extends BaseModelService<ProbeTypeModel, QueryParams> {

    /**
     * 查询所有探针类型
     * @return
     */
    List<ProbeTypeModel> findAll();
}
