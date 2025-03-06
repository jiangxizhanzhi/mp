package cn.cyberpeace.mp.behavior.req;

import cn.cyberpeace.mp.behavior.bo.Param;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RunBehaviorReq implements Serializable {

	@ApiModelProperty("队列名称")
	private String queueName;

	@ApiModelProperty("网络行为模板id")
	private String behaviorTemplateId;

	@ApiModelProperty("参数配置")
	private List<Param> paramList;
}
