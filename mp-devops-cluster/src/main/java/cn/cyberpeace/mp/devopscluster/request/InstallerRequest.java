 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InstallerRequest.java.
 // File size 1741.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Resource;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @ClassName InstallerRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallerRequest extends BaseRequest {

    /**
     * UUID
     */
    @ApiModelProperty("uuid")
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * logo文件流
     */
    @ApiModelProperty(value = "logo文件流/文件地址",hidden = true)
    private MultipartFile logoFile;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String logo;

    /**
     * 分类
     */
    @ApiModelProperty(value = "分类,以|分割")
    private String categories;

    /**
     * 服务类型
     */
    @ApiModelProperty(value = "服务类型")
    private Integer deployType;

    @ApiModelProperty("docker参数")
    private DockerInstallerData dockerInstallerData;

    @ApiModelProperty(value = "windows服务资源流",hidden = true)
    private List<MultipartFile> multipartFiles;

    /**
     * 资源列表
     */
    @ApiModelProperty(value = "资源列表")
    private List<Resource> resourceList;


}
