package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneNetworkBehaviorRes {

	@ApiModelProperty("网络行为id")
	private String networkBehaviorId;

	@ApiModelProperty("网络行为名称")
	private String name;

	@ApiModelProperty("描述")
	private String description;

	@ApiModelProperty("行为类型")
	private Integer type;

	@ApiModelProperty("url")
	private String url;
}
