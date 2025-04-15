package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.behavior.bo.*;
import cn.cyberpeace.mp.behavior.mapper.SceneModelMapper;
import cn.cyberpeace.mp.behavior.mapper.SimulateMapper;
import cn.cyberpeace.mp.behavior.mapper.TaskMapper;
import cn.cyberpeace.mp.behavior.model.SceneModel;
import cn.cyberpeace.mp.behavior.model.SimulateModel;
import cn.cyberpeace.mp.behavior.model.TaskModel;
import cn.cyberpeace.mp.behavior.req.CreateSimulateReq;
import cn.cyberpeace.mp.behavior.req.SimulatePageReq;
import cn.cyberpeace.mp.behavior.res.*;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SimulateService {

	private final Map<String, Map<String, BehaviorTimer>> timerMap = Maps.newConcurrentMap();

	@Autowired
	private SimulateMapper simulateMapper;

	@Autowired
	private TaskMapper taskMapper;

	@Autowired
	private SceneModelMapper sceneModelMapper;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@PostConstruct
	public void init() {
		try {
			// 重启程序后恢复正在运行的仪表的定时任务
			recoverTimerTask();
		} catch (Exception e) {
			log.error("failed to recover timer task");
		}
	}

	public void createTask(CreateSimulateReq createSimulateReq) {

		List<TaskModel> taskModelList = new ArrayList<>();
		List<SimulateModel> simulateModelList = new ArrayList<>();
		List<SceneModel> sceneModelList = new ArrayList<>();

//		Map<String, String> nodeIpMap = createSimulateReq.getNodeList()
//				.stream()
//				.collect(Collectors.toMap(NodeBo::getNodeId, NodeBo::getNodeIp));
		for (NodeBo nodeBo : createSimulateReq.getNodeList()) {
			HumanCondition humanCondition = new HumanCondition().filterEqual(SceneModel::getNodeId, nodeBo.getNodeId());
			SceneModel sceneModel = sceneModelMapper.first(humanCondition);
			if (sceneModel == null) {
				break;
			}
			SimulateModel simulateModel = new SimulateModel();
			BeanUtils.copyProperties(createSimulateReq, simulateModel);
			simulateModel.setStatus(SimulateModel.StatusEnum.NOT_STARTED.getCode());
			simulateModel.setDelete(false);
			simulateModel.setOnline(false);
			simulateModel.setId(TextUtils.uuid());
			simulateModel.setIp(nodeBo.getNodeIp());
			simulateModel.setNodeId(nodeBo.getNodeId());
			simulateModel.setNodeName(nodeBo.getNodeName());
			simulateModelList.add(simulateModel);

			SceneModel newSceneModel = new SceneModel();
			newSceneModel.setSceneId(createSimulateReq.getSceneId());
			newSceneModel.setNodeId(nodeBo.getNodeId());
			newSceneModel.setInstance(true);
			newSceneModel.setNodeConfig(sceneModel.getNodeConfig());
			sceneModelList.add(newSceneModel);
			taskModelList.addAll(createTask(JSONObject.parseArray(sceneModel.getNodeConfig(), NodeUserBehavior.class), simulateModel.getId()));
		}

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

				taskMapper.bulkCreate(taskModelList);
				simulateMapper.bulkCreate(simulateModelList);
				sceneModelMapper.bulkCreate(sceneModelList);
			}
		});


	}

	public Integer countSimulate() {
		return simulateMapper.countSimulate();
	}

	public void runSimulate(String simulateId, Boolean dynamic) {
		SimulateModel simulateModel = simulateMapper.get(simulateId);
		if (simulateModel == null) {
			log.error("找不到对应仪表, 仪表id:[{}]", simulateId);
			throw new MpRuntimeException("找不到对应仪表");
		}
		if (!dynamic && !SimulateModel.StatusEnum.NOT_STARTED.getCode().equals(simulateModel.getStatus())) {
			log.error("行为仪表[{}]启动失败，状态错误", simulateId);
			throw new MpRuntimeException("行为仪表状态必须为未开始");
		}
		String queueName = simulateModel.getSceneId() + "_" + simulateModel.getNodeId();
		simulateModel.setStatus(SimulateModel.StatusEnum.RUNNING.getCode());
		timerMap.put(simulateId, new HashMap<>());
		List<TaskModel> taskModelList = taskMapper.list(new HumanCondition().filterEqual(TaskModel::getSimulateId, simulateId));
		for (TaskModel taskModel : taskModelList) {
			BehaviorTimer behaviorTimer = new BehaviorTimer();
			behaviorTimer.setTime(TimeUtils.format(taskModel.getRunTime()));
			behaviorTimer.setQueueName(queueName);
			BehaviorTimerTask behaviorTimerTask = new BehaviorTimerTask();
			behaviorTimerTask.setBehaviorTemplateId(taskModel.getNetworkBehaviorId());
			behaviorTimerTask.setBehaviorTemplateName(taskModel.getName());
			behaviorTimerTask.setRunBehaviorId(TextUtils.uuid());
			behaviorTimerTask.setBehaviorDownloadUrl(taskModel.getFileUrl());
			behaviorTimerTask.setParamList(JSONObject.parseArray(taskModel.getParams(), Param.class));
			behaviorTimerTask.setQueueName(queueName);
			behaviorTimer.scheduleAtFixedRate(behaviorTimerTask, TimeUtils.getNextDate(taskModel.getRunTime()), 24 * 60 * 60 * 1000L);
			timerMap.get(simulateId).put(taskModel.getId(), behaviorTimer);
		}

		simulateMapper.update(simulateModel);
	}

	public void stopSimulate(String simulateId) {
		SimulateModel simulateModel = simulateMapper.get(simulateId);
		if (simulateModel == null) {
			log.error("找不到对应仪表, 仪表id:[{}]", simulateId);
			throw new MpRuntimeException("找不到对应仪表");
		}
		timerMap.remove(simulateId);
		simulateModel.setStatus(SimulateModel.StatusEnum.STOPPED.getCode());
		simulateMapper.update(simulateModel);
	}

	public ListObjServiceResult<SimulatePageRes> page(SimulatePageReq pageReq) {
		HumanCondition humanCondition = new HumanCondition()
				.pagination(pageReq.getPage(), pageReq.getPageSize());
		if (StringUtils.isNotEmpty(pageReq.getName())) {
			humanCondition.filterIContains(SimulateModel::getSceneName, pageReq.getName());
		}
		if (pageReq.getStatus() != null) {
			humanCondition.filterEqual(SimulateModel::getStatus, pageReq.getStatus());
		}

		humanCondition.filterEqual(SimulateModel::getDelete, false);
		humanCondition.orderBy(SimulateModel::getCreateTime, true);

		List<SimulateModel> modelList = simulateMapper.list(humanCondition);
		int total = simulateMapper.total(humanCondition);
		List<SimulatePageRes> pageRes = modelList.stream().map(this::model2Page).collect(Collectors.toList());
		return new ListObjServiceResult(new ListObjResult(pageRes, total).asResult());
	}

	public void deleteTask(String sceneId) {
		List<SimulateModel> simulateModelList = simulateMapper.list(new HumanCondition().filterEqual(SimulateModel::getSceneId, sceneId));
		for (SimulateModel simulateModel : simulateModelList) {
			timerMap.remove(simulateModel.getId());
		}
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

				if (CollectionUtil.isNotEmpty(simulateModelList)) {
					sceneModelMapper.filterDelete(new HumanCondition().filterEqual(SceneModel::getSceneId, sceneId).filterEqual(SceneModel::getInstance, true));
					simulateMapper.filterDelete(new HumanCondition().filterEqual(SimulateModel::getSceneId, sceneId));
					taskMapper.filterDelete(new HumanCondition().filterIn(TaskModel::getSimulateId, simulateModelList.stream().map(SimulateModel::getId).toArray()));
				}
			}
		});
	}

	public List<String> getNameList() {
		return simulateMapper.getNameList();
	}

	public SimulateDetailRes detail(String simulateId) {
		SimulateModel simulateModel = simulateMapper.get(simulateId);
		if (simulateModel == null) {
			log.error("找不到对应仪表, 仪表id:[{}]", simulateId);
			throw new MpRuntimeException("找不到对应仪表");
		}
		SimulateDetailRes simulateDetailRes = new SimulateDetailRes();
		simulateDetailRes.setId(simulateModel.getId());
		simulateDetailRes.setIp(simulateModel.getIp());
		simulateDetailRes.setSceneName(simulateModel.getSceneName());
//		simulateDetailRes.setStartTime(TimeUtils.format(simulateModel.getStartTime()));
//		simulateDetailRes.setEndTime(TimeUtils.format(simulateModel.getEndTime()));
		List<TaskPageRes> taskPageResList = new ArrayList<>();
		List<TaskModel> taskModelList = taskMapper.list(new HumanCondition().filterEqual(TaskModel::getSimulateId, simulateId));
		for (TaskModel taskModel : taskModelList) {
			TaskPageRes taskPageRes = new TaskPageRes();
			BeanUtils.copyProperties(taskModel, taskPageRes);
			taskPageRes.setRunTime(TimeUtils.format(taskModel.getRunTime()));
			taskPageResList.add(taskPageRes);
		}
		simulateDetailRes.setBehaviorList(taskPageResList);
		return simulateDetailRes;
	}

	public void deleteTask(SimulateModel simulateModel) {
		if (simulateModel != null) {
			timerMap.remove(simulateModel.getId());
			taskMapper.filterDelete(new HumanCondition().filterEqual(TaskModel::getSimulateId, simulateModel.getId()));
		}
	}

	public List<StatusRes> getStatusList() {
		List<StatusRes> statusResList = new ArrayList<>();
		for (SimulateModel.StatusEnum statusEnum:SimulateModel.StatusEnum.values()) {
			StatusRes statusRes = new StatusRes();
			statusRes.setStatus(statusEnum.getCode());
			statusRes.setName(statusEnum.getDesc());
			statusResList.add(statusRes);
		}
		return statusResList;
	}

	private void recoverTimerTask() {
		List<String> simulateIdList = simulateMapper.selectIdByStatus(SimulateModel.StatusEnum.RUNNING.getCode());
		for (String simulateId : simulateIdList) {
			runSimulate(simulateId, true);
		}
	}

	private SimulatePageRes model2Page(SimulateModel simulateModel) {
		SimulatePageRes simulatePageRes = new SimulatePageRes();
		BeanUtils.copyProperties(simulateModel, simulatePageRes);
		return simulatePageRes;
	}

	public List<TaskModel> createTask(List<NodeUserBehavior> nodeUserBehaviorList, String simulateId) {
		List<TaskModel> taskModelList = new ArrayList<>();
		for (NodeUserBehavior nodeUserBehavior : nodeUserBehaviorList) {
			for (NodeNetworkBehavior nodeNetworkBehavior : nodeUserBehavior.getNetworkBehaviorList()) {
				TaskModel taskModel = new TaskModel();
				BeanUtils.copyProperties(nodeNetworkBehavior, taskModel);
				taskModel.setSimulateId(simulateId);
				taskModel.setNetworkBehaviorId(nodeNetworkBehavior.getNetworkBehaviorId());
				List<Param> params = nodeNetworkBehavior.getParamList();
//				for (Param param : params) {
//					if ("host".equals(param.getParam())) {
//						param.setValue(nodeIpMap.get(param.getValue()));
//					}
//				}
				taskModel.setParams(JSONObject.toJSONString(params));
				taskModel.setRunTime(TimeUtils.toLocalTime(nodeNetworkBehavior.getRunTime()));
				taskModel.setDelete(false);
				taskModelList.add(taskModel);
			}
		}
		return taskModelList;
	}

	public List<TimerRes> getTimerMap() {
		List<TimerRes> timerResList = new ArrayList<>();
		for (String simulateId : timerMap.keySet()) {
			TimerRes timerRes = new TimerRes();
			timerRes.setSimulateId(simulateId);
			List<TaskRes> taskResList = new ArrayList<>();
			for (String taskId : timerMap.get(simulateId).keySet()) {
				BehaviorTimer behaviorTimer = timerMap.get(simulateId).get(taskId);
				TaskRes taskRes = new TaskRes();
				taskRes.setTaskId(taskId);
				taskRes.setTime(behaviorTimer.getTime());
				taskRes.setQueueName(behaviorTimer.getQueueName());
				taskResList.add(taskRes);
			}
			timerRes.setTaskResList(taskResList);
			timerResList.add(timerRes);
		}
		return timerResList;
	}
}
