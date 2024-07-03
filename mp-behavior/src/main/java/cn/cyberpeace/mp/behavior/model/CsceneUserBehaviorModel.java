package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneUserBehaviorModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see UserBehaviorModel
 */
@Data
@Table("mp_cscene_user_behavior")
public class CsceneUserBehaviorModel extends UserBehaviorModel {

	@ApiModelProperty("节点id")
	private String nodeId;

	@ApiModelProperty("用户行为id")
	private String userBehaviorId;

	@ApiModelProperty("场景id")
	private String sceneId;

}
