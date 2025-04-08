package cn.cyberpeace.mp.behavior.bo;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.behavior.mapper.RunBehaviorLogMapper;
import cn.cyberpeace.mp.behavior.model.RunBehaviorLogModel;
import cn.cyberpeace.mp.behavior.service.RemoteService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorTimerTask extends TimerTask {

	//执行行为id
	private String runBehaviorId;

	//行为模板id
	private String behaviorTemplateId;

	//行为模板名称
	private String behaviorTemplateName;

	//行为包下载地址
	private String behaviorDownloadUrl;

	//行为参数列表
	private List<Param> paramList;

	//消息队列
	private String queueName;

	@Override
	public void run() {
		RabbitTemplate rabbitTemplate = BeanCommon.getBean(RabbitTemplate.class);
		RemoteService remoteService = BeanCommon.getBean(RemoteService.class);
		Map<String, String> idIpMap = remoteService.getIdIpMap(queueName.substring(0, queueName.indexOf("_")));
		for (Param param : paramList) {
			if ("host".equals(param.getParam())) {
				param.setValue(idIpMap.get(param.getValue()));
			}
		}
		RunBehaviorLogModel runBehaviorLogModel = new RunBehaviorLogModel();
		runBehaviorLogModel.setLogContent("");
		runBehaviorLogModel.setBehaviorTemplateId(behaviorTemplateId);
		runBehaviorLogModel.setBehaviorTemplateName(behaviorTemplateName);
		runBehaviorLogModel.setQueueName(queueName);
		runBehaviorLogModel.setParams(JSONObject.toJSONString(paramList));

		RunBehaviorBo runBehaviorBo = new RunBehaviorBo();
		runBehaviorBo.setBehaviorTemplateId(behaviorTemplateId);
		runBehaviorBo.setBehaviorDownloadUrl(behaviorDownloadUrl);
		runBehaviorBo.setRunBehaviorId(runBehaviorId);
		runBehaviorBo.setParamList(paramList);
		runBehaviorBo.setBehaviorName(behaviorTemplateName);
		SerializeConfig config = new SerializeConfig();
		config.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);

		rabbitTemplate.convertAndSend(queueName, JSON.toJSONString(runBehaviorBo, config));
		RunBehaviorLogMapper runBehaviorLogMapper = BeanCommon.getBean(RunBehaviorLogMapper.class);
		runBehaviorLogMapper.create(runBehaviorLogModel);
	}

}
