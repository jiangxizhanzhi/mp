package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_behavior_scene")
public class SceneModel extends BaseModel {

	@ApiModelProperty("场景id")
	private String sceneId;

	@ApiModelProperty("节点id")
	private String nodeId;

	@ApiModelProperty("节点配置")
	private String nodeConfig;
}
