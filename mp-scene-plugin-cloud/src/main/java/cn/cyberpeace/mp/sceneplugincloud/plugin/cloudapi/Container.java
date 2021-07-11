package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CommonQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerAddPortData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerAddRouteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerArchiveData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeletePortData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerExecuteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerExecuteResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerFileGetData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerFileGetResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerGetData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerNewDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerPauseData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerResizeData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerRestartData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerResumeData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerRetrieveParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerStartData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerStopData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerSuspendData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerUnPauseData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleDeleteConnectionData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.MacvlanContainerCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NameQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.util.ForceReleaseResourceUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Container extends CloudClient {

    public ContainerCreateData create(ContainerCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<ContainerCreateData> result = cloudPost(CloudUrlConstant.CONTAINER_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<ContainerCreateData>>() {
                }, true);
        return result.getData();
    }

    public void resize(String containerId, ContainerResizeData resizeData) {
        heavyConcurrentWait();

        cloudPatch(CloudUrlConstant.CONTAINER_RESIZE_URL.replace("{container_id}", containerId), resizeData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
    }

    public boolean delete(ContainerDeleteData deleteData) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.CONTAINER_DELETE_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    public ContainerAddPortData addPort(ContainerAddPortData addPortData) {
        heavyConcurrentWait();

        CloudResponse<ContainerAddPortData> result = cloudPost(CloudUrlConstant.CONTAINER_ADD_PORT_URL, addPortData, null, new TypeReference<CloudResponse<ContainerAddPortData>>() {
        }, true);
        log.info("container add port result is {}", JsonCommon.stringify(result));
        return result.getData();
    }

    public boolean deletePort(ContainerDeletePortData deletePortData, CloudPluginSceneElementPortBO cloudPluginSceneElementPortBO) {
        heavyConcurrentWait();

        cloudPost(CloudUrlConstant.CONTAINER_DELETE_PORT_URL, deletePortData, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
        cloudDelete(CloudUrlConstant.DELETE_PORTS.replace("{port_id}", deletePortData.getPortId()), null, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
        if (cloudPluginSceneElementPortBO != null && CollectionUtils.isNotEmpty(cloudPluginSceneElementPortBO.getAccessModes())) {
            
            cloudPluginSceneElementPortBO.getAccessModes().forEach(a -> {
                if (a.getFwdId() != null) {
                    cloudDelete(CloudUrlConstant.DELETE_PORT_MAPPING.replace("{fwd_id}", a.getFwdId()), null, null, new TypeReference<CloudResponse<Object>>() {
                    }, true);
                }
                if (a.getConnectionId() != null) {
                    GuacamoleDeleteConnectionData guacamoleDeleteConnectionData = new GuacamoleDeleteConnectionData();
                    guacamoleDeleteConnectionData.setConnectionId(a.getConnectionId());
                    cloudDelete(CloudUrlConstant.DELETE_CONNECTION_URL, guacamoleDeleteConnectionData, null, new TypeReference<CloudResponse<Object>>() {
                    }, true);
                }
            });
        }
        return true;
    }

    public boolean forceDelete(ContainerDeleteData deleteData) {
        ForceReleaseResourceUtils.forceDeleteContainer(deleteData.getContainers());
        return true;
    }

    public boolean deleteByName(String resourceName) {
        heavyConcurrentWait();

        NameQueryParam param = new NameQueryParam();
        param.setName(resourceName);
        param.setOffset(1);
        param.setLimit(1);
        CloudResponse<CloudListData<ContainerGetData>> result = cloudGet(CloudUrlConstant.GET_ALL_CONTAINER_URL, param,
                new TypeReference<>() {
                }, true);
        if (result != null && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
            if (result.getData().getList().size() == 1) {
                String resourceId = result.getData().getList().get(0).getId();
                if (StringUtils.isNotEmpty(resourceId)) {
                    this.deleteContainerById(resourceId);
                }
            } else {
                log.error("find more than one resource by name {} , resources is {}", resourceName, JsonCommon.stringify(result.getData().getList()));
            }
        }
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
        var result = new CloudResponse<ContainerExecuteResult>();
        try {
            result = cloudPost(requestUrl, data, null,
                    new TypeReference<>() {
                    }, true);
        } catch (Exception e) {
            log.error("container execute command error containerId:{} command:{}", containerId, command, e);
        }
        return result.getData();
    }

    public ContainerExecuteResult executeCommand(String containerId, String command, Runnable action, Integer retryTimes) {
        var data = new ContainerExecuteData();
        data.setCommand(String.format("sh -c '%s'", command));
        data.setInteractive(false);
        data.setRun(true);
        var requestUrl = String.format(CloudUrlConstant.CONTAINER_EXECUTE_COMMAND_URL, containerId);
        var result = new CloudResponse<ContainerExecuteResult>();
        int times = retryTimes != null ? retryTimes : 1;
        for (int i = 0; i < times; i++) {
            if (i > 0) {
                if (action != null) {
                    action.run();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                result = cloudPost(requestUrl, data, null, new TypeReference<>() {
                }, true);
                break;
            } catch (Exception e) {
                log.error("container execute command error containerId:{} command:{}", containerId, command, e);
            }
        }
        return result.getData();
    }

    public ContainerExecuteResult executeCommand(String containerId, String command,String user) {
        var data = new ContainerExecuteData();
        data.setCommand(String.format("sh -c '%s'", command));
        data.setInteractive(false);
        data.setRun(true);
        data.setUser(user);
        var requestUrl = String.format(CloudUrlConstant.CONTAINER_EXECUTE_COMMAND_URL, containerId);
        var result = new CloudResponse<ContainerExecuteResult>();
        try {
            result = cloudPost(requestUrl, data, null,
                    new TypeReference<>() {
                    }, true);
        } catch (Exception e) {
            log.error("container execute command error containerId:{} command:{}", containerId, command, e);
        }
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

    public boolean start(String instanceId) {
        return this.doAction(instanceId, new ContainerStartData());
    }

    public boolean stop(String instanceId) {
        return this.doAction(instanceId, new ContainerStopData());
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
            log.info("change container state but it is already that state", ExceptionUtil.stacktraceToString(e));
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                return true;
            } else if (e.getMessage() != null && e.getMessage().contains("has stopped")) {
                return start(instanceId);
            }
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

    public boolean portExists(String resourceId) {
        CloudResponse<Object> result = cloudGet(CloudUrlConstant.QUERY_PORTS.replace("{resource_id}", resourceId), null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        if (result != null && result.isSuccess()) {
            return true;
        }
        return false;
    }

    
    public ContainerGetData getContainerByName(String name) {
        NameQueryParam param = new NameQueryParam();
        param.setName(name);
        param.setOffset(1);
        param.setLimit(1);
        CloudResponse<CloudListData<ContainerGetData>> result = cloudGet(CloudUrlConstant.GET_ALL_CONTAINER_URL, param,
                new TypeReference<>() {
                }, true);
        List<ContainerGetData> datas = Optional.of(result).map(CloudResponse::getData).map(CloudListData::getList).orElse(null);
        if (CollectionUtils.isEmpty(datas)) {
            log.error("find no resource by name {}", name);
        } else if (datas.size() == 1) {
            return datas.get(0);
        } else {
            log.error("find more than one resource by name {} , resources is {}", name, JsonCommon.stringify(result.getData().getList()));
        }

        return null;
    }

    
    public HttpResult retrieveFile(String containerId, String filepath) {
        var data = new ContainerRetrieveParam();
        data.setContainerPath(filepath);
        var requestUrl = String.format(CloudUrlConstant.CONTAINER_COPY_FILE_URL, containerId);

        return cloudDownload(requestUrl, data);
    }

    public ContainerFileGetResult retrieveFile(ContainerFileGetData data) {
        CloudResponse<ContainerFileGetResult> result = cloudPost(CloudUrlConstant.DOWNLOAD_CONTAINER_FILE, data, null,
                new TypeReference<CloudResponse<ContainerFileGetResult>>() {
                }, true);
        return result.getData();
    }

    public void addRoute(String containerId) {
        ContainerAddRouteData data = new ContainerAddRouteData();
        data.setContainerUuid(containerId);
        CloudResponse<Object> result = cloudPost(CloudUrlConstant.CONTAINER_ADD_ROUTE, data, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
    }

    public MacvlanContainerCreateData createMacvlanContainer(MacvlanContainerCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<MacvlanContainerCreateData> result = cloudPost(CloudUrlConstant.MACVLAN_CONTAINER_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<MacvlanContainerCreateData>>() {
                }, true);
        return result.getData();
    }

}
