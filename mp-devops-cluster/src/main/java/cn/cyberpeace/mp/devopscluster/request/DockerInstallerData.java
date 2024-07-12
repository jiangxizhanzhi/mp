 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DockerInstallerData.java.
 // File size 1572.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.StartUpParameter;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DockerInstallerData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DockerInstallerData implements Serializable {

    /**
     * 启动参数（包装对象）
     */
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

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址路径")
    private String file;

    /**
     * 临时文件ID
     */
    @ApiModelProperty("临时文件ID")
    private String tmpdirUuid;

    @ApiModelProperty("用户文件名称")
    private String userFilename;
}
