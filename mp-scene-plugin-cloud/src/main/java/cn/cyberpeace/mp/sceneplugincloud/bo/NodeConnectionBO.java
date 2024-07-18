// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name NodeConnectionBO.java.
// File size 1173.
// File create date Fri Jul 12 12:55:34 CST 2024.
// File update date Fri Jul 12 12:55:34 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName NodeConnectionBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@ApiModel(description = "场景接入连接数据")
@Data
public class NodeConnectionBO {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty(value = "场景名称")
    private String sceneName;
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "节点名称")
    private String nodeName;
    @ApiModelProperty(value = "会话用户")
    private String user;
    @ApiModelProperty(value = "会话协议")
    private String protocol;
    @ApiModelProperty(value = "连接id")
    private String connectionId;
    @ApiModelProperty(value = "连接url")
    private String connectionUrl;
    @ApiModelProperty(value = "连接url前缀")
    private String urlPrefix;
    @ApiModelProperty(value = "是否开启录屏")
    private Boolean connectionEnableRecording;
    @ApiModelProperty(value = "录屏名称")
    private String connectionRecordingName;

    /**
     * 获取URL前缀。
     * 该方法用于提取连接URL的前缀部分，直到最后一个斜杠为止。如果URL为空，则返回空字符串。
     * 这对于处理与特定URL相关的操作非常有用，例如构建相对路径或提取服务器地址。
     *
     * @return String 返回URL的前缀部分。如果URL为空，则返回空字符串。
     */
    public String getUrlPrefix() {
        // 检查连接URL是否为空
        if (StringUtils.isEmpty(connectionUrl)) {
            return "";
        } else {
            // 返回URL的前缀，即直到最后一个斜杠为止的字符串
            return connectionUrl.substring(0, connectionUrl.lastIndexOf("/"));
        }
    }

}
