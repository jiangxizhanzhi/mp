package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.behavior.bo.NodeBehavior;
import cn.cyberpeace.mp.behavior.bo.NodeUserBehavior;
import cn.cyberpeace.mp.behavior.mapper.SceneModelMapper;
import cn.cyberpeace.mp.behavior.model.SceneModel;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class SceneBehaviorService {

	@Autowired
	private SceneModelMapper sceneModelMapper;

	public NodeBehavior getNodeBehavior(String sceneId, String nodeId) {
		HumanCondition condition = new HumanCondition().filterEqual(SceneModel::getSceneId, sceneId)
													   .filterEqual(SceneModel::getNodeId, nodeId);
		SceneModel sceneModel = sceneModelMapper.first(condition);
		NodeBehavior nodeBehavior = new NodeBehavior();
		nodeBehavior.setNodeId(nodeId);
		nodeBehavior.setSceneId(sceneId);
		if (sceneModel == null) {
			nodeBehavior.setUserBehaviorList(new ArrayList<>());
			return nodeBehavior;
		}
		nodeBehavior.setUserBehaviorList(JSONObject.parseArray(sceneModel.getNodeConfig(), NodeUserBehavior.class));
		return nodeBehavior;
	}

	public void save(NodeBehavior nodeBehavior) {
		HumanCondition condition = new HumanCondition().filterEqual(SceneModel::getSceneId, nodeBehavior.getSceneId())
													   .filterEqual(SceneModel::getNodeId, nodeBehavior.getNodeId());
		sceneModelMapper.filterDelete(condition);
		SceneModel sceneModel = new SceneModel();
		sceneModel.setSceneId(nodeBehavior.getSceneId());
		sceneModel.setNodeId(nodeBehavior.getNodeId());
		sceneModel.setNodeConfig(JSONObject.toJSONString(nodeBehavior.getUserBehaviorList()));
		sceneModelMapper.create(sceneModel);
	}
}
