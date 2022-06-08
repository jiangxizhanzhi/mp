package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CommonQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ConsoleInfoGetParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ConsoleInfoGetResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NameQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineAddPortData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineAddPortResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineGetData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineGetResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachinePauseData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineRestartData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineResumeData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineSuspendData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineUnPauseData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public class VirtualMachine extends CloudClient {

    public VirtualMachineCreateData create(VirtualMachineCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<VirtualMachineCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_CREATE_URL, createData,
                null, new TypeReference<CloudResponse<VirtualMachineCreateData>>() {
                }, true);
        return result.getData();
    }

    public VirtualMachineGetResult get(String instanceId) {
        CloudResponse<VirtualMachineGetResult> result = cloudGet(CloudUrlConstant.GET_INSTANCE_URL.replace("{instance_id}", instanceId),
                null, new TypeReference<>() {
                }, true);
        if (result.isSuccess() && result.getData() != null) {
            return result.getData();
        }
        return null;
    }

    public boolean delete(VirtualMachineDeleteData deleteData) {
        heavyConcurrentWait();

        try {
            CloudResponse<Object> result = cloudDelete(CloudUrlConstant.VIRTUAL_MACHINE_DELETE_URL, deleteData, null,
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

    public boolean deleteByName(String resourceName) {
        heavyConcurrentWait();

        NameQueryParam param = new NameQueryParam();
        param.setName(resourceName);
        param.setOffset(1);
        param.setLimit(1);
        CloudResponse<CloudListData<VirtualMachineGetData>> result = cloudGet(CloudUrlConstant.GET_ALL_INSTANCE_URL, param, new TypeReference<>() {
        }, true);
        if (result != null && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
            if (result.getData().getList().size() == 1) {
                String resourceId = result.getData().getList().get(0).getId();
                if (StringUtils.isNotEmpty(resourceId)) {
                    this.deleteInstanceById(resourceId);
                }
            } else {
                log.error("find more than one resource by name {} , resources is {}", resourceName, JsonCommon.stringify(result.getData().getList()));
            }
        }
        return true;
    }

    public boolean pause(String instanceId) {
        try {
            return doAction(instanceId, new VirtualMachinePauseData());
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                // 判断实例是否已是暂停状态
                var instance = this.get(instanceId);
                if (instance != null && VirtualMachineGetResult.Status.PAUSED.equalsIgnoreCase(instance.getStatus())) {
                    return true;
                }
            }
            throw e;
        }
    }

    public boolean unpause(String instanceId) {
        try {
            return doAction(instanceId, new VirtualMachineUnPauseData());
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                // 判断实例是否是活动状态
                var instance = this.get(instanceId);
                if (instance != null && VirtualMachineGetResult.Status.ACTIVE.equalsIgnoreCase(instance.getStatus())) {
                    return true;
                }
            }
            throw e;
        }
    }

    public boolean restart(String instanceId) {
        return doAction(instanceId, new VirtualMachineRestartData());
    }

    public boolean resume(String instanceId) {
        try {
            return doAction(instanceId, new VirtualMachineResumeData());
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                // 判断实例是否已是活动状态
                var instance = this.get(instanceId);
                if (instance != null && VirtualMachineGetResult.Status.ACTIVE.equalsIgnoreCase(instance.getStatus())) {
                    return true;
                }
            }
            throw e;
        }
    }

    public boolean suspend(String instanceId) {
        try {
            return doAction(instanceId, new VirtualMachineSuspendData());
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                // 判断实例是否已是挂起状态
                var instance = this.get(instanceId);
                if (instance != null && VirtualMachineGetResult.Status.SUSPENDED.equalsIgnoreCase(instance.getStatus())) {
                    return true;
                }
            }
            throw e;
        }
    }

    public ConsoleInfoGetResult getConsoleInfo(String instanceId, String hostname) {
        var param = new ConsoleInfoGetParam();
        param.setInstanceUuid(instanceId);
        param.setHostname(hostname);
        return getHttpClient().get(CloudUrlConstant.CONSOLE_INFO_GET_URL, param,
                ConsoleInfoGetResult.class);
    }

    private boolean doAction(String instanceId, CloudHttpData httpData) {
        try {
            cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_LIFE_CONTROL_URL.replace("{instance_id}", instanceId), httpData, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
            return true;
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }

    public List<VirtualMachineGetData> getAllInstance(Integer limit, Integer offset) {
        CommonQueryParam param = new CommonQueryParam();
        param.setLimit(limit);
        param.setOffset(offset);
        CloudResponse<CloudListData<VirtualMachineGetData>> result = cloudGet(CloudUrlConstant.GET_ALL_INSTANCE_URL, param, new TypeReference<>() {
        }, true);
        if (result != null && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
            return result.getData().getList();
        }
        return null;
    }

    public void deleteInstanceById(String instanceId) {
        heavyConcurrentWait();

        cloudDelete(CloudUrlConstant.DELETE_INSTANCE_URL.replace("{instance_id}", instanceId), null, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    public VirtualMachineAddPortResult addPort(String instanceId, VirtualMachineAddPortData port) {
        var response = cloudPost(CloudUrlConstant.ADD_VM_PORTS.replace("{instance_id}", instanceId), port, null, new TypeReference<CloudResponse<VirtualMachineAddPortResult>>() {
        }, true);
        return response.getData();
    }

    public void deletePort(String instanceId, String portId) {
        String url = CloudUrlConstant.DELETE_VM_PORTS.replace("{instance_id}", instanceId);
        url = url.replace("{port_id}", portId);
        try {
            cloudDelete(url, null, null, new TypeReference<CloudResponse<VirtualMachineAddPortResult>>() {
            }, true);
        } catch (Exception e) {
            if (super.excludeDeleteException(e)) {
                return;
            }
            throw e;
        }
    }
}
