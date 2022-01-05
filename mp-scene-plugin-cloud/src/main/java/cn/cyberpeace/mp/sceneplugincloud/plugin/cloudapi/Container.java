package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

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

        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.CONTAINER_DELETE_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
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
}
