package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Resource;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallerRequest extends BaseRequest {

    
    @ApiModelProperty("uuid")
    private String uuid;

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "描述")
    private String description;

    
    @ApiModelProperty(value = "logo文件流/文件地址",hidden = true)
    private MultipartFile logoFile;

    
    @ApiModelProperty(value = "图标")
    private String logo;

    
    @ApiModelProperty(value = "分类,以|分割")
    private String categories;

    
    @ApiModelProperty(value = "服务类型")
    private Integer deployType;

    @ApiModelProperty("docker参数")
    private DockerInstallerData dockerInstallerData;

    @ApiModelProperty(value = "windows服务资源流",hidden = true)
    private List<MultipartFile> multipartFiles;

    
    @ApiModelProperty(value = "资源列表")
    private List<Resource> resourceList;


}
