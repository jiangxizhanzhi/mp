package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Table("mp_cscene_network_behavior")
public class CsceneNetworkBehaviorModel extends NetworkBehaviorModel {

	@ApiModelProperty("节点id")
	private String nodeId;

	@ApiModelProperty("url配置")
	private String url;

	@ApiModelProperty("网络行为id")
	private String networkBehaviorId;

}
