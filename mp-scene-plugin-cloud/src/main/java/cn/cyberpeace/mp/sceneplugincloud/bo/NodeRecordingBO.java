 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NodeRecordingBO.java.
 // File size 1569.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName NodeRecordingBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@ApiModel(description = "场景接入录屏数据")
@Data
public class NodeRecordingBO {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty(value = "场景名称")
    private String sceneName;
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "节点名称")
    private String nodeName;
    @ApiModelProperty(value = "会话用户ID")
    private String user;
    @ApiModelProperty(value = "会话用户名")
    private String username;
    @ApiModelProperty(value = "会话协议")
    private String protocol;
    @ApiModelProperty(value = "录屏名称")
    private String name;
    @ApiModelProperty(value = "录屏文件名称")
    private String filename;
    @ApiModelProperty(value = "录屏文件地址")
    private String fileUrl;
    @ApiModelProperty(value = "录屏播放地址")
    private String playUrl;
    @ApiModelProperty(value = "录屏大小")
    private Integer filesize;
    @ApiModelProperty(value = "录屏时长")
    private Float duration;
    @ApiModelProperty(value = "录屏时间")
    private LocalDateTime time;
    @ApiModelProperty(value = "组织名称")
    private String orgName;
}
