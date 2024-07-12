 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Npc.java.
 // File size 2492.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CommonQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcCreateResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcGetData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * @ClassName Npc
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
public class Npc extends CloudClient {

    /**
     * Npc
     */
    public Npc() {
        super();
    }

    /**
     * create
     *
     * @param createData
     * @return
     */
    public NpcCreateResult create(NpcCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudPost(CloudUrlConstant.CREATE_NPC_URL, createData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        // todo
        return null;
    }

    /**
     * delete
     *
     * @param deleteData
     * @return boolean
     */
    public boolean delete(NpcDeleteData deleteData) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.DELETE_NPC_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    /**
     * getAllNpc
     *
     * @param limit
     * @param offset
     * @return
     */
    public List<NpcGetData> getAllNpc(Integer limit, Integer offset) {
        CommonQueryParam param = new CommonQueryParam();
        param.setLimit(limit);
        param.setOffset(offset);
        CloudResponse<List<NpcGetData>> result = cloudGet(CloudUrlConstant.GET_NPC_URL, param,
                new TypeReference<CloudResponse<List<NpcGetData>>>() {
                }, true);
        if (result != null && result.getData() != null && result.getData().size() > 0) {
            return result.getData();
        }
        return null;
    }
}
