 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectedCsceneElementCommonBO.java.
 // File size 638.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.report.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CollectedCsceneElementCommonBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CollectedCsceneElementCommonBO<T> {
    @ApiModelProperty(value = "业务场景id")
    private String csceneId;
    @ApiModelProperty(value = "数据类型: node_access_user node_host_state")
    private String callAction;
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "数据体")
    private T data;
}
