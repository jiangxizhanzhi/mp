 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InstallerDTO.java.
 // File size 1524.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Resource;
import cn.cyberpeace.mp.devopscluster.request.BaseRequest;
import cn.cyberpeace.mp.devopscluster.request.DockerInstallerData;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName InstallerDTO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallerDTO extends BaseRequest {

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

    /**
     * 资源列表
     */
    @ApiModelProperty(value = "资源列表")
    private List<Resource> resourceList;


}
