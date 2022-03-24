package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerArchiveData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerExecuteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerExecuteResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerPauseData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerRestartData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerResumeData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerSuspendData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerUnPauseData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.List;

public class Container extends CloudClient {

    public ContainerCreateData create(ContainerCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<ContainerCreateData> result = cloudPost(CloudUrlConstant.CONTAINER_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<ContainerCreateData>>() {
                }, true);
        return result.getData();
    }

    public boolean delete(ContainerDeleteData deleteData) {
        heavyConcurrentWait();

        try {
            CloudResponse<Object> result = cloudDelete(CloudUrlConstant.CONTAINER_DELETE_URL, deleteData, null,
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

    public boolean copyFile(String containerId, String filepath, String dstPath) {
        var data = new ContainerArchiveData();
        data.setFile(new File(filepath));
        data.setDstPath(dstPath);
        var requestUrl = String.format(CloudUrlConstant.CONTAINER_COPY_FILE_URL, containerId);
        CloudResponse<Object> result = cloudPost(requestUrl, data, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    public ContainerExecuteResult executeCommand(String containerId, String command) {
        var data = new ContainerExecuteData();
        data.setCommand(String.format("sh -c '%s'", command));
        data.setInteractive(false);
        data.setRun(true);
        var requestUrl = String.format(CloudUrlConstant.CONTAINER_EXECUTE_COMMAND_URL, containerId);
        CloudResponse<ContainerExecuteResult> result = cloudPost(requestUrl, data, null,
                new TypeReference<CloudResponse<ContainerExecuteResult>>() {
                }, true);
        return result.getData();
    }

    public boolean restart(String instanceId) {
        return this.doAction(instanceId, new ContainerRestartData());
    }

    public boolean pause(String instanceId) {
        return this.doAction(instanceId, new ContainerPauseData());
    }

    public boolean unPause(String instanceId) {
        return this.doAction(instanceId, new ContainerUnPauseData());
    }

    public boolean suspend(String instanceId) {
        return this.doAction(instanceId, new ContainerSuspendData());
    }

    public boolean resume(String instanceId) {
        return this.doAction(instanceId, new ContainerResumeData());
    }

    private boolean doAction(String instanceId, CloudHttpData httpData) {
        try {
            cloudPost(CloudUrlConstant.CONTAINER_LIFE_CONTROL_URL.replace("{instance_id}", instanceId), httpData, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
            return true;
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }

    public List<ContainerGetData> getAllContainer(Integer limit, Integer offset) {
        CommonQueryParam param = new CommonQueryParam();
        param.setLimit(limit);
        param.setOffset(offset);
        CloudResponse<CloudListData<ContainerGetData>> result = cloudGet(CloudUrlConstant.GET_ALL_CONTAINER_URL, param,
                new TypeReference<CloudResponse<CloudListData<ContainerGetData>>>() {
        }, true);
        if (result != null && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
            return result.getData().getList();
        }
        return null;
    }

    public void deleteContainerById(String containerId) {
        heavyConcurrentWait();

        ContainerNewDeleteData data = new ContainerNewDeleteData();
        data.setForce(true);
        cloudDelete(CloudUrlConstant.DELETE_CONTAINER_URL.replace("{container_id}", containerId), data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }
}
