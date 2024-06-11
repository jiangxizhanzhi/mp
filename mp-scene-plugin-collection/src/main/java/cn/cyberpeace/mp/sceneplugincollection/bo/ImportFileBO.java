package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportFileBO {

	@ApiModelProperty(value = "文件", required = true)
	private MultipartFile file;

	@ApiModelProperty(value = "场景id", required = true)
	private String sceneId;

	@ApiModelProperty(value = "导入数据类型(0：文件变更事件、1：注册表变更事件、2：日志事件、3：自定义采集事件、4：告警事件", required = true)
	private Integer type;
}
