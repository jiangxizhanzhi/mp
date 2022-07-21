package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ComInfoGetParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ComInfoGetResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CommonQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ConsoleInfoGetParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ConsoleInfoGetResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.SnapshotParams;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.SnapshotResultData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineGetData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachinePauseData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineRestartData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineResumeData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineSuspendData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineUnPauseData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class VirtualMachine extends CloudClient {

    public VirtualMachineCreateData create(VirtualMachineCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<VirtualMachineCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_CREATE_URL, createData,
                null, new TypeReference<CloudResponse<VirtualMachineCreateData>>() {
                }, true);
        return result.getData();
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

    public boolean pause(String instanceId) {
        return doAction(instanceId, new VirtualMachinePauseData());
    }

    public boolean unpause(String instanceId) {
        return doAction(instanceId, new VirtualMachineUnPauseData());
    }

    public boolean restart(String instanceId) {
        return doAction(instanceId, new VirtualMachineRestartData());
    }

    public boolean resume(String instanceId) {
        return doAction(instanceId, new VirtualMachineResumeData());
    }

    public boolean suspend(String instanceId) {
        return doAction(instanceId, new VirtualMachineSuspendData());
    }

    public ConsoleInfoGetResult getConsoleInfo(String instanceId, String hostname) {
        var param = new ConsoleInfoGetParam();
        param.setInstanceUuid(instanceId);
        param.setHostname(hostname);
        return getHttpClient().get(CloudUrlConstant.CONSOLE_INFO_GET_URL, param,
                ConsoleInfoGetResult.class);
    }

    public ComInfoGetResult getComInfo(String instanceId, String hostname) {
        var param = new ComInfoGetParam();
        param.setInstanceUuid(instanceId);
        param.setHostname(hostname);
        return getHttpClient().get(CloudUrlConstant.COM_INFO_GET_URL, param,
                ComInfoGetResult.class);
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

    public SnapshotResultData snapshot(String instanceId, String image, String snapshotName,String desc) {
        SnapshotParams params = new SnapshotParams();
        params.setDescription(StringUtils.EMPTY);
        params.setImageName(image);
        params.setInstanceUuid(instanceId);
        params.setName(snapshotName);
        params.setDescription(desc);
        var response = cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_SNAPSHOT_URL, params, null, new TypeReference<CloudResponse<SnapshotResultData>>() {
        }, true);
        return response.getData();
    }
}
