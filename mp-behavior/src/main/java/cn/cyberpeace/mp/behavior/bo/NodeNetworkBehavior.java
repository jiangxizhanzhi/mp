package cn.cyberpeace.mp.behavior.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NodeNetworkBehavior implements Serializable {

	@ApiModelProperty("网络行为id")
	private String networkBehaviorId;

	@ApiModelProperty("行为名称")
	private String name;

	@ApiModelProperty("行为描述")
	private String description;

	@ApiModelProperty("文件下载地址")
	private String fileUrl;

	@ApiModelProperty("参数配置")
	private List<Param> paramList;

	@ApiModelProperty("目标服务")
	private String target;

	@ApiModelProperty("分类")
	private Integer type;

	@ApiModelProperty("步骤")
	private Integer step;

	@ApiModelProperty("运行时间")
	private String runTime;
}
