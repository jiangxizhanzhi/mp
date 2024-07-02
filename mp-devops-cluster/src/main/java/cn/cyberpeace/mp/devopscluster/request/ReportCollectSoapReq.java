package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ReportCollectSoapReq
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class ReportCollectSoapReq {

	@ApiModelProperty("上报内容")
	private String content;

	@ApiModelProperty("ip")
	private String ip;
}
