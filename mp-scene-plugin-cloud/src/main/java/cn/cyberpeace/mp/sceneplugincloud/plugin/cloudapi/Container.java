package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Container extends CloudClient {

    private static final String CONTAINER_ID = "{container_id}";


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

        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.CONTAINER_BATCH_DELETE_URL, deleteData, null,
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
            // 删除原来创建的端口映射 以及原来的接入连接
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

    @Deprecated
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

        // 只能上传已存在的文件夹中，若是文件夹不存在需要调用命令创建文件夹
        ContainerMkdirData dataMkdir = new ContainerMkdirData();
        dataMkdir.setCmd("mkdir");
        dataMkdir.setArgs(Arrays.asList("-p", dstPath));
        String mkdirUrl = String.format(CloudUrlConstant.CONTAINER_COMMAND_URL, containerId);
        cloudPost(mkdirUrl, dataMkdir, null, new TypeReference<CloudResponse<Object>>() {}, true);

        // 上传文件
        ContainerArchiveData data = new ContainerArchiveData();
        data.setFile(new File(filepath));
        data.setPath(dstPath);
        String requestUrl = String.format(CloudUrlConstant.CONTAINER_COPY_FILE_URL, containerId);
        CloudResponse<Object> result = cloudPost(requestUrl, data, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    private ContainerExecuteData getContainerExecuteDataByCommand(String command) {
        log.info("getContainerExecuteDataByCommand split command is [{}]", command);
        String[] s = command.split(" ");
        List<String> strings = Arrays.asList(s);
        String cmd = strings.remove(0);

        return new ContainerExecuteData(cmd, strings);
    }

    public ContainerExecuteResult executeCommand(String containerId, String command) {
        final ContainerExecuteData data = getContainerExecuteDataByCommand(command);

        String requestUrl = String.format(CloudUrlConstant.CONTAINER_EXECUTE_COMMAND_URL, containerId);
        CloudResponse<ContainerExecuteResult> result = new CloudResponse<ContainerExecuteResult>();
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
        final ContainerExecuteData data = getContainerExecuteDataByCommand(command);

        String requestUrl = String.format(CloudUrlConstant.CONTAINER_EXECUTE_COMMAND_URL, containerId);
        CloudResponse<ContainerExecuteResult> result = new CloudResponse<ContainerExecuteResult>();
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
        final ContainerExecuteData data = getContainerExecuteDataByCommand(command);
        String requestUrl = String.format(CloudUrlConstant.CONTAINER_EXECUTE_COMMAND_URL, containerId);
        CloudResponse<ContainerExecuteResult> result = new CloudResponse<ContainerExecuteResult>();
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
        return this.doAction(instanceId, "reboot");
    }

    public boolean pause(String instanceId) {
        return this.doAction(instanceId, "stop");
    }

    public boolean unPause(String instanceId) {
        return this.doAction(instanceId, "start");
    }

    public boolean start(String instanceId) {
        return this.doAction(instanceId, "start");
    }

    public boolean stop(String instanceId) {
        return this.doAction(instanceId, "stop");
    }

    public boolean suspend(String instanceId) {
        return this.doAction(instanceId, "suspend");
    }

    public boolean resume(String instanceId) {
        return this.doAction(instanceId, "resume");
    }

    private boolean doAction(String instanceId, String action) {
        try {
            cloudPost(CloudUrlConstant.CONTAINER_LIFE_CONTROL_URL.replace(CONTAINER_ID, instanceId)
                    .replace("{action}", action), null, null, new TypeReference<CloudResponse<Object>>() {
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

    public boolean portExists(String resourceId) {
        CloudResponse<Object> result = cloudGet(CloudUrlConstant.QUERY_PORTS.replace("{resource_id}", resourceId), null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        if (result != null && result.isSuccess()) {
            return true;
        }
        return false;
    }

    /**
     * 根据容器名称获取容器id
     *
     * @param name
     * @return
     */
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

    /**
     * 从容器中copy出文件
     *
     * @param containerId
     * @param filepath
     * @return
     */
    public HttpResult retrieveFile(String containerId, String filepath) {
        ContainerRetrieveParam data = new ContainerRetrieveParam();
        data.setContainerPath(filepath);
        String requestUrl = String.format(CloudUrlConstant.CONTAINER_COPY_FILE_URL, containerId);

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

    @Deprecated
    public MacvlanContainerCreateData createMacvlanContainer(MacvlanContainerCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<MacvlanContainerCreateData> result = cloudPost(CloudUrlConstant.MACVLAN_CONTAINER_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<MacvlanContainerCreateData>>() {
                }, true);
        return result.getData();
    }

    public String snapshot(String containerId, ContainerSnapshotData data) {
        var result = cloudPost(CloudUrlConstant.CONTAINER_SNAPSHOT_URL.replace(CONTAINER_ID,
                containerId), data, null, new TypeReference<CloudResponse<String>>() {
        }, true);
        return result.getData();
    }

    public void deleteSnapshot(String containerId, String snapshotId) {
        cloudPost(CloudUrlConstant.CONTAINER_DELETE_SNAPSHOT_URL.replace(CONTAINER_ID,
                containerId).replace("{image_id}", snapshotId), null, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    public ContainerSnapshotActionResult snapshotAction(String containerId, String snapshotId, String action) {
        var result = cloudPost(CloudUrlConstant.CONTAINER_SNAPSHOT_ACTION_URL.replace(CONTAINER_ID,
                containerId).replace("{image_id}", snapshotId).replace("{action}", action), null, null, new TypeReference<CloudResponse<ContainerSnapshotActionResult>>() {
        }, true);
        return result.getData();
    }

}
