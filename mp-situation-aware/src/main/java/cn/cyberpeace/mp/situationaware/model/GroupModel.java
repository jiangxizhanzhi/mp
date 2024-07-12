 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name GroupModel.java.
 // File size 573.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组是晒盘的抽象概念，创建组用于查询，分析流量（组的业务含义是场景）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupModel {

    private String groupId;

    /**
     * 物理机地址
     */
    private List<String> macIds;

    /**
     * 组的请求地址
     */
    private String url;
}
