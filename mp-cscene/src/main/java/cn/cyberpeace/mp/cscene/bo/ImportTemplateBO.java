package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportTemplateBO {
    @ApiModelProperty(value = "场景拓扑id")
    private String id;
    @ApiModelProperty(value = "区域id")
    private String areaId;
    @ApiModelProperty(value = "拓扑文件")
    private MultipartFile file;
}
