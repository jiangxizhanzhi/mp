package cn.cyberpeace.mp.behavior.req;


import cn.cyberpeace.mp.auth.request.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NetworkPageReq extends PageRequest {

	@ApiModelProperty("行为类型")
	private Integer type;
}
