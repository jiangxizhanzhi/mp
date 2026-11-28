package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.config.XdAppConfig;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@AllArgsConstructor
@Slf4j
public class VendorSecondCloudServiceImpl extends AbstractVendorCloudService {
    private final MultiCloudClient multiCloudClient;
    private final XdAppConfig xdAppConfig;
    private final VendorSecondAddressPool addressPool;
    private final ReentrantLock allocationRecoveryLock = new ReentrantLock();

    private static final ThreadLocal<VendorSecondClientContext> vendorClientContext = new ThreadLocal<>();
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";

    private static final String VM_CREATE_TEMPLATE = "{\n" +
            "    \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
            "    \"cores\": 4,\n" +
            "    \"disks\": [\n" +
            "        {\n" +
            "            \"id\": \"ide0\",\n" +
            "            \"preallocate\": 0,\n" +
            "            \"size_mb\": 204800,\n" +
            "            \"type\": \"derive_disk\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"image_id\": \"53460d60-72d8-48e4-8573-ac09314e2e60\",\n" +
            "    \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
            "    \"memory_mb\": 8192,\n" +
            "    \"name\": \"MP_CREATE_\",\n" +
            "    \"networks\": [\n" +
            "        {\n" +
            "            \"connect\": 1,\n" +
            "            \"host_tso\": 0,\n" +
            "            \"model\": \"virtio\",\n" +
            "            \"vif_id\": \"net0\",\n" +
            "            \"ip_info\": {\n" +
            "                \"ip_address\": \"\",\n" +
            "                \"netmask\": \"\",\n" +
            "                \"gateway\": \"\"\n" +
            "            }\n" +
            "        }\n" +
            "    ],\n" +
            "    \"power_on\": 1,\n" +
            "    \"sockets\": 2\n" +
            "}";

    @Override
    public Map<String, String> getHeaders() {
        return XdTwoHttpClient.getHeaders(vendorClientContext.get().getUrl(), vendorClientContext.get().getMethod(),
                vendorClientContext.get().getBody());
    }

    @Override
    public HttpClient getClient() {
        return multiCloudClient.getClient(getVendor());
    }

    @Override
    public List<String> listAllDevices() {

        try {
            int index = 0;
            String url = "/janus/20180725/images";
            vendorClientContext.set(new VendorSecondClientContext(url, METHOD_GET, null));
            JSONObject jsonObject = JSONObject.parseObject(getClient().get(url, null, getHeaders(), 30000).getResult());
            List<String> images = new ArrayList<>();

            for (Object o : jsonObject.getJSONObject("data").getJSONArray("data")) {
                JSONObject image = (JSONObject) o;
                boolean isActive = "active".equals(image.getString("status"));
                boolean isWin = image.getString("os_type").contains("win");
                String id = image.getString("id");
                String name = image.getString("name");

                if (isActive) {
                    JSONObject device = JSONObject.parseObject(DEVICE_TEMPLATE);
                    device.put("id", index++);
                    device.put("vendor", getVendor());
                    device.put("uuid", id);
                    device.put("name", name);
                    device.put("image", name);
                    device.put("description", name);
                    device.put("default_launch_image_name", name);
                    if (!isWin) {
                        device.put("second_category", 2);
                        device.put("l3_category", 306);
                        device.getJSONObject("ext_system_info")
                                .put("parent_system_type", 2);
                        device.getJSONObject("ext_system_info")
                                .put("system_type", 306);
                    } else {
                        device.put("second_category", 1);
                        device.put("l3_category", 106);
                        device.getJSONObject("ext_system_info")
                                .put("parent_system_type", 1);
                        device.getJSONObject("ext_system_info")
                                .put("system_type", 106);
                    }
                    device.getJSONObject("ext_vm").put("access_protocols", new ArrayList<>());
                    device.getJSONObject("ext_image_info").put("uuid", id);
                    device.getJSONObject("ext_image_info").put("base_image_name", name);

                    images.add(device.toJSONString());
                }
            }
            return images;

        } catch (Exception e) {
            log.error("获取厂商靶标列表异常: " + e.getMessage(), e);
            return Collections.emptyList();
        } finally {
            vendorClientContext.remove();
        }
    }

    @Override
    public int getVendor() {
        return 2;
    }

    @Override
    public String createVm(String imageUuid) {
        ManagedVmCreationResult result = createManagedVm(imageUuid, null, null, null, IP_THREADLOCAL.get());
        if (!result.isReady()) throw new MpRuntimeException(result.pendingMessage());
        return result.getResourceId();
    }

    @Override
    public ManagedVmCreationResult createManagedVm(String imageUuid, String sceneId, String elementId,
                                                    String portId, String requestedIp) {
        try {
            JSONObject createVmObject = JSONObject.parseObject(VM_CREATE_TEMPLATE);
            createVmObject.put("image_id", imageUuid);
            JSONObject network = createVmObject.getJSONArray("networks").getJSONObject(0);
            network.put("device_id", xdAppConfig.getXdTwoDeviceId());
            network.put("port_id", xdAppConfig.getXdTwoPortId());

            JSONObject image = getTargetImage(imageUuid);

            String azId = ((JSONObject) image.getJSONArray("azs").get(0)).getString("id");
            createVmObject.put("az_id", azId);
            createVmObject.put("storage_id", ((JSONObject) image.getJSONArray("disks").get(0)).getString("storage_id"));

            List<JSONObject> vmDisks = new ArrayList<>();
            for (Object disks : image.getJSONArray("disks")) {
                JSONObject disk = (JSONObject) disks;

                JSONObject newDisk = JSONObject.parseObject(" {\n" +
                        "            \"id\": \"ide0\",\n" +
                        "            \"preallocate\": 0,\n" +
                        "            \"size_mb\": 204800,\n" +
                        "            \"type\": \"derive_disk\"\n" +
                        "        }");
                newDisk.put("id", disk.getString("id"));
                newDisk.put("preallocate", disk.getString("preallocate").equals("off") ? 0 : 1);
                newDisk.put("size_mb", (int) (disk.getDouble("size_mb") * 10));

                vmDisks.add(newDisk);
            }
            createVmObject.put("disks", vmDisks);

            // Image lookup and payload preparation finish before reserving an address.
            VendorSecondInventory inventory = readInventorySafely();
            addressPool.prepare(inventory);
            String owner = StringUtils.isNotBlank(elementId) ? elementId : TextUtils.uuid();
            while (true) {
                String requestName = "MP_" + TextUtils.uuid();
                VendorSecondAddressPool.Lease lease = reserveWithRecovery(sceneId, elementId, portId, requestedIp, owner, requestName);
                StringHttpData body;
                try {
                    createVmObject.put("name", requestName);
                    JSONObject ipInfo = network.getJSONObject("ip_info");
                    ipInfo.put("ip_address", lease.getIp());
                    ipInfo.put("netmask", xdAppConfig.getXdTwoIpNetmask());
                    ipInfo.put("gateway", xdAppConfig.getXdTwoIpGateway());
                    body = new StringHttpData(createVmObject.toJSONString());
                } catch (RuntimeException e) {
                    addressPool.rejectBeforeCreation(lease, false);
                    throw e;
                }
                String uuid, taskId;
                try {
                    String url = "/janus/20180725/servers";
                    vendorClientContext.set(new VendorSecondClientContext(url, METHOD_POST, body));
                    HttpResult response = getClient().post(url, body, null, getHeaders(), 30000);
                    JSONObject result = response == null ? null : JSONObject.parseObject(response.getResult());
                    if (!successful(response, result)) {
                        // Retry only a positively identified IP rejection, not an ambiguous timeout or async failure.
                        boolean ipConflict = explicitIpConflict(response, result);
                        if (ipConflict || explicitRequestRejection(response, result)) {
                            VendorSecondInventory after = readInventorySafely();
                            if (after.complete && !after.containsName(requestName)) {
                                addressPool.rejectBeforeCreation(lease, ipConflict);
                                addressPool.prepare(after);
                                if (ipConflict) {
                                    requestedIp = null;
                                    continue;
                                }
                                throw new MpRuntimeException("厂商二明确拒绝创建请求，未发现新资源");
                            }
                        }
                        throw new MpRuntimeException("厂商二创建结果未确认，地址已保留待核查");
                    }
                    JSONObject data = result.getJSONObject("data");
                    taskId = data == null ? null : data.getString("task_id");
                    JSONArray uuids = data == null ? null : data.getJSONArray("uuids");
                    uuid = uuids == null || uuids.size() != 1 ? null : uuids.getString(0);
                    if (StringUtils.isBlank(uuid)) throw new MpRuntimeException("厂商二创建响应缺少唯一虚拟机UUID，地址待核查");
                } catch (RuntimeException e) {
                    addressPool.uncertainCreation(lease);
                    throw e;
                }
                log.info("厂商二创建请求已受理，uuid={}, task_id={}", uuid, taskId);
                // Preserve the resource association even if readiness cannot be confirmed.
                return confirmCreatedVm(lease, uuid, taskId);
            }
        } finally {
            vendorClientContext.remove();
        }
    }

    protected long createConfirmTimeoutMillis() {
        return Math.max(1000, Math.min(TimeUnit.MINUTES.toMillis(30), xdAppConfig.getXdTwoCreateConfirmTimeoutMs()));
    }

    protected long allocationRecoveryTimeoutMillis() {
        return Math.max(1000, Math.min(300000, xdAppConfig.getXdTwoAllocationRecoveryTimeoutMs()));
    }

    protected long recoveryPollMillis() {
        return Math.max(1000, Math.min(30000, xdAppConfig.getXdTwoRecoveryPollMs()));
    }

    private VendorSecondAddressPool.Lease reserveWithRecovery(String sceneId, String elementId, String portId,
                                                               String requestedIp, String owner, String requestName) {
        try {
            return addressPool.reserve(sceneId, elementId, portId, requestedIp, owner, requestName);
        } catch (VendorSecondAddressPool.Exhausted exhausted) {
            log.info("厂商二 FREE 不足，尝试即时核对回收中的地址，scene={}, element={}", sceneId, elementId);
        }
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(allocationRecoveryTimeoutMillis());
        Set<String> triedOnDemand = new HashSet<>();
        do {
            boolean recovered = allocationRecoveryLock.tryLock();
            if (recovered) {
                try {
                    List<VendorSecondAddressPool.Lease> releasing = addressPool.releasingLeases();
                    VendorSecondInventory inventory = readInventorySafely();
                    addressPool.recoverForAllocation(releasing, inventory);
                    try {
                        return addressPool.reserve(sceneId, elementId, portId, requestedIp, owner, requestName);
                    } catch (VendorSecondAddressPool.Exhausted exhausted) {
                        // Initial-pending fallback is limited to an explicitly
                        // assigned exclusive scope, with current inventory evidence.
                        VendorSecondAddressPool.Lease provisional = addressPool.reserveOnDemand(sceneId, elementId,
                                portId, requestedIp, owner, requestName, inventory, triedOnDemand);
                        if (provisional != null) return provisional;
                    }
                } finally {
                    allocationRecoveryLock.unlock();
                }
                if (!addressPool.hasReleasingAddressInPool() && !addressPool.hasOnDemandCandidate(triedOnDemand)) break;
            } else {
                // Another request is reconciling; it may have freed addresses.
                try {
                    return addressPool.reserve(sceneId, elementId, portId, requestedIp, owner, requestName);
                } catch (VendorSecondAddressPool.Exhausted exhausted) {
                    // Wait for the shared recovery path within the same deadline.
                }
            }
            if (!waitForRecoveryPoll(deadline)) break;
        } while (System.nanoTime() < deadline);
        throw new VendorSecondAddressPool.Exhausted("厂商二无已确认可用的IP地址（已尝试即时核对及可回收候选地址检查），"
                + "地址池状态=" + addressPool.statusSummary() + "，请核对已确认空闲范围");
    }

    private boolean waitForRecoveryPoll(long deadline) {
        long remaining = TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime());
        if (remaining <= 0) return false;
        try {
            Thread.sleep(Math.min(recoveryPollMillis(), remaining));
            return System.nanoTime() < deadline;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MpRuntimeException("厂商二地址或回收状态确认被中断，结果待核查").e(e);
        }
    }

    protected long createConfirmPollMillis() {
        return Math.max(1000, Math.min(30000, xdAppConfig.getXdTwoCreateConfirmPollMs()));
    }

    private ManagedVmCreationResult confirmCreatedVm(VendorSecondAddressPool.Lease lease, String uuid, String taskId) {
        boolean running = false;
        String[] actual = null;
        try {
            addressPool.recordCreated(lease, uuid);
            long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(createConfirmTimeoutMillis());
            do {
                VendorSecondInventory observed = readInventorySafely();
                running = observed.powerState(uuid) == VmPowerState.RUNNING;
                // The lease was already initialized/reserved. Confirming this UUID does
                // not require re-running pool initialization for every readiness poll.
                actual = addressPool.confirm(lease, uuid, observed);
                if (!observed.complete) actual = null;
                if (running && observed.complete && actual != null && StringUtils.isNoneBlank(actual[0], actual[1])) {
                    log.info("厂商二创建已确认运行且地址回读一致，uuid={}, task_id={}, ip={}", uuid, taskId, actual[0]);
                    return creationResult(uuid, taskId, true, actual);
                }
                long remaining = TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime());
                if (remaining <= 0) break;
                Thread.sleep(Math.min(createConfirmPollMillis(), remaining));
            } while (System.nanoTime() < deadline);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
            log.warn("厂商二创建确认被中断，uuid={}, task_id={}", uuid, taskId);
        } catch (RuntimeException e) {
            running = false;
            log.error("厂商二创建确认或台账关联待核查，uuid={}, task_id={}", uuid, taskId, e);
        }
        ManagedVmCreationResult result = creationResult(uuid, taskId, running, actual);
        log.warn("{}", result.pendingMessage());
        return result;
    }

    private ManagedVmCreationResult creationResult(String uuid, String taskId, boolean running, String[] actual) {
        return new ManagedVmCreationResult(uuid, actual == null ? null : actual[0],
                actual == null ? null : actual[1], xdAppConfig.getXdTwoIpNetmask(),
                xdAppConfig.getXdTwoIpGateway(), taskId, running);
    }

    private static boolean successful(HttpResult response, JSONObject result) {
        return response != null && response.isSuccess() && response.getException() == null
                && result != null && Integer.valueOf(0).equals(result.getInteger("code"));
    }

    // No undocumented business error code is treated as a definitive rejection.
    private static boolean rejectionWithNoTask(HttpResult response, JSONObject result) {
        if (response == null || response.getException() != null || response.getResponse() == null || result == null
                || result.getInteger("code") == null || result.getIntValue("code") == 0
                || result.get("task_id") != null || result.get("uuids") != null) return false;
        Object data = result.get("data");
        return data == null || (data instanceof JSONObject && ((JSONObject) data).isEmpty());
    }

    private static boolean explicitIpConflict(HttpResult response, JSONObject result) {
        if (!rejectionWithNoTask(response, result)) return false;
        int status = response.getResponse().getStatusLine().getStatusCode();
        if (status != 409 && status != 400 && status != 422 && !response.isSuccess()) return false;
        String message = StringUtils.defaultString(result.getString("message")).toLowerCase(Locale.ROOT);
        return message.contains("ip") && (message.contains("已占用") || message.contains("冲突")
                || message.contains("already in use") || message.contains("address conflict"));
    }

    private static boolean explicitRequestRejection(HttpResult response, JSONObject result) {
        if (!rejectionWithNoTask(response, result)) return false;
        int status = response.getResponse().getStatusLine().getStatusCode();
        return status == 400 || status == 401 || status == 403 || status == 422;
    }

    private VendorSecondInventory readInventory() {
        try {
            String url = "/janus/20180725/servers";
            return VendorSecondInventory.readAll(page -> {
                // SCP's existing signer signs the URI path without query parameters.
                vendorClientContext.set(new VendorSecondClientContext(url, METHOD_GET, null));
                HttpParam params = page == 0 ? null : new HttpParam() {
                    @Override public String toPathString() { return "page_num=" + page; }
                };
                return getClient().get(url, params, getHeaders(), 30000);
            });
        } finally {
            vendorClientContext.remove();
        }
    }

    private VendorSecondInventory readInventorySafely() {
        try {
            return readInventory();
        } catch (RuntimeException e) {
            log.warn("厂商二虚拟机清单暂不可核对：{}", e.getMessage());
            return VendorSecondInventory.unavailable();
        }
    }

    public void reconcileAddressPool() {
        if (!allocationRecoveryLock.tryLock()) return;
        try {
            List<VendorSecondAddressPool.Lease> before = addressPool.list();
            VendorSecondInventory inventory = readInventorySafely();
            List<VendorSecondAddressPool.Lease> releasing = new ArrayList<>();
            for (VendorSecondAddressPool.Lease row : before)
                if (VendorSecondAddressPool.RELEASING.equals(row.getStatus())) releasing.add(row);
            addressPool.reconcile(inventory, releasing);
            addressPool.refreshAvailableAddresses(inventory, before);
        } finally {
            allocationRecoveryLock.unlock();
        }
    }

    @Override
    public Map<String, String> readReclaimInventory() {
        VendorSecondInventory inventory = readInventory();
        if (!inventory.complete) throw new MpRuntimeException("厂商二资源清单不完整，本轮不执行兜底回收");
        Map<String, String> names = new LinkedHashMap<>();
        inventory.vms.forEach((id, vm) -> names.put(id, vm.getString("name")));
        return names;
    }

    @Override
    public boolean matchesReclaimIdentity(String vmUuid, String name, String sceneId, String elementId) {
        return MultiCloudService.isProviderUuid(vmUuid) && MultiCloudService.isMpCreatedName(name)
                && addressPool.matchesReclaimIdentity(vmUuid, name, sceneId, elementId);
    }

    @Override
    public void reclaimClosedVm(String vmUuid, String name, String sceneId, String elementId) {
        if (!matchesReclaimIdentity(vmUuid, name, sceneId, elementId))
            throw new MpRuntimeException("厂商二残留资源身份未确认");
        Map<String, String> current = readReclaimInventory();
        long sameName = current.values().stream().filter(name::equals).count();
        if (!current.containsKey(vmUuid) && sameName == 0) return;
        if (!name.equals(current.get(vmUuid)) || sameName != 1)
            throw new MpRuntimeException("厂商二残留资源身份已变化或名称不唯一");
        deleteManagedVm(vmUuid, sceneId, elementId);
    }

    @Override
    public boolean reconcileAbsentVm(String vmUuid, String sceneId, String elementId) {
        if (!MultiCloudService.isProviderUuid(vmUuid)) return false;
        boolean retained = addressPool.list().stream().anyMatch(row -> vmUuid.equals(row.getVmUuid())
                && sceneId.equals(row.getSceneId()) && elementId.equals(row.getElementId())
                && (VendorSecondAddressPool.USED.equals(row.getStatus())
                || VendorSecondAddressPool.RELEASING.equals(row.getStatus()) || "LEGACY_RECORD".equals(row.getReason())));
        if (!retained) return false;
        validateManagedVm(vmUuid, sceneId, elementId);
        VendorSecondInventory observed = readInventorySafely();
        if (!observed.complete || observed.vms.containsKey(vmUuid)) return false;
        addressPool.beginDelete(vmUuid, observed);
        addressPool.deleteSubmitted(vmUuid, false);
        List<VendorSecondAddressPool.Lease> beforeRead = addressPool.releasingLeases();
        return addressPool.reconcileDeleted(vmUuid, readInventorySafely(), beforeRead);
    }

    private JSONObject getTargetImage(String imageUuid) {
        try {
            String url = "/janus/20180725/images";
            vendorClientContext.set(new VendorSecondClientContext(url, METHOD_GET, null));
            HttpResult response = getClient().get(url, null, getHeaders(), 30000);
            JSONObject result = response == null ? null : JSONObject.parseObject(response.getResult());
            if (!successful(response, result)) {
                throw new MpRuntimeException("厂商二镜像列表查询失败");
            }
            JSONObject data = result.getJSONObject("data");
            JSONArray images = data == null ? null : data.getJSONArray("data");
            if (images != null) {
                for (int i = 0; i < images.size(); i++) {
                    JSONObject image = images.getJSONObject(i);
                    if (image != null && Objects.equals(image.getString("id"), imageUuid)) return image;
                }
            }
            log.warn("厂商二镜像查询未命中 - imageUuid: {}", imageUuid);
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("未找到指定镜像"));
        } finally {
            vendorClientContext.remove();
        }
    }

    @Override
    public String[] getVmIpAndMac(String vmUuid) {
        return readInventory().primaryAddress(vmUuid);
    }

    @Override
    public VmPowerState getVmPowerState(String vmUuid) {
        requireVmId(vmUuid);
        return readInventory().powerState(vmUuid);
    }

    @Override
    protected void submitPowerAction(String vmUuid, VmPowerState target) {
        requireVmId(vmUuid);
        String url = "/janus/20180725/servers/" + vmUuid + (target == VmPowerState.RUNNING ? "/start" : "/stop");
        try {
            vendorClientContext.set(new VendorSecondClientContext(url, METHOD_POST, null));
            HttpResult response = getClient().post(url, null, null, getHeaders(), 30000);
            JSONObject result = response == null ? null : JSONObject.parseObject(response.getResult());
            if (!successful(response, result)) throw new MpRuntimeException("厂商二电源操作未受理，uuid=" + vmUuid);
            JSONObject data = result.getJSONObject("data");
            log.info("厂商二电源操作已受理，uuid={}, target={}, task_id={}", vmUuid, target,
                    data == null ? null : data.getString("task_id"));
        } finally {
            vendorClientContext.remove();
        }
    }

    protected long recycleStopTimeoutMillis() {
        return Math.max(1000, Math.min(300000, xdAppConfig.getXdTwoRecycleStopTimeoutMs()));
    }

    protected long recycleDeleteTimeoutMillis() {
        return Math.max(1000, Math.min(300000, xdAppConfig.getXdTwoRecycleDeleteTimeoutMs()));
    }

    @Override
    public void deleteManagedVm(String vmUuid, String sceneId, String elementId) {
        if (StringUtils.isBlank(vmUuid)) {
            vmUuid = addressPool.resolveManagedVmForDelete(sceneId, elementId, readInventorySafely());
            if (vmUuid == null) return;
            log.info("厂商二从地址台账恢复待回收虚拟机关联，scene={}, element={}, uuid={}", sceneId, elementId, vmUuid);
        }
        validateManagedVm(vmUuid, sceneId, elementId);
        deleteVm(vmUuid);
    }

    @Override
    public void validateManagedVm(String vmUuid, String sceneId, String elementId) {
        requireVmId(vmUuid);
        addressPool.validateManagedOwner(vmUuid, sceneId, elementId);
    }

    @Override
    public void deleteVm(String vmUuid) {
        // A blank or malformed UUID must never reach /servers//stop or /servers/.
        requireVmId(vmUuid);
        VendorSecondInventory before = readInventorySafely();
        addressPool.prepare(before);
        addressPool.beginDelete(vmUuid, before);
        boolean recorded = false;
        try {
            if (!before.vms.containsKey(vmUuid)) {
                // A missing resource can be a legitimate idempotent delete or an
                // accepted creation still not visible. Recheck instead of guessing.
                addressPool.deleteSubmitted(vmUuid, false);
                recorded = true;
                long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(recycleDeleteTimeoutMillis());
                do {
                    List<VendorSecondAddressPool.Lease> releasing = addressPool.releasingLeases();
                    before = readInventorySafely();
                    if (before.vms.containsKey(vmUuid)) break;
                    if (addressPool.reconcileDeleted(vmUuid, before, releasing)) return;
                    if (!waitForRecoveryPoll(deadline)) break;
                } while (System.nanoTime() < deadline);
                if (!before.vms.containsKey(vmUuid)) {
                    throw new MpRuntimeException("厂商二虚拟机尚不可确认存在或已删除，uuid=" + vmUuid + "，地址保留待核查");
                }
                addressPool.beginDelete(vmUuid, before);
                recorded = false;
            }

            if (before.powerState(vmUuid) != VmPowerState.STOPPED) {
                HttpResult stop = submitDeleteOperation(vmUuid, true);
                boolean stopAccepted = operationAccepted(stop);
                log.info("厂商二回收关机请求，uuid={}, {}", vmUuid, operationResult(stop));
                VendorSecondInventory stopped = waitForRecycleStop(vmUuid, stopAccepted || ambiguousOperation(stop));
                if (!stopped.vms.containsKey(vmUuid) && stopped.complete) {
                    // Resource disappeared during stop; confirm absence again after
                    // recording the outcome, without issuing another mutation.
                    addressPool.deleteSubmitted(vmUuid, false);
                    recorded = true;
                    if (waitForDeletionConfirmation(vmUuid, true)) return;
                    throw new MpRuntimeException("厂商二资源消失但回收结果尚未确认，uuid=" + vmUuid);
                }
                // A verified stopped state is sufficient even if the stop
                // request was rejected as already stopped.
                if (stopped.powerState(vmUuid) != VmPowerState.STOPPED) {
                    throw new MpRuntimeException("厂商二关机尚未确认，不提交删除，uuid=" + vmUuid + "，" + operationResult(stop));
                }
            }

            HttpResult response = submitDeleteOperation(vmUuid, false);
            boolean accepted = operationAccepted(response);
            addressPool.deleteSubmitted(vmUuid, accepted);
            recorded = true;
            log.info("厂商二回收删除请求，uuid={}, {}", vmUuid, operationResult(response));
            if (waitForDeletionConfirmation(vmUuid, accepted || ambiguousOperation(response))) return;
            throw new MpRuntimeException("厂商二删除结果尚未确认，地址暂不释放，uuid=" + vmUuid + "，" + operationResult(response));
        } finally {
            // Failed/ambiguous stop or delete never releases an IP. Reconciliation
            // can subsequently prove absence using an observed resource association.
            if (!recorded) addressPool.deleteSubmitted(vmUuid, false);
            vendorClientContext.remove();
        }
    }

    private VendorSecondInventory waitForRecycleStop(String uuid, boolean wait) {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(recycleStopTimeoutMillis());
        VendorSecondInventory observed;
        do {
            observed = readInventorySafely();
            if (observed.powerState(uuid) == VmPowerState.STOPPED
                    || (observed.complete && !observed.vms.containsKey(uuid))) return observed;
            if (!wait || !waitForRecoveryPoll(deadline)) return observed;
        } while (System.nanoTime() < deadline);
        return observed;
    }

    private boolean waitForDeletionConfirmation(String uuid, boolean wait) {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(recycleDeleteTimeoutMillis());
        do {
            List<VendorSecondAddressPool.Lease> releasing = addressPool.releasingLeases();
            if (addressPool.reconcileDeleted(uuid, readInventorySafely(), releasing)) return true;
            if (!wait || !waitForRecoveryPoll(deadline)) break;
        } while (System.nanoTime() < deadline);
        return false;
    }

    private HttpResult submitDeleteOperation(String uuid, boolean stop) {
        String url = "/janus/20180725/servers/" + uuid + (stop ? "/stop" : "");
        VendorSecondVmDeleteHttpData req = new VendorSecondVmDeleteHttpData();
        try {
            vendorClientContext.set(new VendorSecondClientContext(url, stop ? METHOD_POST : METHOD_DELETE, req));
            return stop ? getClient().post(url, req, null, getHeaders(), 30000)
                    : getClient().delete(url, req, null, getHeaders(), 30000);
        } finally {
            vendorClientContext.remove();
        }
    }

    private JSONObject operationBody(HttpResult response) {
        try {
            return response == null ? null : JSONObject.parseObject(response.getResult());
        } catch (RuntimeException e) {
            return null;
        }
    }

    private boolean operationAccepted(HttpResult response) {
        try {
            return successful(response, operationBody(response));
        } catch (RuntimeException e) {
            return false;
        }
    }

    private boolean ambiguousOperation(HttpResult response) {
        if (response == null || response.getException() != null || response.getResponse() == null) return true;
        int status = response.getResponse().getStatusLine().getStatusCode();
        if (status >= 500) return true;
        JSONObject body = operationBody(response);
        if (!response.isSuccess()) return false;
        try {
            return body == null || body.getInteger("code") == null;
        } catch (RuntimeException e) {
            return true;
        }
    }

    private String operationResult(HttpResult response) {
        JSONObject body = operationBody(response);
        String status = response == null || response.getResponse() == null ? "无响应"
                : String.valueOf(response.getResponse().getStatusLine().getStatusCode());
        return "HTTP=" + status + ", code=" + (body == null ? null : body.get("code"));
    }

    @Override
    public String getConnection(String vmUuid) {
        try {
            VendorSecondConnectionHttpData req = new VendorSecondConnectionHttpData();
            String url = "/janus/20180725/servers/" + vmUuid + "/remote-consoles";
            vendorClientContext.set(new VendorSecondClientContext(url, METHOD_POST, req));

            HttpResult httpResult = getClient().post(url, req, null, getHeaders(), 30000);
            JSONObject jsonObject = JSONObject.parseObject(httpResult.getResult());

            return jsonObject.getJSONObject("remote_console").getString("url");
        } finally {
            vendorClientContext.remove();
        }
    }

    @Override
    public boolean ipAutoGen() {
        return false;
    }
}
