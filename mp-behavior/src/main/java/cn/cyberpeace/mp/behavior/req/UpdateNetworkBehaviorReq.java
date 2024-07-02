package cn.cyberpeace.mp.behavior.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UpdateNetworkBehaviorReq
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see AddNetworkBehaviorReq
 */
@Data
public class UpdateNetworkBehaviorReq extends AddNetworkBehaviorReq {

	@ApiModelProperty("id")
	private String id;
}
