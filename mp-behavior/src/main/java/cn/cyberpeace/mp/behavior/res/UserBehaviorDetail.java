 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserBehaviorDetail.java.
 // File size 696.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.res;

import cn.cyberpeace.mp.behavior.model.NetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.model.UserBehaviorModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName UserBehaviorDetail
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see UserBehaviorModel
 */
@Data
public class UserBehaviorDetail extends UserBehaviorModel {

	@ApiModelProperty("关联的网络行为列表")
	private List<NetworkBehaviorModel> networkBehaviorModelList;

	@ApiModelProperty("关联的网络行为id列表")
	private List<String> networkBehaviorIdList;
}
