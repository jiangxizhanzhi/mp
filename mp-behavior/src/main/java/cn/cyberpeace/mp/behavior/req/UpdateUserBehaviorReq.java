package cn.cyberpeace.mp.behavior.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateUserBehaviorReq extends AddUserBehaviorReq{

	@ApiModelProperty("id")
	private String id;
}
