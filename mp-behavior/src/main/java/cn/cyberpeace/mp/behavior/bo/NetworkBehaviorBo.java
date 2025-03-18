package cn.cyberpeace.mp.behavior.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class NetworkBehaviorBo implements Serializable {

	@ApiModelProperty("行为id")
	private String networkBehaviorId;

	@ApiModelProperty("访问时间")
	private String runTime;
}
