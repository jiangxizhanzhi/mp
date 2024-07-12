 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneNetworkBehaviorModel.java.
 // File size 702.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneNetworkBehaviorModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see NetworkBehaviorModel
 */
@Data
@Table("mp_cscene_network_behavior")
public class CsceneNetworkBehaviorModel extends NetworkBehaviorModel {

	@ApiModelProperty("节点id")
	private String nodeId;

	@ApiModelProperty("url配置")
	private String url;

	@ApiModelProperty("网络行为id")
	private String networkBehaviorId;

	@ApiModelProperty("场景id")
	private String sceneId;

}
