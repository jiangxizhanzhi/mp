 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkBehaviorConfigBo.java.
 // File size 421.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName NetworkBehaviorConfigBo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class NetworkBehaviorConfigBo {

	@ApiModelProperty("网络行为id")
	private String networkBehaviorId;

	@ApiModelProperty("url配置")
	private String url;
}
