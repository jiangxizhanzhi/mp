 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Container.java.
 // File size 18598.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

 /**
 * @ClassName Container
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
@Slf4j
public class Container extends CloudClient {

    private static final String CONTAINER_ID = "{container_id}";

    public ContainerGetData get(String containerId) {
        CloudResponse<ContainerGetData> result = cloudGet(CloudUrlConstant.GET_CONTAINER_URL.replace("{container_id}", containerId), null, new TypeReference<CloudResponse<ContainerGetData>>() {
        }, true);
        return result.getData();
    }


    /**
     * create
     *
     * @param createData
     * @return
     */
    public ContainerCreateData create(ContainerCreateData createData) {
        heavyConcurrentWait();

        // 每个容器都要 加一个网卡
        VirtualNic virtualNic = new VirtualNic();
        virtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getConfigNetId());
        virtualNic.setCidr(CloudSingleton.cloudAppConfig.getConfigNetCidr());
        virtualNic.setFixedIp(IpUtils.getConfigIp());
        createData.getNetwork().add(virtualNic);

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
        for (int i = 0; i < 3; i++) {
            try {
                // 只能上传已存在的文件夹中，若是文件夹不存在需要调用命令创建文件夹
                ContainerMkdirData dataMkdir = new ContainerMkdirData();
                dataMkdir.setCmd("mkdir");
                dataMkdir.setArgs(Arrays.asList("-p", dstPath));
                String mkdirUrl = String.format(CloudUrlConstant.CONTAINER_EXECUTE_COMMAND_URL, containerId);
                cloudPost(mkdirUrl, dataMkdir, null, new TypeReference<CloudResponse<Object>>() {
                }, true);

                // 上传文件
                ContainerArchiveData data = new ContainerArchiveData();
                data.setFile(new File(filepath));
                data.setPath(dstPath);
                String requestUrl = String.format(CloudUrlConstant.CONTAINER_COPY_FILE_URL, containerId);
                CloudResponse<Object> result = cloudPost(requestUrl, data, null, new TypeReference<CloudResponse<Object>>() {
                }, true);
                return true;
            }catch (Exception e){
                if (i == 2) {
                    throw e;
                } else {
                    log.error("failed to copy file from {} to {},container id is {}", filepath, dstPath, containerId);
                }
            }
        }
        return true;
    }

    private ContainerExecuteData getContainerExecuteDataByCommand(String command) {
        log.info("getContainerExecuteDataByCommand split command is [{}]", command);
        String[] s = command.split(" ");
        List<String> strings = new ArrayList<>(List.of(s));
        String cmd = strings.remove(0);
        return new ContainerExecuteData(cmd, strings);
    }

    public ContainerExecuteResult executeCommand(String containerId, String command) {
        String fileName = TextUtils.uuid() + ".sh";
        File file = new File(fileName);
        log.info("executeCommand split containerId is [{}], command is [{}], tmp sh is [{}]", containerId, command, file.getAbsolutePath());
        try {
            FileUtils.writeStringToFile(file, command, Charset.forName("utf8"));
            this.copyFile(containerId, file.getAbsolutePath(), "/tmp/tmp_exec");
        } catch (IOException e) {
            log.error("executeCommand split containerId is " + containerId + ", command is " + command + ", tmp sh is " + file.getAbsolutePath() + "", e);
            throw new RuntimeException(e);
        } finally {
            file.delete();
        }

        ContainerExecuteData execCmd = new ContainerExecuteData();
        execCmd.setCmd("sh");
        execCmd.setArgs(Arrays.asList("/tmp/tmp_exec/" + fileName));
        var result = this.executeCommand(containerId, execCmd);

        // 兼容 30所 返回的数据结构
        result.setExitCode(0);
        result.setOutput(result.getResult());

        log.info("executeCommand result is {}", JsonCommon.stringify(result));
        return result;
    }

    private ContainerExecuteResult executeCommand(String containerId, ContainerExecuteData data) {
        String requestUrl = String.format(CloudUrlConstant.CONTAINER_EXECUTE_COMMAND_URL, containerId);
        CloudResponse<ContainerExecuteResult> result = new CloudResponse<ContainerExecuteResult>();
        for (int i = 0; i < 3; i++) {
            try {
                result = cloudPost(requestUrl, data, null,
                                   new TypeReference<>() {
                                   }, true);
                break;
            } catch (Exception e) {
                log.error("container execute command error containerId:{} command:{}", containerId, JsonCommon.stringify(data), e);
                if (i == 2) {
                    throw e;
                }
            }
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
        StringBuilder cmd = new StringBuilder();
        cmd.append("curl --location --request POST 'http://" + "10.4.1.58" + ":8001/api/mp/cscene/file/attachment_upload' \\")
                .append("--header 'clientSecret: 2112f4b855264ad7b1a4209737fc7836' \\")
                .append("--header 'clientId: 84261d495c414e' \\")
                .append("--form 'file=@\"" + data.getFilePath() + "\"'");

        var result = this.executeCommand(data.getZunContainerId(),cmd.toString());
        ContainerFileGetResult r = new ContainerFileGetResult();

        var matcher = Pattern.compile("\"inner_http_url\"\\s*:\\s*\"([^\"]+)\"").matcher(result.getOutput());
        matcher.find();
        r.setUrl(matcher.group(1));
        return r;
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
