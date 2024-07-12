 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserBehaviorModel.java.
 // File size 1253.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ClassName UserBehaviorModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
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

	public enum UserType {

		ORDER(0, "顺序播放"),
		RANDOM(1, "随机播放");

		@Getter
		private Integer code;

		@Getter
		private String desc;

		UserType(Integer code, String desc) {
			this.code = code;
			this.desc = desc;
		}
	}
}
