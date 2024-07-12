 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DynamipsImageGetData.java.
 // File size 694.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.HttpData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName DynamipsImageGetData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HttpData
 */
@Data
public class DynamipsImageGetData implements HttpData {
    // 镜像ID
    private String id;
    // 镜像名
    private String name;
    // 镜像类型
    private String type;
    // 镜像文件
    @JsonProperty(value = "fileName")
    private String fileName;
    private String path;
    // 平台
    private String platform;
}