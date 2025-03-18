package cn.cyberpeace.mp.behavior.res;

import cn.cyberpeace.mp.behavior.bo.Param;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BehaviorFileRes implements Serializable {

	@ApiModelProperty(value = "文件名称")
	private String fileName;

	@ApiModelProperty(value = "文件下载url")
	private String filePath;

	@ApiModelProperty(value = "参数配置")
	private List<Param> paramList;

	@ApiModelProperty("步骤")
	private Integer step;
}
