 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScreenRecordData.java.
 // File size 484.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName ScreenRecordData
 * @Description
 * @Author shgang
 * @Date 2022/12/5
 * @Version 1.0.0
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScreenRecordData {

    private String Url;
    private boolean success;
    private String requestId;
}
