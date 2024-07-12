 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ContainerActionCloudPlatformModel.java.
 // File size 621.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model;

import lombok.*;

import java.io.Serializable;

/**
 * @ClassName ContainerActionCloudPlatformModel
 * @Description 容器创建模型
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ContainerActionCloudPlatformModel extends BaseModel implements Serializable {

    /**
     * 操作
     */
    private String action;

    /**
     * 状态
     */
    private Boolean waitForStatus;
}
