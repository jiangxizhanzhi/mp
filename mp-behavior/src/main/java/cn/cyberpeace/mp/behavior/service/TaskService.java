package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.behavior.bo.Param;
import cn.cyberpeace.mp.behavior.bo.RunBehaviorBo;
import cn.cyberpeace.mp.behavior.mapper.RunBehaviorLogMapper;
import cn.cyberpeace.mp.behavior.mapper.SimulateMapper;
import cn.cyberpeace.mp.behavior.mapper.TaskMapper;
import cn.cyberpeace.mp.behavior.model.RunBehaviorLogModel;
import cn.cyberpeace.mp.behavior.model.SimulateModel;
import cn.cyberpeace.mp.behavior.model.TaskModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TaskService {

	@Autowired
	private TaskMapper taskMapper;

	@Autowired
	private SimulateMapper simulateMapper;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private RemoteService remoteService;

	public Integer countTask() {
		return taskMapper.countTask();
	}

	public void runTask(String taskId) {
		TaskModel taskModel = taskMapper.get(taskId);
		if (taskModel == null) {
			log.error("cannot find taskModel, taskId = [{}]", taskId);
			throw new MpRuntimeException("找不到对应的任务");
		}
		SimulateModel simulateModel = simulateMapper.get(taskModel.getSimulateId());
		Map<String, String> idIpMap = remoteService.getIdIpMap(simulateModel.getSceneId());
		List<Param> paramList = JSONObject.parseArray(taskModel.getParams(), Param.class);
		for (Param param : paramList) {
			if ("host".equals(param.getParam())) {
				param.setValue(idIpMap.get(param.getValue()));
			}
		}
		String queueName = simulateModel.getSceneId() + "_" + simulateModel.getNodeId();

		RunBehaviorLogModel runBehaviorLogModel = new RunBehaviorLogModel();
		runBehaviorLogModel.setLogContent("");
		runBehaviorLogModel.setBehaviorTemplateId(taskModel.getNetworkBehaviorId());
		runBehaviorLogModel.setBehaviorTemplateName(taskModel.getName());
		runBehaviorLogModel.setQueueName(queueName);
		runBehaviorLogModel.setParams(JSONObject.toJSONString(paramList));
		runBehaviorLogModel.setId(TextUtils.uuid());

		RunBehaviorBo runBehaviorBo = new RunBehaviorBo();
		runBehaviorBo.setBehaviorTemplateId(taskModel.getNetworkBehaviorId());
		runBehaviorBo.setBehaviorDownloadUrl(taskModel.getFileUrl());
		runBehaviorBo.setRunBehaviorId(runBehaviorLogModel.getId());
		runBehaviorBo.setParamList(paramList);
		runBehaviorBo.setBehaviorName(taskModel.getName());
		SerializeConfig config = new SerializeConfig();
		config.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);

		rabbitTemplate.convertAndSend(queueName, JSON.toJSONString(runBehaviorBo, config));
		RunBehaviorLogMapper runBehaviorLogMapper = BeanCommon.getBean(RunBehaviorLogMapper.class);
		runBehaviorLogMapper.create(runBehaviorLogModel);




	}
}
