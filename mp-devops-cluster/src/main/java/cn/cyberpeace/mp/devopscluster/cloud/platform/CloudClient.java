package cn.cyberpeace.mp.devopscluster.cloud.platform;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ContainerSnapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.IsoDevice;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.*;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.Network;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.QuickCreateDevice;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.Snapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.VirtualNode;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.request.SnapshotPageRequest;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CloudClient {
    private CloudHttpClient client;

    private static final String VM_ID_TARGET_STR = "{vm_id}";

    private static final String CONTAINER_ID_TARGET_STR = "{container_id}";

    @Autowired
    public CloudClient(DevopsClusterConfig devopsClusterConfig) {
        this.client = new CloudHttpClient(devopsClusterConfig.getCpUrl(), 60000 * 10, 10);
    }

    public ListObjResult<Network> networkPage(NetworkPageParam param) {
        var result = client.cloudGet(CloudUrlConstant.MONITOR_NETWORKS, null, param, new TypeReference<CloudResponse<List<Network>>>() {
        });
        this.checkResponse(result);
        return new ListObjResult<>(result.getData(), result.getTotal());
    }

    public ListObjResult<VirtualNode> virtualNodePage(VirtualNodePageParam param) {
        var result = client.cloudGet(CloudUrlConstant.MONITOR_COMPUTE_NODES, null, param, new TypeReference<CloudResponse<List<VirtualNode>>>() {
        });
        this.checkResponse(result);
        return new ListObjResult<>(result.getData(), result.getTotal());
    }

    public void batchDeleteInstance(InstanceBatchDeleteData deleteData) {
        try {
            client.delete(CloudUrlConstant.BATCH_DELETE_INSTANCE, deleteData, null, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("batch delete instance error :{}", ExceptionUtils.getStackTrace(e));
        }
    }

    public Snapshot vmSnapshot(String vmId, String description) {
        SnapshotData data = new SnapshotData();
        data.setDescription(description);
        var result = client.cloudPost(CloudUrlConstant.VM_SNAPSHOT_URL.replace(VM_ID_TARGET_STR, vmId), data, null, new TypeReference<CloudResponse<Snapshot>>() {
        });
        this.checkResponse(result);
        return result.getData();
    }

    public String containerSnapshot(String containerId, String name) {
        SnapshotData data = new SnapshotData();
        data.setSnapshotName(name);
        var result = client.cloudPost(CloudUrlConstant.CONTAINER_SNAPSHOT_URL.replace(CONTAINER_ID_TARGET_STR, containerId), data, null, new TypeReference<CloudResponse<String>>() {
        });
        this.checkResponse(result);
        return result.getData();
    }

    public void vmSnapshotAction(String vmId, String snapshotId, String action) {
        var result = client.cloudPost(CloudUrlConstant.VM_SNAPSHOT_ACTION_URL.replace(VM_ID_TARGET_STR, vmId)
                        .replace("{snapshot_id}", snapshotId)
                        .replace("{action}", action),
                null, null, new TypeReference<>() {
                });
        this.checkResponse(result);
    }

    public void containerSnapshotAction(String containerId, String snapshotId, String action) {
        var result = client.cloudPost(CloudUrlConstant.CONTAINER_SNAPSHOT_ACTION_URL.replace(CONTAINER_ID_TARGET_STR, containerId)
                        .replace("{image_id}", snapshotId)
                        .replace("{action}", action),
                null, null, new TypeReference<>() {
                });
        this.checkResponse(result);
    }

    public void deleteVmSnapshot(String vmId, String snapshotId) {
        var result = client.cloudDelete(CloudUrlConstant.VM_DELETE_SNAPSHOT_URL.replace(VM_ID_TARGET_STR, vmId).replace("{snapshot_id}", snapshotId),
                null, null, new TypeReference<>() {
                });
        this.checkResponse(result);
    }

    public void deleteContainerSnapshot(String containerId, String snapshotId) {
        var result = client.cloudDelete(CloudUrlConstant.CONTAINER_DELETE_SNAPSHOT_URL.replace(CONTAINER_ID_TARGET_STR, containerId).replace("{image_id}", snapshotId),
                null, null, new TypeReference<>() {
                });
        this.checkResponse(result);
    }

    public IsoDevice getISODeviceByUuid(String uuid) {
        return client.get(CloudUrlConstant.GET_ISO_DEVICE_BY_UUID.replace("{uuid}", uuid), null, null, IsoDevice.class);
    }

    public ListObjResult<QuickCreateDevice> getQuickCreateDeviceList(QuickCreatePageParam param) {
        var result = client.cloudGet(CloudUrlConstant.QUICK_CREATE_DEVICE, null, param, new TypeReference<CloudResponse<QuickCreateDevice>>() {
        });
        return new ListObjResult<>(result.getItems(), result.getTotal());
    }

    public void quickCreate(QuickCreateData data) {
        var result = client.cloudPost(CloudUrlConstant.QUICK_CREATE_DEVICE, data, null, new TypeReference<>() {
        });
        this.checkResponse(result);
    }

    public CloudPlatformItemsPageResult.CloudPlatformItemData<ContainerSnapshot> listContainerSnapshot(SnapshotPageRequest request) {
        CloudPlatformItemsPageResult.CloudPlatformItemData<ContainerSnapshot> res = client.cloudGet(CloudUrlConstant.GET_CONTAINER_SNAPSHOT_LIST
                        .replace("{container_id}", request.getInstanceId()) + "?offset=" + request.getPage() + "&limit=" + request.getPerPage(),
                null, null, new TypeReference<CloudResponse<CloudPlatformItemsPageResult.CloudPlatformItemData<ContainerSnapshot>>>() {
                }).getData();
        return res;
    }

    public CloudPlatformItemsPageResult.CloudPlatformItemData<Snapshot> listVmSnapshot(SnapshotPageRequest request) {
        var res = client.cloudGet(CloudUrlConstant.GET_VM_SNAPSHOT_LIST
                        .replace("{vm_id}", request.getInstanceId()) + "?offset=" + request.getPage() + "&limit=" + request.getPerPage(),
                null, null, new TypeReference<CloudResponse<CloudPlatformItemsPageResult.CloudPlatformItemData<Snapshot>>>() {
                }).getData();
        return res;
    }

    private void checkResponse(CloudResponse<?> response) {
        if (!response.isSuccess()) {
            throw new MpRuntimeException(DevopsClusterErrorCode.CP_SERVICE_ERROR.format(response.getMessage()));
        }
    }


}
