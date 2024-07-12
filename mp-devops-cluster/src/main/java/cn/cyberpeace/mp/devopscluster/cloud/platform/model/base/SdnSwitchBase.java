 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SdnSwitchBase.java.
 // File size 548.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import cn.cyberpeace.mp.devopscluster.model.SwitchesCloudPlatformData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @ClassName SdnSwitch
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SdnSwitchBase extends BaseModel {

    private SdnSwitchBaseData data;
}
