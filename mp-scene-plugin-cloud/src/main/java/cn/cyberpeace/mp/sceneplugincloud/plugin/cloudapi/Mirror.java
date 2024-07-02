package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CreateMirrorData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
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
}