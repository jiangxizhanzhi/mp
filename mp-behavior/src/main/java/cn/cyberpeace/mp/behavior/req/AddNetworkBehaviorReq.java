 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AddNetworkBehaviorReq.java.
 // File size 733.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.req;

import cn.cyberpeace.mp.behavior.bo.Param;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

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
	private String description;

	@ApiModelProperty("文件名称")
	private String fileName;

	@ApiModelProperty("导入文件地址")
	private String filePath;

	@ApiModelProperty("目标服务")
	private String target;

	@ApiModelProperty("分类")
	private Integer type;

	@ApiModelProperty("步骤")
	private Integer step;

	@ApiModelProperty("参数列表")
	private List<Param> paramList;
}
