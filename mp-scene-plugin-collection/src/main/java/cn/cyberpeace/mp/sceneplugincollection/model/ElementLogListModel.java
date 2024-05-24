package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_element_log_list")
public class ElementLogListModel extends BaseModel {

	@ApiModelProperty("场景id")
	private String sceneId;

	@ApiModelProperty("节点名称")
	private String nodeName;

	@ApiModelProperty("时间")
	private LocalDateTime time;

	@ApiModelProperty("应用名")
	private String appName;

	@ApiModelProperty("事件描述")
	private String eventDesc;

	@ApiModelProperty("级别")
	private Integer level;

	@ApiModelProperty("rule描述信息")
	private String description;

	@ApiModelProperty("分类")
	private String catagory;

}
