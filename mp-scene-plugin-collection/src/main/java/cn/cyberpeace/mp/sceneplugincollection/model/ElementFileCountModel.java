package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_element_file_count")
public class ElementFileCountModel extends BaseModel {

	@ApiModelProperty("场景id")
	private String sceneId;

	@ApiModelProperty("时间")
	private String time;

	@ApiModelProperty("数量")
	private Integer count;

	@ApiModelProperty("group")
	private String group;

}
