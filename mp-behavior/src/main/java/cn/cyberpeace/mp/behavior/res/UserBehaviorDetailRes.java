package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserBehaviorDetailRes implements Serializable {

	@ApiModelProperty("id")
	private String id;

	@ApiModelProperty("名称")
	private String name;

	@ApiModelProperty("简介")
	private String description;

	@ApiModelProperty("开始时间")
	private String startTime;

	@ApiModelProperty("结束时间")
	private String endTime;

	@ApiModelProperty("行为列表")
	private List<NetworkBehaviorBoRes> behaviorList;
}
