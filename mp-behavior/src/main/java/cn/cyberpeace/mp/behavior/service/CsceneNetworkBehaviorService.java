package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.behavior.bo.NetworkBehaviorConfigBo;
import cn.cyberpeace.mp.behavior.model.CsceneNetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.model.NetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.repository.CsceneNetworkBehaviorMapper;
import cn.cyberpeace.mp.behavior.repository.NetworkBehaviorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CsceneNetworkBehaviorService {

	@Autowired
	private CsceneNetworkBehaviorMapper csceneNetworkBehaviorMapper;

	@Autowired
	private NetworkBehaviorMapper networkBehaviorMapper;

	public List<String> add(List<NetworkBehaviorConfigBo> configBos, String nodeId) {
		List<String> ids = configBos.stream().map(NetworkBehaviorConfigBo::getNetworkBehaviorId).collect(Collectors.toList());

		HumanCondition humanCondition = new HumanCondition().filterIn(NetworkBehaviorModel::getId, ids.toArray());

		Map<String, NetworkBehaviorModel> behaviorModelMap = networkBehaviorMapper.list(humanCondition)
																				  .stream().collect(Collectors.toMap(NetworkBehaviorModel::getId, m -> m));

		List<CsceneNetworkBehaviorModel> modelList = new ArrayList<>();
		List<String> csceneNetworkBehaviorIds = new ArrayList<>();
		for (NetworkBehaviorConfigBo bo : configBos) {
			CsceneNetworkBehaviorModel model = new CsceneNetworkBehaviorModel();
			NetworkBehaviorModel originalModel = behaviorModelMap.get(bo.getNetworkBehaviorId());
			if (originalModel == null) {
				throw new MpRuntimeException("找不到网络行为数据");
			}
			BeanUtils.copyProperties(originalModel, model);
			model.setNetworkBehaviorId(bo.getNetworkBehaviorId());
			model.setNodeId(nodeId);
			model.setCreateTime(null);
			model.setUpdateTime(null);
			model.setUrl(bo.getUrl());
			model.setId(TextUtils.uuid());
			csceneNetworkBehaviorIds.add(model.getId());
			modelList.add(model);
		}
		csceneNetworkBehaviorMapper.bulkCreate(modelList);
		return csceneNetworkBehaviorIds;
	}

	public void deleteByNodeId(String nodeId) {
		HumanCondition humanCondition = new HumanCondition().filterEqual(CsceneNetworkBehaviorModel::getNodeId, nodeId);
		csceneNetworkBehaviorMapper.filterDelete(humanCondition);
	}

	public Map<String, CsceneNetworkBehaviorModel> getMapByNodeId(String nodeId) {
		HumanCondition humanCondition = new HumanCondition().filterEqual(CsceneNetworkBehaviorModel::getNodeId, nodeId);
		return csceneNetworkBehaviorMapper.list(humanCondition).stream().collect(Collectors.toMap(CsceneNetworkBehaviorModel::getId, m -> m));
	}
}
