package cn.cyberpeace.mp.cscene.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.cscene.model.CsceneStatusModel;
import cn.cyberpeace.mp.cscene.model.CsceneTypeModel;
import cn.cyberpeace.mp.cscene.repository.CsceneStatusMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneTypeMapper;
import cn.cyberpeace.mp.cscene.service.CsceneStatusService;
import cn.cyberpeace.mp.cscene.service.CsceneTypeService;
import cn.cyberpeace.mp.cscene.service.filed.CsceneTypeFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class CsceneTypeServiceImpl extends BaseModelServiceImpl<CsceneTypeModel, CsceneTypeMapper, CsceneTypeFields,
        QueryParams>
        implements CsceneTypeService {

    @Autowired
    public CsceneTypeServiceImpl(CsceneTypeMapper csceneTypeMapper, CsceneTypeFields csceneTypeFields) {
        setModelMapper(csceneTypeMapper);
        setModelFields(csceneTypeFields);
    }
    @Override
    public List<CsceneTypeModel> findAll() {
        return getModelMapper().list(new HumanCondition());
    }
}
