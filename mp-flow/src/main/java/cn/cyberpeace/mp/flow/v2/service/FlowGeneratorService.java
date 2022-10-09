package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.model.FlowGeneratorModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowGeneratorMapper;
import cn.cyberpeace.mp.flow.v2.service.fields.FlowGeneratorFields;
import cn.cyberpeace.mp.flow.v2.service.param.FlowGeneratorQueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowGeneratorService extends BaseModelServiceImpl<FlowGeneratorModel, FlowGeneratorMapper, FlowGeneratorFields, FlowGeneratorQueryParams> {

    @Autowired
    public FlowGeneratorService(FlowGeneratorMapper modelMapper, FlowGeneratorFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowGeneratorQueryParams queryParams) {
        var types = queryParams.getTypes();
        if (types != null && !types.isEmpty()) {
            humanCondition.filterIn(FlowGeneratorModel::getType, types.toArray());
        }
    }

    public ObjServiceResult<FlowGeneratorBO> createFlowGenerator(FlowGeneratorBO bo, String[] fields) {
        // TODO: 创建对应资源
        return createBO(bo, fields, FlowGeneratorBO.class);
    }

    public ObjServiceResult<FlowGeneratorBO> updateFlowGenerator(FlowGeneratorBO bo, String[] fields) {
        // TODO: 更新对应资源
        return updateBO(bo, fields, FlowGeneratorBO.class);
    }

    public ServiceResult<Integer> bulkDeleteFlowGenerator(String[] ids) {
        // TODO: 删除对应资源
        return bulkDeleteBO(ids, FlowGeneratorBO.class);
    }

    public ServiceResult<List<FlowGeneratorBO.Nic>> addFlowGeneratorNics(String id, List<FlowGeneratorBO.Nic> nics) {
        var bo = getBO(id, FlowGeneratorBO.class).sourceObj();
        // TODO: 添加对应网卡，注意同步问题
        updateBO(bo, FlowGeneratorBO.class);
        return new ServiceResult<>(nics);
    }

    public ServiceResult<Integer> removeFlowGeneratorNics(String id, List<String> nicIds) {
        var bo = getBO(id, FlowGeneratorBO.class).sourceObj();
        // TODO: 删除对应网卡，注意同步问题
        updateBO(bo, FlowGeneratorBO.class);
        return new ServiceResult<>(nicIds.size());
    }

}
