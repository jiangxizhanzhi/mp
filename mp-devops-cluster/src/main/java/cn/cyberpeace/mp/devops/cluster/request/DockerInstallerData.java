package cn.cyberpeace.mp.devops.cluster.request;

import cn.cyberpeace.mp.devops.cluster.model.common.StartUpParameter;
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

    /**
     * 启动参数（包装对象）
     */
    @ApiModelProperty(value = "启动参数")
    private List<StartUpParameter> startUpParameterList;

    @ApiModelProperty("docker文件流")
    private MultipartFile dockerFile;

    @ApiModelProperty("docker文件地址")
    private String dockerUrl;
}
