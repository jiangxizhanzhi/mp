 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UpdateNetworkBehaviorReq.java.
 // File size 401.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UpdateNetworkBehaviorReq
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see AddNetworkBehaviorReq
 */
@Data
public class UpdateNetworkBehaviorReq extends AddNetworkBehaviorReq {

	@ApiModelProperty("id")
	private String id;
}
