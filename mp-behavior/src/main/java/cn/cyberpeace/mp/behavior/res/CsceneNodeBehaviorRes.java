 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneNodeBehaviorRes.java.
 // File size 850.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName CsceneNodeBehaviorRes
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CsceneNodeBehaviorRes {

	@ApiModelProperty("用户行为id")
	private String userBehaviorId;

	@ApiModelProperty("用户行为名称")
	private String name;

	@ApiModelProperty("描述")
	private String description;

	@ApiModelProperty("访问配置")
	private Integer type;

	@ApiModelProperty("开始时间")
	private LocalDateTime startTime;

	@ApiModelProperty("结束时间")
	private LocalDateTime endTime;

	@ApiModelProperty("网络行为信息")
	private List<CsceneNetworkBehaviorRes> networkList;
}
