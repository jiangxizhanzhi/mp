 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkTrafficMirrorRequest.java.
 // File size 982.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
  *@Desciption 新增流量镜像的参数请求
  *@Author zhangjin
  *@Date 11/23/21 3:07 下午
**/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkTrafficMirrorRequest {
    /**
     * 虚拟机id列表
     */
    @Builder.Default
    private List<String> vmInstanceIds = Lists.newArrayList();

    /**
     * 容器id列表
     */
    @Builder.Default
    private List<String> containerInstanceIds = Lists.newArrayList();

    /**
     * 流量镜像名称列表,创建镜像返回的Id
     */
    @Builder.Default
    private List<String> arkimeInstanceNames = Lists.newArrayList();

    /**
     * 场景ID
     */
    private String sceneId;

    private Boolean entity;
}
