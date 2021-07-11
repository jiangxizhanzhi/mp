package cn.cyberpeace.mp.frontbusiness.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontbusiness.fields.UrlACTargetFields;
import cn.cyberpeace.mp.frontbusiness.mapper.UrlACTargetMapper;
import cn.cyberpeace.mp.frontbusiness.model.UrlACTargetModel;
import cn.cyberpeace.mp.frontbusiness.service.UrlACTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UrlACTargetServiceImpl extends BaseModelServiceImpl<UrlACTargetModel, UrlACTargetMapper, UrlACTargetFields, QueryParams>
    implements UrlACTargetService {

    @Autowired
    public UrlACTargetServiceImpl(UrlACTargetMapper urlACTargetMapper) {
        setModelMapper(urlACTargetMapper);
    }

    @Override
    public int deleteByUrlACId(String[] urlacIds) {
        HumanCondition condition = new HumanCondition()
                .filterIn(UrlACTargetModel::getUrlacId, urlacIds);
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }
}
