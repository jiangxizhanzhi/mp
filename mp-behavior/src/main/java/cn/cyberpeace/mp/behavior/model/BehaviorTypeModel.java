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
@Table("mp_behavior_type")
public class BehaviorTypeModel extends BaseModel {

	@ApiModelProperty("分类名称")
	private String name;

	@ApiModelProperty("分类编号")
	private Integer code;
}
