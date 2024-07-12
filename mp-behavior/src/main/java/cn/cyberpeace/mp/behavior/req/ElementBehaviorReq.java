 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ElementBehaviorReq.java.
 // File size 578.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.req;

import cn.cyberpeace.mp.behavior.bo.UserBehaviorConfigBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName ElementBehaviorReq
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class ElementBehaviorReq {

	@ApiModelProperty("节点id")
	@NotBlank
	private String nodeId;

	@ApiModelProperty("用户行为配置列表")
	private List<UserBehaviorConfigBo> userBos;
}
