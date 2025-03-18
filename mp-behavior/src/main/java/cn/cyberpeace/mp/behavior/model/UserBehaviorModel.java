package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_user_behavior")
public class UserBehaviorModel extends BaseModel {

	@ApiModelProperty("名称")
	private String name;

	@ApiModelProperty("简介")
	private String description;

	@ApiModelProperty("开始时间")
	private LocalTime startTime;

	@ApiModelProperty("结束时间")
	private LocalTime endTime;

	@ApiModelProperty("行为列表")
	private String behaviors;
}
