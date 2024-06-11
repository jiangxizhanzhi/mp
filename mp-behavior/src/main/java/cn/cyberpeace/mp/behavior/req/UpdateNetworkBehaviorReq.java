package cn.cyberpeace.mp.behavior.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateNetworkBehaviorReq extends AddNetworkBehaviorReq {

	@ApiModelProperty("id")
	private String id;
}
