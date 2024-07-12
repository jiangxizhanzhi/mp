 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserBehaviorConfigBo.java.
 // File size 528.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName UserBehaviorConfigBo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class UserBehaviorConfigBo {

	@ApiModelProperty("用户行为id")
	@NotBlank
	private String userBehaviorId;

	@ApiModelProperty("网络行为配置")
	List<NetworkBehaviorConfigBo> networkBos;
}
