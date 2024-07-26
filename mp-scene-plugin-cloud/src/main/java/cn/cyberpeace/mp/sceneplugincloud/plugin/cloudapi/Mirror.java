 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Mirror.java.
 // File size 1302.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CreateMirrorData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName Mirror
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
@Slf4j
public class Mirror extends CloudClient {
    /**
     * create
     *
     * @param createData
     * @return
     */
    public CreateMirrorData create(CreateMirrorData createData) {
        CloudResponse<CreateMirrorData> result = cloudPost(CloudUrlConstant.CREATE_MIRROR_URL, createData, null,
                new TypeReference<CloudResponse<CreateMirrorData>>() {
                }, true);
        return result.getData();
    }

    /**
     * delete
     *
     * @param mirrorId
     */
    public void delete(String mirrorId) {
        cloudDelete(CloudUrlConstant.DELETE_MIRROR_URL.replace("{mirror_id}", mirrorId), null, null, new TypeReference<>() {
        }, true);
    }

    public void createBridge(String name, String ip) {
        BridgeData data = new BridgeData();
        data.setIp(ip);
        data.setName(name);
        getHttpClient().post(CloudUrlConstant.CREATE_BRIDGE_MIRROR_URL, data, null, new TypeReference<>() {
        });
    }

    public void deleteBridge(String name, String ip) {
        BridgeData data = new BridgeData();
        data.setIp(ip);
        data.setName(name);
        getHttpClient().delete(CloudUrlConstant.DELETE_BRIDGE_MIRROR_URL, data, null, new TypeReference<>() {
        });
    }

    @Getter
    @Setter
    public static class BridgeData implements SnakeHttpData {
        private String name;
        private String ip;
    }
}