package cn.cyberpeace.mp.behavior.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName AddNetworkBehaviorReq
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class AddNetworkBehaviorReq {

	@ApiModelProperty("名称")
	@NotNull(message = "名称不能为空")
	private String name;

	@ApiModelProperty("描述")
	@NotNull(message = "描述不能为空")
	private String description;

	@ApiModelProperty("行为类型")
	private Integer type;

	@ApiModelProperty("文件名称")
	private String fileName;

	@ApiModelProperty("导入文件地址")
	private String fileUrl;
}
