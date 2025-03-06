package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.IdUtils;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.behavior.bo.RunBehaviorBo;
import cn.cyberpeace.mp.behavior.mapper.NetworkBehaviorTemplateMapper;
import cn.cyberpeace.mp.behavior.mapper.RunBehaviorLogMapper;
import cn.cyberpeace.mp.behavior.model.NetworkBehaviorTemplateModel;
import cn.cyberpeace.mp.behavior.model.RunBehaviorLogModel;
import cn.cyberpeace.mp.behavior.req.ReportLogReq;
import cn.cyberpeace.mp.behavior.req.RunBehaviorReq;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class RunBehaviorLogService {

	@Autowired
	private RunBehaviorLogMapper runBehaviorLogMapper;

	@Autowired
	private NetworkBehaviorTemplateMapper networkBehaviorTemplateMapper;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private RabbitAdmin rabbitAdmin;

	public void runBehavior(RunBehaviorReq runBehaviorReq) {
		NetworkBehaviorTemplateModel behaviorTemplateModel = networkBehaviorTemplateMapper.get(runBehaviorReq.getBehaviorTemplateId());
		if (behaviorTemplateModel == null) {
			throw new MpRuntimeException("cannot find behaviorTemplateId:[" + runBehaviorReq.getBehaviorTemplateId() + "]");
		}

		RunBehaviorLogModel runModel = new RunBehaviorLogModel();
		runModel.setId(IdUtils.globalId());
		runModel.setParams(JSONObject.toJSONString(runBehaviorReq.getParamList()));
		runModel.setBehaviorTemplateId(runBehaviorReq.getBehaviorTemplateId());
		runModel.setQueueName(runBehaviorReq.getQueueName());
		runModel.setLogName("");
		runModel.setLogUrl("");

		RunBehaviorBo runBehaviorBo = new RunBehaviorBo();
		BeanUtils.copyProperties(runBehaviorReq, runBehaviorBo);
		runBehaviorBo.setBehaviorDownloadUrl(behaviorTemplateModel.getFileUrl());
		runBehaviorBo.setRunBehaviorId(runModel.getId());
		createQueueAndSend(runBehaviorReq.getQueueName(), JSONObject.toJSONString(runBehaviorBo));
		runBehaviorLogMapper.create(runModel);
	}

	public void reportLog(ReportLogReq reportLogReq) {
		RunBehaviorLogModel runModel = runBehaviorLogMapper.get(reportLogReq.getBehaviorId());

		String suffix = FileUtil.getSuffix(reportLogReq.getLog().getOriginalFilename());
		String filePath = null;
		try {
			filePath = FsCommon.upload(reportLogReq.getLog().getInputStream(), suffix);
		} catch (IOException e) {
			log.error("file upload fail, behaviorId = [{}]", reportLogReq.getBehaviorId());
			return;
		}
		runModel.setLogName(reportLogReq.getLog().getOriginalFilename());
		runModel.setLogUrl(filePath);
		runBehaviorLogMapper.update(runModel);
	}

	public void register(String queueName) {

	}

	private void createQueueAndSend(String queueName, String message) {
		// 动态创建队列
		Queue queue = new Queue(queueName, false, false, true);
		rabbitAdmin.declareQueue(queue);

		// 发送消息到队列
		rabbitTemplate.convertAndSend(queueName, message);
	}
}
