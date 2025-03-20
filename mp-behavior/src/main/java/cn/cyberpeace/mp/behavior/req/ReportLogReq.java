package cn.cyberpeace.mp.behavior.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ReportLogReq implements Serializable {

	@ApiModelProperty(value = "日志内容", required = true)
	@NotNull
	private String logContent;

	@ApiModelProperty(value = "运行行为id", required = true)
	@NotBlank
	private String behaviorId;

}
