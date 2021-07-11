package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class EntityNode extends CloudClient {

    
    public EntityNodeCreateData create(EntityNodeCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<EntityNodeCreateData> result = cloudPost(CloudUrlConstant.ENTITY_NODE_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<EntityNodeCreateData>>() {
                }, true);
        return result.getData();
    }

    
    public CloudResponse<List<EntityNodeMibFileResult>> uploadAndParseMibFile(EntityNodeMibUploadData data,EntityNodeMibUploadParam param) {
        return cloudPost(CloudUrlConstant.ENTITY_NODE_MIB_UPLOAD_URL, data, param,
            new TypeReference<CloudResponse<List<EntityNodeMibFileResult>>>() {
            }, true);
    }

    
    public List<MibResultData> getMibNodeInfo(MibNodeQueryParam param) {
        try {
            CloudResponse<List<MibResultData>> result = cloudGet(CloudUrlConstant.ENTITY_NODE_MIB_NODE_INFO_URL, param,
                new TypeReference<CloudResponse<List<MibResultData>>>() {
                }, true);
            return result.getData();
        } catch (Exception e) {
            if(e.getMessage().startsWith("timed out")){
                throw new MpRuntimeException(BaseErrorCode.GATEWAY_TIME_OUT);
            }
            throw new MpRuntimeException(e.getMessage());
        }
    }

    
    public boolean delete(EntityNodeDeleteData deleteData) {
        heavyConcurrentWait();

        try {

            CloudResponse<Object> result = cloudDelete(CloudUrlConstant.ENTITY_NODE_DELETE_URL, deleteData, null,
                    new TypeReference<CloudResponse<Object>>() {
                    }, true);
            return true;
        } catch (Exception e) {
            if (super.excludeDeleteException(e)) {
                return true;
            }
            throw e;
        }
    }
}
