package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class VirtualDynamips extends CloudClient {
    /**
     * 创建dynamips
     *
     * @param createData
     * @return
     */
    public DynamipCreateResult create(DynamipCreateData createData) {
        heavyConcurrentWait();
        CloudResponse<DynamipCreateResult> result = cloudPost(CloudUrlConstant.DYNAMIPS_CREATE_URL, createData,
                null, new TypeReference<>() {
                }, true);
        return result.getData();
    }

    /**
     * 删除dynamip设备
     *
     * @param data
     * @return
     */
    public boolean delete(DeleteDynamipData data) {
        heavyConcurrentWait();
        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.DYNAMIPS_DELETE_URL, data,
                null, new TypeReference<>() {
                }, true);
        return true;
    }

    /**
     * 读取所有的dynamips镜像列表
     *
     * @return
     */
    public List<DynamipsImageGetData> getImages() {
        CloudResponse<CloudListData<DynamipsImageGetData>> result = cloudGet(CloudUrlConstant.DYNAMIPS_IMAGE_GET_URL, null, new TypeReference<>() {
        }, true);
        if (result != null && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
            return result.getData().getList();
        }
        return new ArrayList<>();
    }

    /**
     * 获取dynamips对象状态
     *
     * @param id 资源ID
     * @return
     */
    public DynamipGetResult getDynamips(String id) {
        CloudResponse<DynamipGetResult> result = cloudGet(CloudUrlConstant.DYNAMIPS_GET_URL + "?id=" + id, null, new TypeReference<>() {
        }, true);
        if (result != null && result.getData() != null) {
            return result.getData();
        }
        return null;
    }
}
