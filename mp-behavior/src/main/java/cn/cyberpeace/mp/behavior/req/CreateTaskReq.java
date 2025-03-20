package cn.cyberpeace.mp.behavior.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CreateTaskReq implements Serializable {

	@ApiModelProperty("行为模板id")
	private String networkBehaviorId;

	@ApiModelProperty("任务名称")
	private String name;

	@ApiModelProperty("任务描述")
	private String description;

	@ApiModelProperty("文件下载地址")
	private String fileUrl;

	@ApiModelProperty("参数配置")
	private String params;

	@ApiModelProperty("目标服务")
	private String target;

	@ApiModelProperty("分类")
	private Integer type;

	@ApiModelProperty("步骤")
	private Integer step;

	@ApiModelProperty("运行时间")
	private LocalDateTime runTime;
}
