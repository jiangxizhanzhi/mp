 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceUserCountModel.java.
 // File size 907.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CsceneInstanceUserCountModel
 * @Description 用户场景统计对象
 * @Author qiaokang
 * @Date 2023-06-20 14:26:35
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_cscene_instance_user_count")
public class CsceneInstanceUserCountModel extends BaseModel {

	/**
	 * 用户id
	 */
	@ApiModelProperty(value = "用户id")
	@DefaultStr
	private String userId;

	/**
	 * 场景启动数量
	 */
	@ApiModelProperty(value = "场景启动数量")
	private Integer count;


}
