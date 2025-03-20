package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.behavior.bo.*;
import cn.cyberpeace.mp.behavior.mapper.SceneModelMapper;
import cn.cyberpeace.mp.behavior.mapper.SimulateMapper;
import cn.cyberpeace.mp.behavior.mapper.TaskMapper;
import cn.cyberpeace.mp.behavior.model.SceneModel;
import cn.cyberpeace.mp.behavior.model.SimulateModel;
import cn.cyberpeace.mp.behavior.model.TaskModel;
import cn.cyberpeace.mp.behavior.req.CreateSimulateReq;
import cn.cyberpeace.mp.behavior.res.SimulatePageRes;
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

		for (NodeBo nodeBo : createSimulateReq.getNodeList()) {
			HumanCondition humanCondition = new HumanCondition().filterEqual(SceneModel::getSceneId, createSimulateReq.getSceneId())
																.filterEqual(SceneModel::getNodeId, nodeBo.getNodeId());
			SceneModel sceneModel = sceneModelMapper.first(humanCondition);
			if (sceneModel == null) {
				log.error("cannot find behavior config, sceneId:[{}], nodeId:[{}]", createSimulateReq.getSceneId(), nodeBo.getNodeId());
			}
			SimulateModel simulateModel = new SimulateModel();
			BeanUtils.copyProperties(createSimulateReq, simulateModel);
			simulateModel.setStatus(SimulateModel.StatusEnum.NOT_STARTED.getCode());
			simulateModel.setDelete(false);
			simulateModel.setOnline(false);
			simulateModel.setId(TextUtils.uuid());
			simulateModel.setIp("");

			List<TaskModel> taskModelList = new ArrayList<>();
			for (NodeUserBehavior nodeUserBehavior : JSONObject.parseArray(sceneModel.getNodeConfig(), NodeUserBehavior.class)) {
				for (NodeNetworkBehavior nodeNetworkBehavior : nodeUserBehavior.getNetworkBehaviorList()) {
					TaskModel taskModel = new TaskModel();
					BeanUtils.copyProperties(nodeNetworkBehavior, taskModel);
					taskModel.setSimulateId(simulateModel.getId());
					taskModel.setNetworkBehaviorId(nodeNetworkBehavior.getNetworkBehaviorId());
					taskModel.setParams(JSONObject.toJSONString(nodeNetworkBehavior.getParamList()));
					taskModel.setRunTime(TimeUtils.toLocalTime(nodeNetworkBehavior.getRunTime()));
					taskModel.setDelete(false);
					taskModelList.add(taskModel);
				}
			}
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

					taskMapper.bulkCreate(taskModelList);
					simulateMapper.create(simulateModel);
				}
			});
		}


	}

	public Integer countSimulate() {
		return simulateMapper.countSimulate();
	}

	public void runSimulate(String simulateId) {
		SimulateModel simulateModel = simulateMapper.get(simulateId);
		if (simulateModel == null) {
			log.error("找不到对应仪表, 仪表id:[{}]", simulateId);
			throw new MpRuntimeException("找不到对应仪表");
		}
		String queueName = simulateModel.getSceneId() + "_" + simulateModel.getNodeId();
		simulateModel.setStatus(SimulateModel.StatusEnum.RUNNING.getCode());
		timerMap.put(simulateId, new HashMap<>());
		List<TaskModel> taskModelList = taskMapper.list(new HumanCondition().filterEqual(TaskModel::getSimulateId, simulateId));
		for (TaskModel taskModel : taskModelList) {
			BehaviorTimer behaviorTimer = new BehaviorTimer();
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

	public void deleteSimulate(List<String> simulateIds) {
		simulateMapper.delete(simulateIds);
	}

	public ListObjServiceResult<SimulatePageRes> page(QueryParams pageReq) {
		HumanCondition humanCondition = new HumanCondition()
				.pagination(pageReq.getPage(), pageReq.getPageSize());

		if (StringUtils.isNotEmpty(pageReq.getSearch())) {
			humanCondition.filterIContains(SimulateModel::getSceneName, pageReq.getSearch());
		}
		humanCondition.filterEqual(SimulateModel::getDelete, false);
		humanCondition.orderBy(SimulateModel::getCreateTime, true);

		List<SimulateModel> modelList = simulateMapper.list(humanCondition);
		int total = simulateMapper.total(humanCondition);
		List<SimulatePageRes> pageRes = modelList.stream().map(this::model2Page).collect(Collectors.toList());
		return new ListObjServiceResult(new ListObjResult(pageRes, total).asResult());
	}

	public void startTask() {

	}

	private void recoverTimerTask() {
		List<String> simulateIdList = simulateMapper.selectIdByStatus(SimulateModel.StatusEnum.RUNNING.getCode());
		for (String simulateId : simulateIdList) {
			runSimulate(simulateId);
		}
	}

	private SimulatePageRes model2Page(SimulateModel simulateModel) {
		SimulatePageRes simulatePageRes = new SimulatePageRes();
		BeanUtils.copyProperties(simulateModel, simulatePageRes);
		return simulatePageRes;
	}
}
