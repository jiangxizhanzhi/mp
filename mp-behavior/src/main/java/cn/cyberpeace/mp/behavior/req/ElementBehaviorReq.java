package cn.cyberpeace.mp.behavior.req;

import cn.cyberpeace.mp.behavior.bo.UserBehaviorConfigBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName ElementBehaviorReq
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class ElementBehaviorReq {

	@ApiModelProperty("节点id")
	@NotBlank
	private String nodeId;

	@ApiModelProperty("用户行为配置列表")
	private List<UserBehaviorConfigBo> userBos;
}
