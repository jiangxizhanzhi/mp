// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name CloudPluginSceneModel.java.
// File size 943.
// File create date Fri Jul 12 12:55:23 CST 2024.
// File update date Fri Jul 12 12:55:23 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultLong;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CloudPluginSceneModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugincloud_config_network")
public class CloudPluginSceneConfigNetworkModel extends BaseModel {
    @DefaultStr
    private String scene;
    @DefaultStr
    private String cidr;
    @DefaultStr
    private String cidrIp;
    @DefaultLong
    private Long cidrIpLong;
    @DefaultStr
    private String gatewayIp;
    @DefaultStr
    private String nextHopIp;
    @DefaultStr
    private String snRouterId;
    @DefaultStr
    private String resourceId;
}
