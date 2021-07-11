package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;
import cn.cyberpeace.mp.frontadmin.repository.SysActivCtlMapper;
import cn.cyberpeace.mp.frontadmin.service.fields.SysActivCtlFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivCtlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class SysActivCtlServiceImpl extends BaseModelServiceImpl<SysActivCtlModel, SysActivCtlMapper, SysActivCtlFields, QueryParams>
        implements SysActivCtlService {

    @Autowired
    public SysActivCtlServiceImpl(SysActivCtlMapper modelMapper, SysActivCtlFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public SysActivCtlModel selectUnused() {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(SysActivCtlModel::getStatus, SysActivCtlModel.STATUS_UNUSED);
        return getModelMapper().first(humanCondition);
    }

    @Override
    public SysActivCtlModel selectValid() {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(SysActivCtlModel::getStatus, SysActivCtlModel.STATUS_VALID);
        return getModelMapper().first(humanCondition);
    }

    @Override
    public void invalidOtherExceptOne(String exceptedId) {
        HumanCondition humanCondition = new HumanCondition()
                .filterNotEqual(SysActivCtlModel::getId, exceptedId);
        SysActivCtlModel model = new SysActivCtlModel();
        model.setStatus(SysActivCtlModel.STATUS_INVALID);
        getModelMapper().filterUpdate(model, humanCondition);
    }

    private List<SysActivCtlModel> selectAll() {
        List<SysActivCtlModel> list = getModelMapper().list(new HumanCondition());
        return list;
    }
}
