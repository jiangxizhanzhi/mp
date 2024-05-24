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
}
