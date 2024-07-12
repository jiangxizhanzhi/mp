 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneLinkStatusInfo.java.
 // File size 670.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.scene;

import cn.cyberpeace.mp.sceneplugincloud.bo.NodeLinkStatusBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName SceneLinkStatusInfo
 * @Description
 * @Author maojun
 * @Date 2022/7/8
 * @Version 1.0.0
 */
@ApiModel(description = "场景链路状态")
@Getter
@Setter
public class SceneLinkStatusInfo {
    @ApiModelProperty(value = "id")
    private String csceneId;
    @ApiModelProperty(value = "节点状态列表")
    private List<NodeLinkStatusBO> nodeLinkStatusList;
}