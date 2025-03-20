package cn.cyberpeace.mp.behavior.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RunBehaviorBo implements Serializable {

	@ApiModelProperty("执行行为id")
	private String runBehaviorId;

	@ApiModelProperty("行为模板id")
	private String behaviorTemplateId;

	@ApiModelProperty("行为包下载地址")
	private String behaviorDownloadUrl;

	@ApiModelProperty("行为参数列表")
	private List<Param> paramList;

	@ApiModelProperty("行为名称")
	private String behaviorName;
}
