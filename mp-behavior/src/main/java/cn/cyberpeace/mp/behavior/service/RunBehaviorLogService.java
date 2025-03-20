package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.behavior.bo.RunBehaviorBo;
import cn.cyberpeace.mp.behavior.mapper.NetworkBehaviorMapper;
import cn.cyberpeace.mp.behavior.mapper.RunBehaviorLogMapper;
import cn.cyberpeace.mp.behavior.mapper.SimulateMapper;
import cn.cyberpeace.mp.behavior.model.NetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.model.RunBehaviorLogModel;
import cn.cyberpeace.mp.behavior.model.SimulateModel;
import cn.cyberpeace.mp.behavior.req.ReportLogReq;
import cn.cyberpeace.mp.behavior.req.RunBehaviorReq;
import cn.cyberpeace.mp.behavior.res.RunBehaviorLogPage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RunBehaviorLogService {

	@Autowired
	private RunBehaviorLogMapper runBehaviorLogMapper;

	@Autowired
	private NetworkBehaviorMapper networkBehaviorMapper;

	@Autowired
	private SimulateMapper simulateMapper;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private RabbitAdmin rabbitAdmin;

	Cache<String, String> queueCache = CacheBuilder.newBuilder()
															.expireAfterWrite(1, TimeUnit.DAYS).build();

	public void runBehavior(RunBehaviorReq runBehaviorReq) {
		NetworkBehaviorModel behaviorTemplateModel = networkBehaviorMapper.get(runBehaviorReq.getBehaviorTemplateId());
		if (behaviorTemplateModel == null) {
			throw new MpRuntimeException("cannot find behaviorTemplateId:[" + runBehaviorReq.getBehaviorTemplateId() + "]");
		}

		RunBehaviorLogModel runModel = new RunBehaviorLogModel();
		runModel.setId(TextUtils.uuid());
		runModel.setParams(JSONObject.toJSONString(runBehaviorReq.getParamList()));
		runModel.setBehaviorTemplateId(runBehaviorReq.getBehaviorTemplateId());
		runModel.setQueueName(runBehaviorReq.getQueueName());
		runModel.setLogContent("");
		runModel.setBehaviorTemplateName(behaviorTemplateModel.getName());

		RunBehaviorBo runBehaviorBo = new RunBehaviorBo();
		BeanUtils.copyProperties(runBehaviorReq, runBehaviorBo);
		runBehaviorBo.setBehaviorDownloadUrl(behaviorTemplateModel.getFilePath());
		runBehaviorBo.setRunBehaviorId(runModel.getId());
		runBehaviorBo.setParamList(runBehaviorReq.getParamList());
		runBehaviorBo.setBehaviorName(behaviorTemplateModel.getFileName().substring(0,behaviorTemplateModel.getFileName().lastIndexOf(".zip")));
		SerializeConfig config = new SerializeConfig();
		config.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
		rabbitTemplate.convertAndSend(runBehaviorReq.getQueueName(), JSON.toJSONString(runBehaviorBo, config));
		runBehaviorLogMapper.create(runModel);
	}

	public void reportLog(ReportLogReq reportLogReq) {
		RunBehaviorLogModel runModel = runBehaviorLogMapper.get(reportLogReq.getBehaviorId());
		if (runModel == null) {
			log.error("cannot find runModel, id = [{}]", reportLogReq.getBehaviorId());
			throw new MpRuntimeException("找不到对应的行为记录");
		}

		runModel.setLogContent(reportLogReq.getLogContent());
		runBehaviorLogMapper.update(runModel);
	}

	public void register(String queueName) {
		queueCache.put(queueName, queueName);
		// 动态创建队列
		Queue queue = new Queue(queueName, false, false, true);
		rabbitAdmin.declareQueue(queue);
		String[] strings = queueName.split("_");
		SimulateModel simulateModel = simulateMapper.selectBySceneNode(strings[0], strings[1]);
		if (simulateModel != null) {
			simulateModel.setOnline(true);
			simulateMapper.update(simulateModel);
		}
	}

	public List<String> getQueueList() {
		return new ArrayList<>(queueCache.asMap().keySet());
	}

	public ListObjServiceResult<RunBehaviorLogPage> page(QueryParams pageReq) {
		HumanCondition humanCondition = new HumanCondition()
				.pagination(pageReq.getPage(), pageReq.getPageSize());

		if (StringUtils.isNotEmpty(pageReq.getSearch())) {
			humanCondition.filterIContains(RunBehaviorLogModel::getBehaviorTemplateName, pageReq.getSearch());
		}
		humanCondition.orderBy(NetworkBehaviorModel::getCreateTime, true);

		List<RunBehaviorLogModel> modelList = runBehaviorLogMapper.list(humanCondition);
		int total = runBehaviorLogMapper.total(humanCondition);
		List<RunBehaviorLogPage> networkBehaviorPageResList = modelList.stream().map(this::RunBehaviorLogModel2Page).collect(Collectors.toList());
		return new ListObjServiceResult(new ListObjResult(networkBehaviorPageResList, total).asResult());
	}

	private RunBehaviorLogPage RunBehaviorLogModel2Page(RunBehaviorLogModel runBehaviorLogModel) {
		RunBehaviorLogPage runBehaviorLogPage = new RunBehaviorLogPage();
		BeanUtils.copyProperties(runBehaviorLogModel, runBehaviorLogPage);
		runBehaviorLogPage.setBehaviorName(runBehaviorLogModel.getBehaviorTemplateName());
		runBehaviorLogPage.setRunTime(runBehaviorLogModel.getCreateTime());
		return runBehaviorLogPage;
	}
}
