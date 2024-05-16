package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;

@Slf4j
public class EntityNode extends CloudClient {

    /**
     * @description: 配置实装设备接口参数
     * @param createData
     * @return EntityNodeCreateData
     */
    public String create(EntityNodeCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<String> result = cloudPost(CloudUrlConstant.ENTITY_NODE_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<String>>() {
                }, true);
        return result.getData();
    }

    public boolean delete(EntityNodeDeleteData deleteData) {
        heavyConcurrentWait();

        try {

            CloudResponse<Object> result = cloudDelete(CloudUrlConstant.ENTITY_NODE_DELETE_URL, deleteData, null,
                    new TypeReference<CloudResponse<Object>>() {
                    }, true);
            return true;
        } catch (Exception e) {
            log.error("EntityNode delete error.", e);
            if (super.excludeDeleteException(e)) {
                return true;
            }
            throw e;
        }
    }

    public LogicalSwitchCreateData createLogicalSwitch(LogicalSwitchCreateData createData) {
        CloudResponse<LogicalSwitchCreateData> result = cloudPost(CloudUrlConstant.LOGICAL_SWITCH_CREATE_URL, createData,
                null, new TypeReference<CloudResponse<LogicalSwitchCreateData>>() {
                }, true);
        return result.getData();
    }

    public void deleteLogicalSwitch(LogicalSwitchDeleteData deleteData) {
        try {
            cloudDelete(CloudUrlConstant.LOGICAL_SWITCH_DELETE_URL, deleteData, null,
                    new TypeReference<CloudResponse<Object>>() {
                    }, true);
        } catch (Exception e) {
            log.error("delete logical switch error:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * @description: 实装上传文件
     * @param data
     * @return List<EntityNodeMibFileResult>
     */
    @Deprecated
    public CloudResponse<List<EntityNodeMibFileResult>> uploadAndParseMibFile(EntityNodeMibUploadData data,EntityNodeMibUploadParam param) {
        return cloudPost(CloudUrlConstant.ENTITY_NODE_MIB_UPLOAD_URL, data, param,
            new TypeReference<CloudResponse<List<EntityNodeMibFileResult>>>() {
            }, true);
    }

    /**
     * @description: 实装mib节点信息
     * @param param
     * @return List<MibResultData>
     */
    @Deprecated
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
}
