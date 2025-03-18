package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserBehaviorPageRes implements Serializable {

	@ApiModelProperty("id")
	private String id;

	@ApiModelProperty("用户名称")
	private String name;

	@ApiModelProperty("行为数量")
	private Integer count;

	@ApiModelProperty("开始时间")
	private String startTime;

	@ApiModelProperty("结束时间")
	private String endTime;
}
