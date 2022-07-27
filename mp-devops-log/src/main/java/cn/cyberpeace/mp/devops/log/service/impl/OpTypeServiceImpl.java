package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.log.model.OpTypeModel;
import cn.cyberpeace.mp.devops.log.repository.OpTypeMapper;
import cn.cyberpeace.mp.devops.log.service.OpTypeService;
import cn.cyberpeace.mp.devops.log.service.fields.OpTypeFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class OpTypeServiceImpl extends BaseModelServiceImpl<OpTypeModel, OpTypeMapper, OpTypeFields, QueryParams>
        implements OpTypeService {

    public OpTypeServiceImpl(OpTypeMapper opTypeMapper, OpTypeFields opTypeFields) {
        setModelMapper(opTypeMapper);
        setModelFields(opTypeFields);
    }

    @Override
    public List<OpTypeModel> findAll() {
        return getModelMapper().list(new HumanCondition());
    }
}
