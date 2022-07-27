package cn.cyberpeace.mp.cscene.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.cscene.model.CsceneStatusModel;
import cn.cyberpeace.mp.cscene.model.CsceneTypeModel;
import cn.cyberpeace.mp.cscene.repository.CsceneStatusMapper;
import cn.cyberpeace.mp.cscene.service.CsceneStatusService;
import cn.cyberpeace.mp.cscene.service.filed.CsceneStatusFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class CsceneStatusServiceImpl extends BaseModelServiceImpl<CsceneStatusModel, CsceneStatusMapper,
        CsceneStatusFields, QueryParams>
        implements CsceneStatusService {

    public CsceneStatusServiceImpl(CsceneStatusMapper csceneStatusMapper, CsceneStatusFields csceneStatusFields) {
        setModelMapper(csceneStatusMapper);
        setModelFields(csceneStatusFields);
    }

    @Override
    public List<CsceneStatusModel> findAll() {
        return getModelMapper().list(new HumanCondition());
    }
}
