package cn.cyberpeace.mp.situationaware.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.situationaware.model.OsTypeModel;
import cn.cyberpeace.mp.situationaware.repository.OsTypeMapper;
import cn.cyberpeace.mp.situationaware.service.OsTypeService;
import cn.cyberpeace.mp.situationaware.service.fields.OsTypeFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class OsTypeServiceImpl extends BaseModelServiceImpl<OsTypeModel, OsTypeMapper, OsTypeFields, QueryParams>
        implements OsTypeService {

    public OsTypeServiceImpl(OsTypeMapper osTypeMapper, OsTypeFields osTypeFields) {
        setModelMapper(osTypeMapper);
        setModelFields(osTypeFields);
    }

    @Override
    public List<OsTypeModel> findAll() {
        return getModelMapper().list(new HumanCondition());
    }
}
