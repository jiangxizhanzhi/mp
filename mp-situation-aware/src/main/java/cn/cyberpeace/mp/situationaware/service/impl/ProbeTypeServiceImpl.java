package cn.cyberpeace.mp.situationaware.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.situationaware.model.ProbeTypeModel;
import cn.cyberpeace.mp.situationaware.repository.ProbeTypeMapper;
import cn.cyberpeace.mp.situationaware.service.ProbeTypeService;
import cn.cyberpeace.mp.situationaware.service.fields.ProbeTypeFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class ProbeTypeServiceImpl extends BaseModelServiceImpl<ProbeTypeModel, ProbeTypeMapper, ProbeTypeFields, QueryParams>
        implements ProbeTypeService {

    public ProbeTypeServiceImpl(ProbeTypeMapper probeTypeMapper, ProbeTypeFields probeTypeFields) {
        setModelMapper(probeTypeMapper);
        setModelFields(probeTypeFields);
    }

    @Override
    public List<ProbeTypeModel> findAll() {
        return getModelMapper().list(new HumanCondition());
    }
}
