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
