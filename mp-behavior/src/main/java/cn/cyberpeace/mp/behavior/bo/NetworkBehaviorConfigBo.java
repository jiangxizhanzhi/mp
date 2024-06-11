package cn.cyberpeace.mp.behavior.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NetworkBehaviorConfigBo {

	@ApiModelProperty("网络行为id")
	private String networkBehaviorId;

	@ApiModelProperty("url配置")
	private String url;
}
