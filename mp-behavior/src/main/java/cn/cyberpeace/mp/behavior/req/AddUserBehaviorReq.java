package cn.cyberpeace.mp.behavior.req;

import cn.cyberpeace.mp.behavior.bo.NetworkBehaviorBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
public class AddUserBehaviorReq implements Serializable {

	@ApiModelProperty("名称")
	@NotEmpty(message = "名称不能为空")
	private String name;

	@ApiModelProperty("简介")
	private String description;

	@ApiModelProperty("开始时间")
	@NotEmpty(message = "开始时间不能为空")
	private String startTime;

	@ApiModelProperty("结束时间")
	@NotEmpty(message = "结束时间不能为空")
	private String endTime;

	@ApiModelProperty("行为列表")
	private List<NetworkBehaviorBo> behaviorList;
}
