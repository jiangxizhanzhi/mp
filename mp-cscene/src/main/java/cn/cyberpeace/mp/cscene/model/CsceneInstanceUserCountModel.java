package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_cscene_instance_user_count")
public class CsceneInstanceUserCountModel extends BaseModel {

	
	@ApiModelProperty(value = "用户id")
	@DefaultStr
	private String userId;

	
	@ApiModelProperty(value = "场景启动数量")
	private Integer count;


}
