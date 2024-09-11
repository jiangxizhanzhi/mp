package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CsceneInstanceRecordRequest {

	@ApiModelProperty(value = "场景id")
	private List<String> ids;

	@ApiModelProperty(value = "开关")
	private Boolean button;
}
