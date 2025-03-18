package cn.cyberpeace.mp.behavior.res;

import cn.cyberpeace.mp.behavior.req.AddNetworkBehaviorReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NetworkBehaviorRes extends AddNetworkBehaviorReq {

	@ApiModelProperty("id")
	private String id;
}
