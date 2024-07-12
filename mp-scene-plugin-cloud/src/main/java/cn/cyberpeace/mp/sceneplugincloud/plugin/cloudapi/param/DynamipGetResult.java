 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DynamipGetResult.java.
 // File size 656.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.HttpData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DynamipGetResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DynamipGetResult implements HttpData {
    @JsonProperty(value = "hostIp")
    private String hostIp;
    private String port;
    private String status;
}