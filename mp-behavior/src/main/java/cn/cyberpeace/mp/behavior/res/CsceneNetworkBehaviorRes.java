 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneNetworkBehaviorRes.java.
 // File size 609.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneNetworkBehaviorRes
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CsceneNetworkBehaviorRes {

	@ApiModelProperty("网络行为id")
	private String networkBehaviorId;

	@ApiModelProperty("网络行为名称")
	private String name;

	@ApiModelProperty("描述")
	private String description;

	@ApiModelProperty("行为类型")
	private Integer type;

	@ApiModelProperty("url")
	private String url;
}
