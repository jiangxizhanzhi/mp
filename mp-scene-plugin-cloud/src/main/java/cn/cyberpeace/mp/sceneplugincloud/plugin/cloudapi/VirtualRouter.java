 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualRouter.java.
 // File size 4330.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * @ClassName VirtualRouter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
public class VirtualRouter extends CloudClient {

    private static final String ROUTER_ID = "{router_id}";


    /**
     * create
     *
     * @param createData
     * @return
     */
    public VirtualRouterCreateData create(VirtualRouterCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<VirtualRouterCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_ROUTER_CREATE_URL, createData,
                null, new TypeReference<CloudResponse<VirtualRouterCreateData>>() {
                }, true);
        return result.getData();
    }

    /**
     * delete
     *
     * @param deleteData
     * @return boolean
     */
    public boolean delete(VirtualRouterDeleteData deleteData) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.VIRTUAL_ROUTER_DELETE_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    /**
     * modify
     *
     * @param data
     * @param routerId
     * @return
     */
    public VirtualRouterModifyData modify(VirtualRouterModifyData data, String routerId) {
        heavyConcurrentWait();

        CloudResponse<VirtualRouterModifyData> result = cloudPatch(CloudUrlConstant.VIRTUAL_ROUTER_MODIFY_URL
                .replace(ROUTER_ID, routerId), data, null, new TypeReference<CloudResponse<VirtualRouterModifyData>>() {
        }, true);
        return result.getData();
    }

    /**
     * bindNetwork
     *
     * @param routerId
     * @param data
     * @return boolean
     */
    public boolean bindNetwork(String routerId, VirtualRouterBindNetworkData data) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudPost(CloudUrlConstant.VIRTUAL_ROUTER_BIND_NETWORK_URL.replace(ROUTER_ID, routerId),
                data, null, new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    /**
     * unbindNetwork
     *
     * @param routerId
     * @param data
     * @return boolean
     */
    public boolean unbindNetwork(String routerId, VirtualRouterUnbindNetworkData data) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.VIRTUAL_ROUTER_UNBIND_NETWORK_URL.replace(ROUTER_ID, routerId),
                data, null, new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    /**
     * getAllRouters
     *
     * @param limit
     * @param offset
     * @return
     */
    public List<VirtualRouterGetData> getAllRouters(Integer limit, Integer offset) {
        CommonQueryParam param = new CommonQueryParam();
        param.setLimit(limit);
        param.setOffset(offset);
        CloudResponse<CloudListData<VirtualRouterGetData>> result = cloudGet(CloudUrlConstant.GET_ALL_ROUTER_URL, param, new TypeReference<>() {
        }, true);
        if (result != null && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
            return result.getData().getList();
        }
        return null;
    }

    /**
     * deleteRouterById
     *
     * @param routerId
     */
    public void deleteRouterById(String routerId) {
        heavyConcurrentWait();

        VirtualRouterDeleteData deleteData = new VirtualRouterDeleteData();
        deleteData.setIds(List.of(routerId));
        cloudDelete(CloudUrlConstant.VIRTUAL_ROUTER_DELETE_URL, deleteData, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }
}
