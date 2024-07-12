 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImagesBuildRequest.java.
 // File size 984.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.dto.InstallerDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ImagesBuildRequest
 * @Description 镜像构建请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagesBuildRequest extends BaseRequest {


    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 镜像名称
     */
    @ApiModelProperty(value = "镜像名称")
    private String imageName;

    /**
     * 服务集合
     */
    @ApiModelProperty(value = "服务集合")
    private List<InstallerDTO> installerDTOList;


}
