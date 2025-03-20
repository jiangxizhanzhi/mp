package cn.cyberpeace.mp.behavior.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NodeUserBehavior implements Serializable {

	@ApiModelProperty("用户行为id")
	private String userBehaviorId;

	@ApiModelProperty("用户行为名称")
	private String userBehaviorName;

	@ApiModelProperty("开始时间")
	private String startTime;

	@ApiModelProperty("结束时间")
	private String endTime;

	@ApiModelProperty("行为数量")
	private Integer behaviorCount;

	@ApiModelProperty("网络行为列表")
	private List<NodeNetworkBehavior> networkBehaviorList;
}
