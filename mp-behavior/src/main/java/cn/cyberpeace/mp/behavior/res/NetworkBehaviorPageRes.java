package cn.cyberpeace.mp.behavior.res;

import cn.cyberpeace.mp.behavior.bo.Param;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NetworkBehaviorPageRes implements Serializable {

	@ApiModelProperty("id")
	private String id;

	@ApiModelProperty("网络行为名称")
	private String name;

	@ApiModelProperty("网络行为描述")
	private String description;

	@ApiModelProperty("目标服务")
	private String target;

	@ApiModelProperty("分类")
	private Integer type;

	@ApiModelProperty("步骤")
	private Integer step;

	@ApiModelProperty("参数配置")
	private List<Param> paramList;
}
