package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskBO.TaskFlow;
import cn.cyberpeace.mp.flow.v2.model.FlowModel;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.v2.service.fields.FlowTaskFields;
import cn.cyberpeace.mp.flow.v2.service.param.FlowQueryParams;
import cn.cyberpeace.mp.flow.v2.service.param.FlowTaskQueryParams;
import cn.hutool.core.collection.CollectionUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FlowTaskService extends BaseModelServiceImpl<FlowTaskModel, FlowTaskMapper, FlowTaskFields, FlowTaskQueryParams> {
	
	@Autowired
	private FlowService flowService;
	
    @Autowired
    public FlowTaskService(@Qualifier("v2FlowTaskMapper") FlowTaskMapper modelMapper, @Qualifier("v2FlowTaskFields") FlowTaskFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowTaskQueryParams queryParams) {
        var categories = queryParams.getCategories();
        if (categories != null && !categories.isEmpty()) {
            humanCondition.filterIn(FlowModel::getCategory, categories.toArray());
        }
    }

	/**
	 * 查询填充流量信息
	 * 
	 * @param flowTaskBO
	 */
	public void fillFlowInfo(FlowTaskBO flowTaskBO) {
		if(flowTaskBO == null || CollectionUtil.isEmpty(flowTaskBO.getFlows())) {
			return;
		}
		
		List<TaskFlow> taskFlows = flowTaskBO.getFlows();
		List<String> flowIds = taskFlows.stream().map(TaskFlow::getFlowId).collect(Collectors.toList());
		FlowQueryParams queryParams = new FlowQueryParams();
		queryParams.setIds(flowIds.toArray(new String[0]));
		ListObjServiceResult<FlowModel> flowModelResult = flowService.list(queryParams);

		Map<String, FlowModel> flowModelMap = flowModelResult.getSource().getObjs().stream()
				.collect(Collectors.toMap(FlowModel::getId, Function.identity(), (v1, v2) -> v2));
		double totalWeight = taskFlows.stream().mapToInt(TaskFlow::getWeight).sum();
		for (TaskFlow taskFlow : taskFlows) {
			FlowModel flowModel = flowModelMap.get(taskFlow.getFlowId());
			if(flowModel != null) {
				taskFlow.setName(flowModel.getName());
				taskFlow.setProtocols(flowModel.getProtocols());
				taskFlow.setWeightRatio(String.format("%.2f", taskFlow.getWeight() / totalWeight * 100));
			}
		}
	}
	
}
