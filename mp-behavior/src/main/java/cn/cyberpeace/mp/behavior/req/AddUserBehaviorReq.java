 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AddUserBehaviorReq.java.
 // File size 830.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName AddUserBehaviorReq
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class AddUserBehaviorReq {

	@ApiModelProperty("名称")
	@NotNull(message = "名称不能为空")
	private String name;

	@ApiModelProperty("描述")
	@NotNull(message = "描述不能为空")
	private String description;

	@ApiModelProperty("访问类型")
	private Integer type;

	@ApiModelProperty("开始时间")
	private String startTime;

	@ApiModelProperty("结束时间")
	private String endTime;

	@ApiModelProperty("网络行为id列表")
	private List<String> networkIdList;
}
