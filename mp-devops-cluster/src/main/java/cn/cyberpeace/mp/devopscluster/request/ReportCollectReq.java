package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReportCollectReq {

	@ApiModelProperty("上报内容")
	private String content;
}
