package cn.cyberpeace.mp.behavior.res;

import cn.cyberpeace.mp.behavior.model.NetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.model.UserBehaviorModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserBehaviorDetail extends UserBehaviorModel {

	@ApiModelProperty("关联的网络行为列表")
	private List<NetworkBehaviorModel> networkBehaviorModelList;
}
