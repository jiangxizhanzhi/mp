package cn.cyberpeace.mp.behavior.req;


import cn.cyberpeace.mp.auth.request.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName NetworkPageReq
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see PageRequest
 */
@Data
public class NetworkPageReq extends PageRequest {

	@ApiModelProperty("行为类型")
	private Integer type;
}
