package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.config.XdAppConfig;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.base.util.http.MultiCloudClient;
import cn.cyberpeace.mp.common.xd.XdThreeCookieRequest;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@AllArgsConstructor
public class VendorThirdCloudServiceImpl extends AbstractVendorCloudService {
    private final MultiCloudClient multiCloudClient;
    private final XdAppConfig config;
    private final VendorThirdVmLedger ledger;
    private static final String INVENTORY = "/api2/json/cluster/resources/";
    private static final String VM_LIST = INVENTORY + "?type=vm";

    @Override
    public Map<String, String> getHeaders() {
        HttpResult response = getClient().post("/api2/json/access/ticket", new XdThreeCookieRequest(), null, new HashMap<>(), 30000);
        JSONObject root = body(response);
        JSONObject data = root == null ? null : root.getJSONObject("data");
        String ticket = data == null ? null : data.getString("ticket");
        if (response == null || !response.isSuccess() || response.getException() != null || StringUtils.isBlank(ticket)) {
            throw new MpRuntimeException("厂商三认证失败");
        }
        return Collections.singletonMap("Cookie", "HCICookie=" + ticket);
    }

    @Override
    public HttpClient getClient() {
        return multiCloudClient.getClient(getVendor());
    }

    @Override
    public synchronized List<String> listAllDevices() {

        try {
            int index = 0;
            List<String> images = new ArrayList<>();
            for (JSONObject image : readInventory(true).vms.values()) {
                boolean isTemplate = 1 == Optional.ofNullable(image.getInteger("template")).orElse(0);
                String name = Optional.ofNullable(image.getString("name")).orElse("");


                if (isTemplate && VendorThirdInventory.isQemu(image)) {
                    int vmId = image.getInteger("vmid");
                    JSONObject device = JSONObject.parseObject(DEVICE_TEMPLATE);
                    device.put("id", index++);
                    device.put("vendor", getVendor());
                    device.put("uuid", vmId);
                    device.put("name", name);
                    device.put("image", name);
                    device.put("description", name);


                    device.put("second_category", 1);
                    device.put("l3_category", 106);
                    device.getJSONObject("ext_system_info")
                            .put("parent_system_type", 1);
                    device.getJSONObject("ext_system_info")
                            .put("system_type", 106);


                    device.getJSONObject("ext_vm").put("access_protocols", new ArrayList<>());
                    device.getJSONObject("ext_image_info").put("uuid", vmId);
                    device.getJSONObject("ext_image_info").put("base_image_name", name);

                    images.add(device.toJSONString());
                }

            }
            return images;

        } catch (Exception e) {
            log.error("获取厂商靶标列表异常: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public int getVendor() {
        return 3;
    }

    protected long cloneTimeoutMillis() { return bounded(config.getXdThreeCloneTimeoutMs()); }
    protected long networkTimeoutMillis() { return bounded(config.getXdThreeNetworkTimeoutMs()); }
    protected long readyTimeoutMillis() { return bounded(config.getXdThreeReadyTimeoutMs()); }
    protected long deleteTimeoutMillis() { return bounded(config.getXdThreeDeleteTimeoutMs()); }
    protected long pollMillis() { return Math.max(1000, Math.min(30000, config.getXdThreePollMs())); }

    private long bounded(long value) { return Math.max(1000, Math.min(TimeUnit.MINUTES.toMillis(30), value)); }

    private long deadline(long millis) { return System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(millis); }

    private boolean pause(long deadline) {
        long remaining = TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime());
        if (remaining <= 0) return false;
        try {
            Thread.sleep(Math.min(pollMillis(), remaining));
            return System.nanoTime() < deadline;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MpRuntimeException("厂商三状态核对被中断").e(e);
        }
    }

    private VendorThirdInventory readInventory(boolean all) {
        return VendorThirdInventory.parse(getClient().get(all ? INVENTORY : VM_LIST, null, getHeaders(), 30000));
    }

    private VendorThirdInventory inventorySafely(boolean all) {
        try {
            return readInventory(all);
        } catch (RuntimeException e) {
            log.warn("厂商三资源清单暂不可核对：{}", e.getMessage());
            return null;
        }
    }

    private VendorThirdInventory waitForInventory() {
        long until = deadline(cloneTimeoutMillis());
        do {
            VendorThirdInventory inventory = inventorySafely(true);
            if (inventory != null) return inventory;
        } while (pause(until));
        throw new MpRuntimeException("厂商三完整资源清单尚未确认，未提交克隆");
    }

    private Map<String, String> headersSafely() {
        try {
            return getHeaders();
        } catch (RuntimeException e) {
            log.warn("厂商三认证暂不可用，尚未发送本次写请求");
            return null;
        }
    }

    private Map<String, String> waitForHeaders() {
        long until = deadline(cloneTimeoutMillis());
        do {
            Map<String, String> headers = headersSafely();
            if (headers != null) return headers;
        } while (pause(until));
        throw new MpRuntimeException("厂商三认证未恢复，未提交克隆");
    }

    private int parseVmid(String value) {
        try {
            int id = Integer.parseInt(value);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            throw new MpRuntimeException("厂商三VMID无效");
        }
    }

    private String checkedNode(JSONObject vm) {
        String node = vm == null ? null : vm.getString("node");
        if (node == null || !node.matches("[A-Za-z0-9_.-]+")) {
            throw new MpRuntimeException("厂商三计算节点未确认");
        }
        return node;
    }

    private String vmUrl(JSONObject vm, int id) {
        return "/api2/json/nodes/" + checkedNode(vm) + "/qemu/" + id;
    }

    private JSONObject body(HttpResult response) {
        try { return response == null ? null : JSONObject.parseObject(response.getResult()); }
        catch (RuntimeException e) { return null; }
    }

    private boolean accepted(HttpResult response) {
        JSONObject body = body(response);
        if (response == null || !response.isSuccess() || response.getException() != null || body == null) return false;
        try { return Integer.valueOf(1).equals(body.getInteger("success")); }
        catch (RuntimeException e) { return false; }
    }

    private String taskId(HttpResult response) {
        JSONObject body = body(response);
        return body != null && body.get("data") instanceof String ? body.getString("data") : null;
    }

    private boolean rejected(HttpResult response) {
        JSONObject body = body(response);
        if (response == null || response.getException() != null || response.getResponse() == null || body == null) return false;
        int http = response.getResponse().getStatusLine().getStatusCode();
        Object data = body.get("data");
        if (http >= 500 || (data != null && !(data instanceof String && ((String) data).isEmpty()))) return false;
        try { return Integer.valueOf(0).equals(body.getInteger("success")); }
        catch (RuntimeException e) { return false; }
    }

    private boolean idConflict(HttpResult response, VendorThirdInventory inventory, VendorThirdVmLedger.Lease lease) {
        if (!rejected(response)) return false;
        String message = StringUtils.defaultString(body(response).getString("message")).toLowerCase(Locale.ROOT);
        boolean conflict = message.contains("already exists") || message.contains("already in use")
                || message.contains("已存在") || message.contains("已被占用");
        return conflict && (inventory.find(lease.getVmid()) != null
                || message.contains("vmid") || message.contains("vm id"));
    }

    private String responseInfo(HttpResult response) {
        JSONObject body = body(response);
        return "HTTP=" + (response == null || response.getResponse() == null ? "无响应" :
                response.getResponse().getStatusLine().getStatusCode())
                + ",success=" + (body == null ? null : body.get("success"));
    }

    private JSONObject requireSuccess(HttpResult response, String stage, int vmid) {
        if (!accepted(response)) throw new MpRuntimeException("厂商三 VM " + vmid + " " + stage + "未确认，" + responseInfo(response));
        return body(response);
    }

    private VmPowerState power(JSONObject vm) {
        if (vm == null || StringUtils.isNotBlank(vm.getString("lock"))) return VmPowerState.UNKNOWN;
        String status = vm.getString("status");
        if ("running".equalsIgnoreCase(status)) return VmPowerState.RUNNING;
        if ("stopped".equalsIgnoreCase(status)) return VmPowerState.STOPPED;
        return VmPowerState.UNKNOWN;
    }

    private JSONObject ownedVm(VendorThirdInventory inventory, VendorThirdVmLedger.Lease lease) {
        if (inventory == null) return null;
        JSONObject vm = inventory.find(lease.getVmid());
        if (vm != null && !inventory.owns(lease, vm)) {
            throw new MpRuntimeException("厂商三VMID与原资源名称不匹配，禁止操作 vmid=" + lease.getVmid());
        }
        if (vm == null && inventory.hasName(lease.getRequestName())) {
            throw new MpRuntimeException("厂商三原资源名称对应的VMID已变化，禁止操作");
        }
        if (vm != null) ledger.observed(lease, checkedNode(vm));
        return vm;
    }

    private JSONObject configOf(JSONObject vm, int id, String expectedName) {
        JSONObject data = requireSuccess(getClient().get(vmUrl(vm, id) + "/config", null, getHeaders(), 30000),
                "读取配置", id).getJSONObject("data");
        if (data == null) throw new MpRuntimeException("厂商三虚拟机配置缺失");
        if (expectedName != null && data.getString("name") != null && !expectedName.equals(data.getString("name"))) {
            throw new MpRuntimeException("厂商三虚拟机配置名称与台账不匹配");
        }
        return data;
    }

    private JSONObject waitForClone(VendorThirdVmLedger.Lease lease) {
        long until = deadline(cloneTimeoutMillis());
        do {
            JSONObject vm = ownedVm(inventorySafely(false), lease);
            if (power(vm) != VmPowerState.UNKNOWN) return vm;
        } while (pause(until));
        throw new MpRuntimeException("厂商三克隆状态尚未确认");
    }

    private boolean targetNetwork(VendorThirdNic nic) {
        return nic.isVirtio() && StringUtils.isNotBlank(config.getXdThreeNetworkId())
                && config.getXdThreeNetworkId().equals(nic.getNetwork());
    }

    private void configureNetwork(VendorThirdVmLedger.Lease lease, boolean canChangeNetwork) {
        long until = deadline(networkTimeoutMillis());
        int writes = 0;
        long nextWrite = 0;
        do {
            JSONObject vm = ownedVm(inventorySafely(false), lease);
            if (power(vm) == VmPowerState.UNKNOWN) continue;
            JSONObject current;
            try { current = configOf(vm, lease.getVmid(), lease.getRequestName()); }
            catch (RuntimeException e) {
                log.warn("厂商三 VM {} 配置暂不可读：{}", lease.getVmid(), e.getMessage());
                continue;
            }
            VendorThirdNic nic = VendorThirdNic.parse(current);
            if (targetNetwork(nic)) {
                if (canChangeNetwork) ledger.advance(lease, "NETWORK_CONFIRMED", null);
                return;
            }
            // Do not modify a running or still locked VM. A lost PUT response
            // is handled by reading the actual configuration before another write.
            if (!canChangeNetwork || power(vm) != VmPowerState.STOPPED || writes >= 3
                    || System.nanoTime() < nextWrite || StringUtils.isBlank(current.getString("digest"))) continue;
            Map<String, String> headers = headersSafely();
            if (headers == null) continue;
            ledger.advance(lease, "NETWORK_CONFIGURING", null);
            JSONObject patch = new JSONObject();
            patch.put("digest", current.getString("digest"));
            patch.put("vnet0", "virtio,network=" + config.getXdThreeNetworkId());
            writes++;
            try {
                HttpResult response = getClient().put(vmUrl(vm, lease.getVmid()) + "/config",
                        new StringHttpData(patch.toJSONString()), null, headers, 30000);
                log.info("厂商三 VM {} 配网请求，{}", lease.getVmid(), responseInfo(response));
            } catch (RuntimeException e) {
                log.warn("厂商三 VM {} 配网结果待回读：{}", lease.getVmid(), e.getMessage());
            }
            // Give asynchronous network changes time to converge before
            // another fresh-digest write can allocate a replacement address.
            nextWrite = deadline(pollMillis() * 3);
        } while (pause(until));
        throw new MpRuntimeException("厂商三网卡配置尚未确认");
    }

    private ManagedVmCreationResult result(VendorThirdVmLedger.Lease lease, VendorThirdNic nic, boolean running, String stage) {
        VendorThirdVmLedger.Lease latest = ledger.byId(lease.getVmid());
        return new ManagedVmCreationResult(String.valueOf(lease.getVmid()), nic == null ? null : nic.getIp(),
                nic == null ? null : nic.getMac(), nic == null ? null : nic.getNetmask(),
                nic == null ? null : nic.getGateway(), latest == null ? lease.getTaskId() : latest.getTaskId(),
                running, 3, stage);
    }

    private ManagedVmCreationResult waitForReady(VendorThirdVmLedger.Lease lease, boolean startMayBeSubmitted) {
        long until = deadline(readyTimeoutMillis());
        int requests = 0;
        boolean running = false;
        VendorThirdNic last = null;
        do {
            JSONObject vm = ownedVm(inventorySafely(false), lease);
            VmPowerState state = power(vm);
            running = state == VmPowerState.RUNNING;
            last = null;
            if (running) {
                try {
                    VendorThirdNic nic = VendorThirdNic.parse(configOf(vm, lease.getVmid(), lease.getRequestName()));
                    if (targetNetwork(nic)) {
                        last = nic;
                        if (nic.hasAddress() && ledger.ready(lease, nic.getIp(), nic.getMac(), nic.getNetmask(), nic.getGateway())) {
                            log.info("厂商三 VM {} 已确认运行及网络地址", lease.getVmid());
                            return result(lease, nic, true, "READY");
                        }
                    }
                } catch (RuntimeException e) {
                    log.warn("厂商三 VM {} 地址暂不可回读：{}", lease.getVmid(), e.getMessage());
                }
            } else if (state == VmPowerState.STOPPED && startMayBeSubmitted && requests < 3) {
                Map<String, String> headers = headersSafely();
                if (headers == null) continue;
                ledger.advance(lease, "START_IN_FLIGHT", null);
                requests++;
                HttpResult start = null;
                try {
                    start = getClient().post(vmUrl(vm, lease.getVmid()) + "/status/start", null, null, headers, 30000);
                } catch (RuntimeException e) {
                    log.warn("厂商三 VM {} 启动结果待核查：{}", lease.getVmid(), e.getMessage());
                }
                startMayBeSubmitted = rejected(start);
                ledger.advance(lease, startMayBeSubmitted ? "START_REJECTED" :
                        accepted(start) ? "START_REQUESTED" : "START_UNCERTAIN", taskId(start));
                log.info("厂商三 VM {} 开机请求，{}", lease.getVmid(), responseInfo(start));
            }
        } while (pause(until));
        VendorThirdVmLedger.Lease latest = ledger.byId(lease.getVmid());
        String stage = latest == null ? "READY_UNCONFIRMED" : latest.getStage();
        ledger.pending(lease, stage);
        // A positively verified address alone cannot override a concurrent
        // ledger state change (e.g. cancellation/recycle).
        return result(lease, last, running && (last == null || !last.hasAddress()), stage);
    }

    @Override
    public String createVm(String imageUuid) {
        ManagedVmCreationResult result = createManagedVm(imageUuid, null, null, null, null);
        if (!result.isReady()) throw new MpRuntimeException(result.pendingMessage());
        return result.getResourceId();
    }

    @Override
    public ManagedVmCreationResult createManagedVm(String imageUuid, String sceneId, String elementId,
                                                    String portId, String requestedIp) {
        int template = parseVmid(imageUuid);
        String owner = VendorThirdVmLedger.ownerKey(sceneId, elementId);
        if (owner == null) owner = TextUtils.uuid();
        if (StringUtils.isBlank(config.getXdThreeNetworkId())) throw new MpRuntimeException("厂商三目标网络未配置");
        VendorThirdVmLedger.Lease existing = ledger.byOwner(owner);
        if (existing != null) {
            if (!imageUuid.equals(existing.getTemplateId()) || VendorThirdVmLedger.DELETING.equals(existing.getStatus())) {
                throw new MpRuntimeException("厂商三节点已有不同资源关联或正在回收");
            }
            if (VendorThirdVmLedger.PENDING.equals(existing.getStatus()) && ledger.claimResume(existing)) {
                // Resume the existing identity; never issue a second clone for an ambiguous request.
                return finishCreation(ledger.byId(existing.getVmid()), true);
            }
            if (VendorThirdVmLedger.READY.equals(existing.getStatus())) {
                JSONObject vm = ownedVm(inventorySafely(false), existing);
                if (power(vm) == VmPowerState.RUNNING) {
                    VendorThirdNic nic = VendorThirdNic.parse(configOf(vm, existing.getVmid(), existing.getRequestName()));
                    if (targetNetwork(nic) && nic.hasAddress()) return result(existing, nic, true, "READY");
                }
            }
            return result(existing, null, false, existing.getStage());
        }
        for (int attempt = 0; attempt < 10; attempt++) {
            VendorThirdInventory inventory = waitForInventory();
            JSONObject image = inventory.find(template);
            if (!VendorThirdInventory.isQemu(image) || image.getIntValue("template") != 1) {
                throw new MpRuntimeException("厂商三未找到指定模板，未提交克隆");
            }
            String node = checkedNode(image);
            Map<String, String> headers = waitForHeaders();
            VendorThirdVmLedger.Lease lease;
            try {
                lease = ledger.reserve(owner, sceneId, elementId, portId, imageUuid,
                        "MP_CREATE_" + TextUtils.uuid(), node, inventory.vms.keySet());
            } catch (MpRuntimeException e) {
                VendorThirdVmLedger.Lease competing = ledger.byOwner(owner);
                if (competing != null && imageUuid.equals(competing.getTemplateId())
                        && !VendorThirdVmLedger.DELETING.equals(competing.getStatus())) {
                    return result(competing, null, false, competing.getStage());
                }
                throw e;
            }
            JSONObject request = new JSONObject();
            request.put("name", lease.getRequestName());
            request.put("newid", lease.getVmid());
            request.put("target", node);
            ledger.advance(lease, "CLONE_IN_FLIGHT", null);
            HttpResult response = null;
            try {
                response = getClient().post(vmUrl(image, template) + "/clone", new StringHttpData(request.toJSONString()),
                        null, headers, 30000);
            } catch (RuntimeException e) {
                log.warn("厂商三 VM {} 克隆响应未确认：{}", lease.getVmid(), e.getMessage());
            }
            if (rejected(response)) {
                VendorThirdInventory after = inventorySafely(true);
                if (after != null && !after.hasName(lease.getRequestName()) && ledger.rejected(lease, "CLONE_REJECTED")) {
                    if (idConflict(response, after, lease)) {
                        log.warn("厂商三 VMID {} 明确被拒绝，重新预占编号", lease.getVmid());
                        continue;
                    }
                    throw new MpRuntimeException("厂商三克隆请求明确失败，" + responseInfo(response));
                }
            }
            ledger.advance(lease, accepted(response) ? "CLONE_REQUESTED" : "CLONE_UNCERTAIN", taskId(response));
            log.info("厂商三 VM {} 克隆请求，node={}, task_id={}, {}", lease.getVmid(), node, taskId(response), responseInfo(response));
            return finishCreation(lease, false);
        }
        throw new MpRuntimeException("厂商三VMID冲突重试次数已达上限");
    }

    private ManagedVmCreationResult finishCreation(VendorThirdVmLedger.Lease lease, boolean resumed) {
        try {
            waitForClone(lease);
            // Resume after an accepted or ambiguous start without blindly resubmitting.
            boolean canStart = !resumed || !Arrays.asList("START_REQUESTED", "START_UNCERTAIN", "START_IN_FLIGHT")
                    .contains(lease.getStage());
            configureNetwork(lease, canStart);
            return waitForReady(lease, canStart);
        } catch (RuntimeException e) {
            VendorThirdVmLedger.Lease latest = ledger.byId(lease.getVmid());
            String stage = latest == null ? lease.getStage() : latest.getStage();
            ledger.pending(lease, stage);
            log.warn("厂商三 VM {} 创建结果待核查，阶段={}：{}", lease.getVmid(), stage, e.getMessage());
            return result(lease, null, false, stage);
        }
    }

    @Override
    public String[] getVmIpAndMac(String vmUuid) {
        int id = parseVmid(vmUuid);
        JSONObject vm = readPowerVm(vmUuid);
        if (vm == null) return null;
        VendorThirdVmLedger.Lease lease = ledger.byId(id);
        VendorThirdNic nic = VendorThirdNic.parse(configOf(vm, id, lease == null ? null : lease.getRequestName()));
        return targetNetwork(nic) && nic.hasAddress() ? new String[]{nic.getIp(), nic.getMac()} : null;
    }

    private JSONObject readPowerVm(String vmUuid) {
        int id = parseVmid(vmUuid);
        VendorThirdInventory inventory = readInventory(false);
        VendorThirdVmLedger.Lease lease = ledger.byId(id);
        if (lease != null) {
            if (VendorThirdVmLedger.DELETED.equals(lease.getStatus()) || VendorThirdVmLedger.REJECTED.equals(lease.getStatus())) return null;
            return ownedVm(inventory, lease);
        }
        JSONObject vm = inventory.find(id);
        return VendorThirdInventory.isQemu(vm) && vm.getIntValue("template") != 1 ? vm : null;
    }

    @Override
    public VmPowerState getVmPowerState(String vmUuid) {
        return power(readPowerVm(vmUuid));
    }

    @Override
    public void validateManagedVm(String vmUuid, String sceneId, String elementId) {
        VendorThirdVmLedger.Lease lease = ledger.byOwner(VendorThirdVmLedger.ownerKey(sceneId, elementId));
        if (lease == null || parseVmid(vmUuid) != lease.getVmid()
                || VendorThirdVmLedger.DELETING.equals(lease.getStatus())) {
            throw new MpRuntimeException("厂商三场景节点资源身份未确认，禁止电源操作");
        }
        if (ownedVm(readInventory(false), lease) == null) throw new MpRuntimeException("厂商三原虚拟机不存在");
    }

    @Override
    protected void submitPowerAction(String vmUuid, VmPowerState target) {
        JSONObject vm = readPowerVm(vmUuid);
        if (power(vm) == VmPowerState.UNKNOWN) throw new MpRuntimeException("厂商三虚拟机状态或所在主机未确认");
        String url = vmUrl(vm, parseVmid(vmUuid)) + "/status/" + (target == VmPowerState.RUNNING ? "start" : "stop");
        JSONObject result = requireSuccess(getClient().post(url, null, null, getHeaders(), 30000),
                "提交电源操作", parseVmid(vmUuid));
        log.info("厂商三 VM {} 电源操作已受理，target={}, UPID={}", vmUuid, target, result.getString("data"));
    }

    private boolean confirmAbsent(VendorThirdVmLedger.Lease lease, VendorThirdInventory inventory) {
        if (inventory == null || inventory.find(lease.getVmid()) != null || inventory.hasName(lease.getRequestName())) return false;
        if (ledger.deleted(lease)) return true;
        VendorThirdVmLedger.Lease current = ledger.byId(lease.getVmid());
        return current != null && VendorThirdVmLedger.DELETED.equals(current.getStatus());
    }

    private void recycle(VendorThirdVmLedger.Lease lease) {
        lease = ledger.beginDelete(lease);
        long until = deadline(deleteTimeoutMillis());
        Set<String> stopBlocked = new HashSet<>(Arrays.asList("STOP_IN_FLIGHT", "STOP_REQUESTED", "STOP_UNCONFIRMED",
                "DELETE_IN_FLIGHT", "DELETE_REQUESTED", "DELETE_UNCONFIRMED"));
        Set<String> deleteBlocked = new HashSet<>(Arrays.asList("DELETE_IN_FLIGHT", "DELETE_REQUESTED", "DELETE_UNCONFIRMED"));
        boolean stopMaySubmit = !stopBlocked.contains(lease.getStage());
        // Once the actual VM is confirmed stopped, deletion may proceed
        // even if the previous stop request's response was lost.
        boolean deleteMaySubmit = !deleteBlocked.contains(lease.getStage());
        int stopAttempts = 0, deleteAttempts = 0;
        do {
            VendorThirdInventory inventory = inventorySafely(true);
            if (confirmAbsent(lease, inventory)) return;
            JSONObject vm = ownedVm(inventory, lease);
            VmPowerState state = power(vm);
            if (state == VmPowerState.RUNNING && stopMaySubmit && stopAttempts < 3 && deleteAttempts == 0) {
                Map<String, String> headers = headersSafely();
                if (headers == null || !ledger.claimDeleteAction(lease, "STOP_IN_FLIGHT", stopBlocked)) continue;
                stopAttempts++;
                HttpResult stop = null;
                try { stop = getClient().post(vmUrl(vm, lease.getVmid()) + "/status/stop", null, null, headers, 30000); }
                catch (RuntimeException e) { log.warn("厂商三 VM {} 回收关机待核查", lease.getVmid()); }
                stopMaySubmit = rejected(stop);
                ledger.deleteStage(lease, "STOP_IN_FLIGHT", stopMaySubmit ? "STOP_REJECTED" : accepted(stop) ? "STOP_REQUESTED" : "STOP_UNCONFIRMED", taskId(stop));
                log.info("厂商三 VM {} 回收关机请求，{}", lease.getVmid(), responseInfo(stop));
            } else if (state == VmPowerState.STOPPED && deleteMaySubmit && deleteAttempts < 3) {
                Map<String, String> headers = headersSafely();
                if (headers == null || !ledger.claimDeleteAction(lease, "DELETE_IN_FLIGHT", deleteBlocked)) continue;
                deleteAttempts++;
                String url = "/api2/extjs/nodes/" + checkedNode(vm) + "/qemu/" + lease.getVmid() + "/";
                HttpResult response = null;
                try { response = getClient().delete(url, null, null, headers, 30000); }
                catch (RuntimeException e) { log.warn("厂商三 VM {} 删除结果待核查", lease.getVmid()); }
                deleteMaySubmit = rejected(response);
                ledger.deleteStage(lease, "DELETE_IN_FLIGHT", deleteMaySubmit ? "DELETE_REJECTED" : accepted(response) ? "DELETE_REQUESTED" : "DELETE_UNCONFIRMED", taskId(response));
                log.info("厂商三 VM {} 回收删除请求，{}", lease.getVmid(), responseInfo(response));
            }
        } while (pause(until));
        throw new MpRuntimeException("厂商三虚拟机回收尚未确认，保留VMID关联待核查，vmid=" + lease.getVmid());
    }

    @Override
    public void deleteManagedVm(String vmUuid, String sceneId, String elementId) {
        VendorThirdVmLedger.Lease lease = ledger.byOwner(VendorThirdVmLedger.ownerKey(sceneId, elementId));
        if (lease != null) {
            if (StringUtils.isNotBlank(vmUuid) && parseVmid(vmUuid) != lease.getVmid()) {
                throw new MpRuntimeException("厂商三场景节点VMID与台账不一致，禁止回收");
            }
            recycle(lease);
            return;
        }
        List<VendorThirdVmLedger.Lease> retired = ledger.forSceneElement(sceneId, elementId);
        Optional<VendorThirdVmLedger.Lease> terminal = retired.stream().filter(row ->
                (StringUtils.isBlank(vmUuid) || String.valueOf(row.getVmid()).equals(vmUuid))
                && (VendorThirdVmLedger.DELETED.equals(row.getStatus()) || VendorThirdVmLedger.REJECTED.equals(row.getStatus()))).findFirst();
        if (terminal.isPresent()) {
            if (readInventory(true).hasName(terminal.get().getRequestName()))
                throw new MpRuntimeException("厂商三已结案资源名称再次出现，需核查归属");
            return;
        }
        if (StringUtils.isBlank(vmUuid)) {
            if (!retired.isEmpty()) throw new MpRuntimeException("厂商三历史节点仍有未结案台账，保留关联待核查");
            // Old failed creations have no VMID and no journal. Finish the local
            // node without guessing another VM's identity or sending an empty ID.
            readInventory(true);
            log.warn("厂商三历史节点无VMID及资源台账，完成本地节点回收，scene={}, element={}", sceneId, elementId);
            return;
        }
        int id = parseVmid(vmUuid);
        // Legacy records may share the same VMID. Absence is idempotent, but
        // an existing resource without the original request name must not be deleted.
        if (readInventory(true).find(id) == null) return;
        throw new MpRuntimeException("厂商三历史资源仍存在但原始身份未确认，需核对归属后回收，vmid=" + id);
    }

    @Override
    public void deleteVm(String vmUuid) {
        int id = parseVmid(vmUuid);
        VendorThirdVmLedger.Lease lease = ledger.byId(id);
        if (lease != null && !VendorThirdVmLedger.DELETED.equals(lease.getStatus())
                && !VendorThirdVmLedger.REJECTED.equals(lease.getStatus())) {
            recycle(lease);
            return;
        }
        if (readInventory(true).find(id) != null) throw new MpRuntimeException("厂商三资源身份未确认，未提交删除");
    }

    @Override
    public Map<String, String> readReclaimInventory() {
        Map<String, String> names = new LinkedHashMap<>();
        readInventory(true).vms.forEach((id, vm) -> {
            if (VendorThirdInventory.isQemu(vm) && vm.getIntValue("template") != 1) {
                names.put(String.valueOf(id), vm.getString("name"));
            }
        });
        return names;
    }

    @Override
    public boolean reconcileAbsentVm(String vmUuid, String sceneId, String elementId) {
        VendorThirdVmLedger.Lease lease = ledger.byId(parseVmid(vmUuid));
        if (lease == null || !sceneId.equals(lease.getSceneId()) || !elementId.equals(lease.getElementId())
                || VendorThirdVmLedger.CREATING.equals(lease.getStatus())
                || VendorThirdVmLedger.DELETED.equals(lease.getStatus()) || VendorThirdVmLedger.REJECTED.equals(lease.getStatus())) return false;
        VendorThirdInventory current = readInventory(true);
        if (current.find(lease.getVmid()) != null || current.hasName(lease.getRequestName())) return false;
        return confirmAbsent(ledger.beginDelete(lease), current);
    }

    @Override
    public boolean matchesReclaimIdentity(String vmUuid, String name, String sceneId, String elementId) {
        if (StringUtils.isBlank(name) || sceneId == null || elementId == null) return false;
        VendorThirdVmLedger.Lease lease = ledger.byId(parseVmid(vmUuid));
        return lease != null && name.equals(lease.getRequestName())
                && sceneId.equals(lease.getSceneId()) && elementId.equals(lease.getElementId())
                && !VendorThirdVmLedger.CREATING.equals(lease.getStatus());
    }

    @Override
    public void reclaimClosedVm(String vmUuid, String name, String sceneId, String elementId) {
        if (!matchesReclaimIdentity(vmUuid, name, sceneId, elementId))
            throw new MpRuntimeException("厂商三残留资源无可核实的原始台账");
        VendorThirdVmLedger.Lease lease = ledger.byId(parseVmid(vmUuid));
        VendorThirdInventory inventory = readInventory(true);
        JSONObject vm = inventory.find(lease.getVmid());
        if (vm == null && !inventory.hasName(name)) return;
        if (!inventory.owns(lease, vm)) throw new MpRuntimeException("厂商三残留资源身份已变化");
        lease = ledger.prepareOrphanReclaim(lease, System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1));
        recycle(lease);
    }

    /** Read-only reconciliation of provider resources; never starts, clones or deletes a VM. */
    public void reconcileVmLedger() {
        List<VendorThirdVmLedger.Lease> rows = ledger.list();
        long cutoff = System.currentTimeMillis() - cloneTimeoutMillis() - networkTimeoutMillis()
                - readyTimeoutMillis() - TimeUnit.MINUTES.toMillis(10);
        boolean needInventory = rows.stream().anyMatch(row ->
                VendorThirdVmLedger.PENDING.equals(row.getStatus()) || VendorThirdVmLedger.DELETING.equals(row.getStatus())
                        || (VendorThirdVmLedger.CREATING.equals(row.getStatus()) && row.getUpdatedAt() != null && row.getUpdatedAt().getTime() < cutoff));
        VendorThirdInventory inventory = needInventory ? readInventory(true) : null;
        for (VendorThirdVmLedger.Lease row : rows) {
            try {
                if (VendorThirdVmLedger.DELETING.equals(row.getStatus())) {
                    confirmAbsent(row, inventory);
                } else if (VendorThirdVmLedger.READY.equals(row.getStatus())) {
                    // No repeated provider configuration query for every already-ready VM.
                    ledger.syncRuntimePort(row, row.getIp(), row.getMac(), StringUtils.defaultString(row.getNetmask()), StringUtils.defaultString(row.getGateway()), true);
                } else {
                    if (VendorThirdVmLedger.CREATING.equals(row.getStatus()) && inventory != null
                            && row.getUpdatedAt() != null && row.getUpdatedAt().getTime() < cutoff) {
                        if ("ALLOCATED".equals(row.getStage()) && inventory.find(row.getVmid()) == null
                                && !inventory.hasName(row.getRequestName())) {
                            ledger.retireUnsubmitted(row, cutoff);
                            continue;
                        }
                        if (!ledger.staleCreating(row, cutoff)) continue;
                        row = ledger.byId(row.getVmid());
                    }
                    if (!VendorThirdVmLedger.PENDING.equals(row.getStatus())) continue;
                    JSONObject vm = ownedVm(inventory, row);
                    if (power(vm) != VmPowerState.RUNNING) continue;
                    VendorThirdNic nic = VendorThirdNic.parse(configOf(vm, row.getVmid(), row.getRequestName()));
                    if (targetNetwork(nic) && nic.hasAddress() && ledger.lateReady(row, nic.getIp(), nic.getMac(), nic.getNetmask(), nic.getGateway())) {
                        ledger.syncRuntimePort(row, nic.getIp(), nic.getMac(), StringUtils.defaultString(nic.getNetmask()), StringUtils.defaultString(nic.getGateway()));
                    }
                }
            } catch (RuntimeException e) {
                log.warn("厂商三 VM {} 台账核对待处理：{}", row.getVmid(), e.getMessage());
            }
        }
    }

    @Override
    public String getConnection(String vmUuid) {
        JSONObject vm = readPowerVm(vmUuid);
        String node = checkedNode(vm);
        String cookie = getHeaders().get("Cookie");
        if (cookie == null || !cookie.startsWith("HCICookie=")) throw new MpRuntimeException("厂商三控制台认证失败");
        return getClient().getServer() + "/?console=kvm&novnc=1&vmid=" + vmUuid + "&node=" + node
                + "&resize=off&cmd=&token=" + Base64.getEncoder().encodeToString(cookie.substring("HCICookie=".length()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    @Override
    public boolean ipAutoGen() { return false; }
}
