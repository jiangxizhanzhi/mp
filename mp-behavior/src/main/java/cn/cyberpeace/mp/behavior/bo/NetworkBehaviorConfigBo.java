package cn.cyberpeace.mp.behavior.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName NetworkBehaviorConfigBo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class NetworkBehaviorConfigBo {

	@ApiModelProperty("网络行为id")
	private String networkBehaviorId;

	@ApiModelProperty("url配置")
	private String url;
}
