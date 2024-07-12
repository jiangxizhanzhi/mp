 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ListPageInfoQueryParams.java.
 // File size 1110.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.service.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ListPageInfoQueryParams
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageRequest
 */
@Data
public class ListPageInfoQueryParams extends BasePageRequest {
    @ApiModelProperty(value = "代理名称")
    private String agentName;
    @ApiModelProperty(value = "场景id")
    private List<String> sceneIds;

    @ApiModelProperty(value = "节点id")
    private List<String> nodeIds;

    @ApiModelProperty(value = "rule级别集合 范围 0 ~ 15")
    private List<Integer> levels;

    @ApiModelProperty(value = "场景关闭情况下是否仍然查询数据，默认false，表示不查询")
    private Boolean fetchIfSceneInstanceShutdown = false;

    public ListPageInfoQueryParams() {

    }

    public ListPageInfoQueryParams(String agentName, List<String> sceneIds) {
        this.agentName = agentName;
        this.sceneIds = sceneIds;
    }
}
