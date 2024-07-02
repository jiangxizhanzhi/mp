package cn.cyberpeace.mp.behavior.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UpdateUserBehaviorReq
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see AddUserBehaviorReq
 */
@Data
public class UpdateUserBehaviorReq extends AddUserBehaviorReq{

	@ApiModelProperty("id")
	private String id;
}
