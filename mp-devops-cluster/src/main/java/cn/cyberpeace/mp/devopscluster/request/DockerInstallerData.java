package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.StartUpParameter;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DockerInstallerData implements Serializable {

    
    @ApiModelProperty(value = "启动参数")
    private List<StartUpParameter> startUpParameterList;

    @ApiModelProperty(value = "docker文件流",hidden = true)
    private MultipartFile dockerFile;

    @ApiModelProperty("docker文件地址")
    private String dockerUrl;

    public DockerInstallerData(List<StartUpParameter> startUpParameterList, String dockerUrl, String userFilename) {
        this.startUpParameterList = startUpParameterList;
        this.dockerUrl = dockerUrl;
        this.userFilename = userFilename;
    }

    
    @ApiModelProperty(value = "文件地址路径")
    private String file;

    
    @ApiModelProperty("临时文件ID")
    private String tmpdirUuid;

    @ApiModelProperty("用户文件名称")
    private String userFilename;
}
