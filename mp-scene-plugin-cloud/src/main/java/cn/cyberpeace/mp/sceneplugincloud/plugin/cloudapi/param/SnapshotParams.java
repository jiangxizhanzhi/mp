 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnapshotParams.java.
 // File size 735.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @ClassName SnapshotParams
 * @Description
 * @Author maojun
 * @Date 2022/7/8
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SnapshotParams implements CloudHttpData {
    private String imageName;
    private String deviceUUid;
    private String instanceUuid;
    private String name;
    private String description;
    private Map<String, Object> extInfo;
}
