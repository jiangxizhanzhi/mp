package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Table("mp_user_behavior")
public class UserBehaviorModel extends BaseModel {

	@ApiModelProperty("用户行为名称")
	private String name;

	@ApiModelProperty("用户行为描述")
	private String description;

	@ApiModelProperty("用户行为类型")
	private Integer type;

	@ApiModelProperty("关联的网络行为id列表")
	private String networkIds;

	@ApiModelProperty("开始时间")
	private LocalDateTime startTime;

	@ApiModelProperty("结束时间")
	private LocalDateTime endTime;

	public enum userType {

		ORDER(0, "order"),
		RANDOM(1, "random");

		@Getter
		private Integer code;

		@Getter
		private String desc;

		userType(Integer code, String desc) {
			this.code = code;
			this.desc = desc;
		}
	}
}
