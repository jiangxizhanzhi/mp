package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_network_behavior")
public class NetworkBehaviorModel extends BaseModel {

	@ApiModelProperty("网络行为名称")
	private String name;

	@ApiModelProperty("网络行为描述")
	private String description;

	@ApiModelProperty("文件名称")
	private String fileName;

	@ApiModelProperty("文件路径")
	private String filePath;

	@ApiModelProperty("参数配置")
	private String params;

	@ApiModelProperty("目标服务")
	private String target;

	@ApiModelProperty("分类")
	private Integer type;

	@ApiModelProperty("步骤")
	private Integer step;

}
