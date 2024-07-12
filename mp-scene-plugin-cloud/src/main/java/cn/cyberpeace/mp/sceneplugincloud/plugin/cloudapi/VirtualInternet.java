 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualInternet.java.
 // File size 930.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualInternetData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @ClassName VirtualInternet
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
public class VirtualInternet extends CloudClient {

    /**
     * create
     *
     * @return
     */
    public VirtualInternetData create() {
        CloudResponse<VirtualInternetData> result = cloudPost(CloudUrlConstant.VIRTUAL_INTERNET_CREATE_URL, null,
                null, new TypeReference<>() {}, true);
        return result.getData();
    }
}
