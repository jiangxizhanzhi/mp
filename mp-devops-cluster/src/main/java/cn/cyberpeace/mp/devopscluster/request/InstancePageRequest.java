 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InstancePageRequest.java.
 // File size 779.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName InstancePageRequest
 * @Description 实例分页请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageRequest
 * @see Serializable
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InstancePageRequest extends BasePageRequest implements Serializable {

    /**
     * 主机名称/节点名称
     */
    @ApiModelProperty(value = "主机名称/节点名称")
    private String host;

    @ApiModelProperty(value = "IP地址")
    private String addr;
}
