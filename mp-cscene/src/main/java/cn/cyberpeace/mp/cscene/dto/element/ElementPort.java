 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ElementPort.java.
 // File size 792.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.element;

import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ElementPort
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/30 14:53
 * @Version 1.0.0
 */
@Data
public class ElementPort {

    @ApiModelProperty("ip地址")
    private String ip;

    @ApiModelProperty("name")
    private String name;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("接入模式")
    private List<AccessMode> accessModes;

    @ApiModelProperty("cloud接入模式")
    private List<CloudAccessMode> cloudAccessModes;

}
