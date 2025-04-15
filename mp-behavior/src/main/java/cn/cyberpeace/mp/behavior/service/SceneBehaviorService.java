package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.behavior.bo.NodeBehavior;
import cn.cyberpeace.mp.behavior.bo.NodeUserBehavior;
import cn.cyberpeace.mp.behavior.mapper.SceneModelMapper;
import cn.cyberpeace.mp.behavior.mapper.SimulateMapper;
import cn.cyberpeace.mp.behavior.mapper.TaskMapper;
import cn.cyberpeace.mp.behavior.model.SceneModel;
import cn.cyberpeace.mp.behavior.model.SimulateModel;
import cn.cyberpeace.mp.behavior.model.TaskModel;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SceneBehaviorService {

	@Autowired
	private SceneModelMapper sceneModelMapper;

	@Autowired
	private SimulateService simulateService;

	@Autowired
	private TaskMapper taskMapper;

	@Autowired
	private SimulateMapper simulateMapper;

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
		SimulateModel simulateModel = simulateMapper.first(condition);
		SceneModel sceneModel = sceneModelMapper.first(condition);
		if (sceneModel != null) {
			sceneModel.setNodeConfig(JSONObject.toJSONString(nodeBehavior.getUserBehaviorList()));
			sceneModelMapper.update(sceneModel);
		} else {
			sceneModel = new SceneModel();
			sceneModel.setSceneId(nodeBehavior.getSceneId());
			sceneModel.setNodeId(nodeBehavior.getNodeId());
			sceneModel.setNodeConfig(JSONObject.toJSONString(nodeBehavior.getUserBehaviorList()));
			sceneModel.setInstance(false);
			sceneModelMapper.create(sceneModel);
		}

		if (sceneModel.getInstance()) {
			simulateService.deleteTask(simulateModel);
			List<TaskModel> taskModelList = simulateService.createTask(nodeBehavior.getUserBehaviorList(), simulateModel.getId());
			taskMapper.bulkCreate(taskModelList);
			simulateMapper.update(simulateModel);
			if (SimulateModel.StatusEnum.RUNNING.getCode().equals(simulateModel.getStatus()) ) {
				simulateService.runSimulate(simulateModel.getId(), true);
			}
		}
	}
}
