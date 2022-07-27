package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.log.model.OpModuleModel;
import cn.cyberpeace.mp.devops.log.repository.OpModuleMapper;
import cn.cyberpeace.mp.devops.log.service.OpModuleService;
import cn.cyberpeace.mp.devops.log.service.fields.OpModuleFields;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class OpModuleServiceImpl extends BaseModelServiceImpl<OpModuleModel, OpModuleMapper, OpModuleFields, QueryParams>
        implements OpModuleService {

    public OpModuleServiceImpl(OpModuleMapper opModuleMapper, OpModuleFields opModuleFields) {
        setModelMapper(opModuleMapper);
        setModelFields(opModuleFields);
    }

    @Override
    public List<OpModuleModel> findAll() {
        return getModelMapper().list(new HumanCondition());
    }
}
