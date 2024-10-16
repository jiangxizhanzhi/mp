 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualMachine.java.
 // File size 19310.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName VirtualMachine
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
@Slf4j
public class VirtualMachine extends CloudClient {

    private static final String INSTANCE_ID = "{instance_id}";
    private static final String CONFLICT = "Conflict";
    private static final String VM_ID = "{vm_id}";

    /**
     * create
     *
     * @param createData
     * @return
     */
    public VirtualMachineCreateData create(VirtualMachineCreateData createData) {
        heavyConcurrentWait();

        // 每个虚拟机都要 加一个网卡虚拟
//        VirtualNic virtualNic = new VirtualNic();
//        virtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getConfigNetId());
//        virtualNic.setCidr(CloudSingleton.cloudAppConfig.getConfigNetCidr());
//        virtualNic.setFixedIp(IpUtils.getConfigIp());
//        createData.getNetwork().add(virtualNic);

        CloudResponse<VirtualMachineCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_CREATE_URL, createData,
                null, new TypeReference<CloudResponse<VirtualMachineCreateData>>() {
                }, true);
        return result.getData();
    }

    /**
     * get
     *
     * @param instanceId
     * @return
     */
    public VirtualMachineGetResult get(String instanceId) {
        CloudResponse<VirtualMachineGetResult> result = cloudGet(CloudUrlConstant.GET_INSTANCE_URL.replace("{instance_id}", instanceId),
                null, new TypeReference<>() {
                }, true);
        if (result.isSuccess() && result.getData() != null) {
            return result.getData();
        }
        return null;
    }

    /**
     * delete
     *
     * @param deleteData
     * @return boolean
     */
    public boolean delete(VirtualMachineDeleteData deleteData) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.VIRTUAL_MACHINE_DELETE_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    /**
     * pause
     *
     * @param instanceId
     * @return boolean
     */
    public boolean pause(String instanceId) {
        try {
            return doAction(instanceId, "stop");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                // 判断实例是否已是暂停状态
                VirtualMachineGetResult instance = this.get(instanceId);
                if (instance != null && VirtualMachineGetResult.Status.PAUSED.equalsIgnoreCase(instance.getStatus())) {
                    return true;
                }
            }
            throw e;
        }
    }

    /**
     * unpause
     *
     * @param instanceId
     * @return boolean
     */
    public boolean unpause(String instanceId) {
        try {
            return doAction(instanceId, "start");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                // 判断实例是否是活动状态
                VirtualMachineGetResult instance = this.get(instanceId);
                if (instance != null && VirtualMachineGetResult.Status.ACTIVE.equalsIgnoreCase(instance.getStatus())) {
                    return true;
                }
            }
            throw e;
        }
    }

    /**
     * restart
     *
     * @param instanceId
     * @return boolean
     */
    public boolean restart(String instanceId) {
        try {
            return doAction(instanceId, "reboot");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                if (e.getMessage().contains("reboot") && e.getMessage().contains("stopped")) {
                    return start(instanceId);
                }
                return true;
            }
            throw e;
        }
    }

    /**
     * resume
     *
     * @param instanceId
     * @return boolean
     */
    public boolean resume(String instanceId) {
        try {
            return doAction(instanceId, "resume");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                // 判断实例是否已是活动状态
                VirtualMachineGetResult instance = this.get(instanceId);
                if (instance != null && VirtualMachineGetResult.Status.ACTIVE.equalsIgnoreCase(instance.getStatus())) {
                    return true;
                }
            }
            throw e;
        }
    }

    /**
     * suspend
     *
     * @param instanceId
     * @return boolean
     */
    public boolean suspend(String instanceId) {
        try {
            return doAction(instanceId, "suspend");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                // 判断实例是否已是挂起状态
                VirtualMachineGetResult instance = this.get(instanceId);
                if (instance != null && VirtualMachineGetResult.Status.SUSPENDED.equalsIgnoreCase(instance.getStatus())) {
                    return true;
                }
            }
            throw e;
        }
    }

    /**
     * start
     *
     * @param instanceId
     * @return boolean
     */
    public boolean start(String instanceId) {
        try{
            return doAction(instanceId, "start");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                // 判断实例是否是活动状态
                VirtualMachineGetResult instance = this.get(instanceId);
                if (instance != null && VirtualMachineGetResult.Status.ACTIVE.equalsIgnoreCase(instance.getStatus())) {
                    return true;
                }
            }
            throw e;
        }
    }

    /**
     * stop
     *
     * @param instanceId
     * @return boolean
     */
    public boolean stop(String instanceId) {
        try{
            return doAction(instanceId, "stop");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                // 判断实例是否已是暂停状态
                VirtualMachineGetResult instance = this.get(instanceId);
                if (instance != null && VirtualMachineGetResult.Status.SHUTOFF.equalsIgnoreCase(instance.getStatus())) {
                    return true;
                }
            }
            throw e;
        }
    }

    /**
     * resize
     *
     * @param instanceId
     * @param flavorId
     * @return
     */
    @Deprecated
    public String resize(String instanceId, String flavorId) {
        VirtualMachineResizeData resizeData = new VirtualMachineResizeData();
        resizeData.setFlavor(flavorId);
        resizeData.setInstanceId(instanceId);
        try {
            return instanceResize(resizeData);
        } catch (Exception e) {
            if (e.getMessage().contains("instances must change flavor!")) {
                return "";
            }
            throw e;
        }
    }

    /**
     * getConsoleInfo
     *
     * @param instanceId
     * @param hostname
     * @return
     */
    @Deprecated
    public ConsoleInfoGetResult getConsoleInfo(String instanceId, String hostname) {
        ConsoleInfoGetParam param = new ConsoleInfoGetParam();
        param.setInstanceUuid(instanceId);
        param.setHostname(hostname);
        return getHttpClient().get(CloudUrlConstant.CONSOLE_INFO_GET_URL, param,
                ConsoleInfoGetResult.class);
    }


    public GetVmInfoResult getVmInfo(String instanceId) {
        var result = cloudGet(CloudUrlConstant.GET_VM_INFO.replace("{vm_id}", instanceId), null, new TypeReference<CloudResponse<GetVmInfoResult>>() {
        }, true);
        return result.getData();
    }

    /**
     * getComInfo
     *
     * @param instanceId
     * @return
     */
    public ComInfoGetResult getComInfo(String instanceId) {
        var response = cloudGet(CloudUrlConstant.COM_INFO_GET_URL.replace(VM_ID, instanceId), null, new TypeReference<CloudResponse<ComInfoGetResult>>() {
        }, true);
        return response.getData();
    }

    /**
     * doAction
     *
     * @param vmId
     * @param action
     * @return boolean
     */
    private boolean doAction(String vmId, String action) {
        try {
            cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_LIFE_CONTROL_URL.replace(VM_ID, vmId)
                    .replace("{action}", action), null, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
            return true;
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }

    /**
     * instanceResize
     *
     * @param httpData
     * @return
     */
    @Deprecated
    private String instanceResize(VirtualMachineResizeData httpData) {
        try {
            CloudResponse<VirtualMachineInstanceResizeResult> response = cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_RESIZE_URL, httpData, null, new TypeReference<CloudResponse<VirtualMachineInstanceResizeResult>>() {
            }, true);
            return response.getData() == null ? "" : response.getData().getHostname();
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }

    /**
     * getAllInstance
     *
     * @param limit
     * @param offset
     * @return
     */
    public List<VirtualMachineGetData> getAllInstance(Integer limit, Integer offset) {
        CommonQueryParam param = new CommonQueryParam();
        param.setLimit(limit);
        param.setOffset(offset);
        CloudResponse<CloudListData<VirtualMachineGetData>> result = cloudGet(CloudUrlConstant.GET_VM_LIST_URL, param, new TypeReference<>() {
        }, true);
        if (result != null && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
            return result.getData().getList();
        }
        return null;
    }

    /**
     * deleteInstanceById
     *
     * @param instanceId
     */
    public void deleteInstanceById(String instanceId) {
        heavyConcurrentWait();

        cloudDelete(CloudUrlConstant.DELETE_INSTANCE_URL.replace("{vm_id}", instanceId), null, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    /**
     * addPort
     *
     * @param instanceId
     * @param port
     * @return
     */
    public VirtualMachineAddPortResult addPort(String instanceId, VirtualMachineAddPortData port) {
        CloudResponse<VirtualMachineAddPortResult> response = cloudPost(CloudUrlConstant.ADD_VM_PORTS.replace("{instance_id}", instanceId), port, null, new TypeReference<CloudResponse<VirtualMachineAddPortResult>>() {
        }, true);
        log.info("VirtualMachine add port response is {}", JsonCommon.stringify(response));
        return response.getData();
    }

    /**
     * deletePort
     *
     * @param instanceId
     * @param portId
     * @return boolean
     */
    public boolean deletePort(String instanceId, String portId) {
        String url = CloudUrlConstant.DELETE_VM_PORTS.replace("{instance_id}", instanceId);
        url = url.replace("{port_id}", portId);
        cloudDelete(url, null, null, new TypeReference<CloudResponse<VirtualMachineAddPortResult>>() {
        }, true);
        return true;
    }

    /**
     * snapshot
     *
     * @param instanceId
     * @param image
     * @param imageUUid
     * @param snapshotName
     * @param desc
     * @param extInfo
     * @return
     */
    public SnapshotResultData snapshot(String instanceId, String image, String imageUUid, String snapshotName, String desc, Map<String, Object> extInfo) {
        SnapshotParams params = new SnapshotParams();
        params.setImageName(image);
        params.setDeviceUUid(imageUUid);
        params.setInstanceUuid(instanceId);
        params.setName(snapshotName);
        params.setDescription(desc);
        params.setExtInfo(extInfo);
        var response = cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_SNAPSHOT_URL.replace(VM_ID, instanceId),
                params, null, new TypeReference<CloudResponse<SnapshotResultData>>() {
                }, true);
        return response.getData();
    }

    /**
     * deleteSnapshot
     *
     * @param instanceId
     * @param snapshotId
     */
    public void deleteSnapshot(String instanceId, String snapshotId) {
        cloudDelete(CloudUrlConstant.VIRTUAL_MACHINE_DELETE_SNAPSHOT_URL.replace(VM_ID, instanceId)
                .replace("{snapshot_id}", snapshotId), null, null, new TypeReference<>() {
        }, true);
    }

    /**
     * uploadFile
     *
     * @param data
     */
    @Deprecated
    public void uploadFile(VirtualMachineUploadFileData data) {
        CloudResponse<Object> response = cloudPost(CloudUrlConstant.SCP_FILE, data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }


    /**
     * snapshotAction
     *
     * @param instanceId
     * @param snapshotId
     * @param action
     */
    public void snapshotAction(String instanceId, String snapshotId, String action) {
        cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_SNAPSHOT_ACTION_URL.replace(VM_ID, instanceId)
                        .replace("{snapshot_id}", snapshotId)
                        .replace("{action}", action),
                null, null, new TypeReference<CloudResponse<SnapshotResultData>>() {
                }, true);
    }

    /**
     * deleteByName
     *
     * @param resourceName
     * @return boolean
     */
    public boolean deleteByName(String resourceName) {
        heavyConcurrentWait();

        NameQueryParam param = new NameQueryParam();
        param.setName(resourceName);
        param.setOffset(1);
        param.setLimit(1);
        CloudResponse<CloudListData<VirtualMachineGetData>> result = cloudGet(CloudUrlConstant.GET_VM_LIST_URL, param, new TypeReference<>() {
        }, true);
        if (result != null && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
            if (result.getData().getList().size() == 1) {
                String resourceId = result.getData().getList().get(0).getId();
                if (StringUtils.isNotEmpty(resourceId)) {
                    VirtualMachineDeleteData deleteData = new VirtualMachineDeleteData();
                    deleteData.setVms(List.of(resourceId));
                    this.delete(deleteData);
                }
            } else {
                log.error("find more than one resource by name {} , resources is {}", resourceName, JsonCommon.stringify(result.getData().getList()));
            }
        }
        return true;
    }

    /**
     * execCmd
     *
     * @param data
     */
    public void execCmd(VirtualMachineExecCmdData data) {
        try {
            cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_EXEC_CMD, data, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
        } catch (Exception e) {
            log.error("vm exc cmd error:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * modify
     *
     * @param data
     * @param vmId
     * @return boolean
     */
    public boolean modify(VirtualMachineModifyData data, String vmId) {
        var result = cloudPatch(CloudUrlConstant.VIRTUAL_MACHINE_MODIFY_URL.replace(VM_ID, vmId),
                data, null, new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }


    /**
     * execCommand
     *
     * @param data
     * @return
     */
    @Deprecated
    public VirtualMachineExecuteResult execCommand(VirtualMachineExecCommandData data) {
        VirtualMachineExecuteResult virtualMachineExecuteResult = getHttpClient().post(CloudUrlConstant.SSH_CMD, data, null, VirtualMachineExecuteResult.class);
        return virtualMachineExecuteResult;
    }

    /**
     * attachISO
     *
     * @param data
     */
    @Deprecated
    public void attachISO(VirtualMachineAttachISOData data) {
        cloudPost(CloudUrlConstant.ATTACH_ISO, data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    /**
     * attachVirtualUsbs
     *
     * @param data
     */
    @Deprecated
    public void attachVirtualUsbs(AttachVirtualUsbsData data) {
        cloudPost(CloudUrlConstant.ATTACH_VIRTUAL_USBS, data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    /**
     * deleteVirtualUsbs
     *
     * @param data
     */
    @Deprecated
    public void deleteVirtualUsbs(AttachVirtualUsbsData data) {
        cloudDelete(CloudUrlConstant.DELETE_VIRTUAL_USBS, data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    /**
     * 虚拟机挂载实体USB
     *
     * @param data
     */
    @Deprecated
    public void attachEntityUsbs(AttachEntityUsbsData data) {
        log.info("VirtualMachine.attachEntityUsbs begin. data:{}", data);
        //外设挂载失败，不影响场景启动
        try {
            CloudResponse<Object> response = cloudPost(CloudUrlConstant.ATTACH_ENTITY_USBS, data, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
            log.info("调用云平台挂载实体USB接口, response:{}", response);
        } catch (Exception e) {
            log.error("attachEntityUsbs error. e:{}", e);
        }
        log.info("VirtualMachine.attachEntityUsbs end.");
    }

    /**
     * 虚拟机卸载实体USB
     *
     * @param data
     */
    @Deprecated
    public void deleteEntityUsbs(AttachEntityUsbsData data) {
        log.info("VirtualMachine.deleteEntityUsbs begin. data:{}", data);
        CloudResponse<Object> response = cloudDelete(CloudUrlConstant.DELETE_ENTITY_USBS, data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
        log.info("调用云平台卸载实体USB接口, response:{}", response);
        log.info("VirtualMachine.deleteEntityUsbs end.");
    }

    /**
     * execControllerCmd
     *
     * @param cmd
     */
    @Deprecated
    public void execControllerCmd(String cmd) {
        ExecHostCommandData execHostCommandData = new ExecHostCommandData();
        execHostCommandData.setCmd(cmd);
        execHostCommandData.setHostname("controller");
        cloudPost(CloudUrlConstant.EXEC_HOST_COMMAND, execHostCommandData, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }




}
