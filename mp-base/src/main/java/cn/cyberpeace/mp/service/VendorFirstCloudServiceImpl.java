package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.base.util.http.MultiCloudClient;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


@Service
@AllArgsConstructor
@Slf4j
public class VendorFirstCloudServiceImpl extends AbstractVendorCloudService {
    private final MultiCloudClient multiCloudClient;
    private final VendorFirstVmLedger ledger;
    private static final String INSTANCE_PATH = "/api/cloud/instances/";
    private static final String CLUSTER_QUERY = "cluster=382ee7c7-336a-41ae-b71e-9c14365bdcd3";
    public static final String DEVICE_ACTIVE = "active";
    public static final String IMAGE_CREATE_TEMPLATE = "{\n" +
            "  \"name\": \"sn_test4\",\n" +
            "  \"az\": \"nova\",\n" +
            "  \"flavor\": {\"id\":\"4\",\"name\":\"m1.large\",\"vcpus\":4,\"ram\":8192,\"disk\":80,\"swap\":0,\"ephemeral\":0,\"is_public\":true,\"extra_specs\":{}},\n" +
            "  \"flavor_id\": \"4\",\n" +
            "  \"image\": {\n" +
            "    \"id\": \"0dbbe49d-3923-4a3d-9b47-6ffd7d9a6012\",\n" +
            "    \"uuid\": \"0dbbe49d-3923-4a3d-9b47-6ffd7d9a6012\",\n" +
            "    \"name\": \"ubuntu-24.04.2-live-server-arm\",\n" +
            "    \"image_type\": \"image\",\n" +
            "    \"image_type_display\": \"镜像\",\n" +
            "    \"status\": \"active\",\n" +
            "    \"status_display\": \"可用\",\n" +
            "    \"min_disk\": 0,\n" +
            "    \"min_ram\": 0,\n" +
            "    \"size\": 2922393600,\n" +
            "    \"disk_format\": \"iso\",\n" +
            "    \"protected\": false,\n" +
            "    \"visibility\": \"private\",\n" +
            "    \"architecture\": \"aarch64\",\n" +
            "    \"hw_video_model\": null,\n" +
            "    \"hw_qemu_guest_agent\": \"no\",\n" +
            "    \"hw_cdrom_bus\": null,\n" +
            "    \"os_distro\": \"ubuntu\",\n" +
            "    \"os_type\": \"linux\",\n" +
            "    \"os_admin_user\": \"ubuntu\",\n" +
            "    \"is_stable\": true,\n" +
            "    \"delete_enabled\": true,\n" +
            "    \"tags\": [],\n" +
            "    \"properties\": {\n" +
            "      \"hw_boot_menu\": \"True\",\n" +
            "      \"hw_qemu_guest_agent\": \"no\",\n" +
            "      \"os_admin_user\": \"ubuntu\",\n" +
            "      \"os_type\": \"linux\",\n" +
            "      \"os_distro\": \"ubuntu\",\n" +
            "      \"os_hidden\": false,\n" +
            "      \"os_hash_algo\": \"sha512\",\n" +
            "      \"os_hash_value\": \"9795300d5de0fb6ee3816cba759ef6e19f7fda28772d4cebdb6c3749320c0ee17fd9a1bd2bdacba64133b3a66ac1cb84bd7a88883aac8410241f98a93c51ac40\",\n" +
            "      \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/0dbbe49d-3923-4a3d-9b47-6ffd7d9a6012/snap\",\n" +
            "      \"stores\": \"rbd\"\n" +
            "    },\n" +
            "    \"created_at\": \"2025-11-21 16:16\",\n" +
            "    \"updated_at\": \"2025-11-23 11:14\"\n" +
            "  },\n" +
            "  \"image_id\": \"0dbbe49d-3923-4a3d-9b47-6ffd7d9a6012\",\n" +
            "  \"networks\": [\n" +
            "    \"30fae181-2670-426e-8afe-f38b289e60bf\"\n" +
            "  ],\n" +
            "  \"driver_images\": [],\n" +
            "  \"ports\": [],\n" +
            "  \"sec_groups\": [],\n" +
            "  \"count\": 1,\n" +
            "  \"authMethod\": \"password\",\n" +
            "  \"password\": \"QWEasd123#\",\n" +
            "  \"keypair\": null,\n" +
            "  \"host\": null,\n" +
            "  \"boot_from_volume\": false,\n" +
            "  \"server_group\": null\n" +
            "}";

    private static volatile String TOKEN = "";
    private static final Object TOKEN_LOCK = new Object();

    @Override
    public Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-mix-root-tenant-id", "2");
        headers.put("x-mix-tenant-id", "2");
        headers.put("x-mix-tenant-name", "admin");
        headers.put("x-mix-tenant-type", "tenant");
        headers.put("x-mix-user-id", "2");
        headers.put("x-mix-user-name", "admin");
        return headers;
    }

    private String authToken(String rejectedToken) {
        synchronized (TOKEN_LOCK) {
            if (StringUtils.isNotBlank(TOKEN) && !Objects.equals(TOKEN, rejectedToken)) return TOKEN;
            HttpResult result = getClient().post("/api/uc/auth/token",
                    new VendorFirstTokenHttpData("{\"username\":\"admin\",\"password\":\"e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7\",\"platform\":false}"), null, null, 30000);
            JSONObject body = requireResponse(result, "认证");
            String token = body.getString("token");
            if (StringUtils.isBlank(token)) throw new MpRuntimeException("厂商一认证响应缺少Token");
            TOKEN = token;
            return token;
        }
    }

    private Map<String, String> authenticatedHeaders(String token) {
        Map<String, String> headers = new HashMap<>(getHeaders());
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }

    /** Retry authentication only after an explicit HTTP 401, not after an ambiguous write. */
    private HttpResult authenticated(Function<Map<String, String>, HttpResult> request) {
        String token = authToken(null);
        HttpResult response = request.apply(authenticatedHeaders(token));
        if (httpStatus(response) == 401) {
            response = request.apply(authenticatedHeaders(authToken(token)));
        }
        return response;
    }

    @Override
    public HttpClient getClient() {
        return multiCloudClient.getClient(1);
    }

    @Override
    public List<String> listAllDevices() {
        try {
            int index = 0;
             JSONObject vendorObject = requireResponse(getClient().get("/api/cloud/images/?page=1&page_size=10000&" + CLUSTER_QUERY,
                    null, getHeaders(), 30000), "镜像查询");
             List<String> images = new ArrayList<>();
            for (Object results : vendorObject.getJSONArray("results")) {
                JSONObject result = (JSONObject) results;
                String imageUuid = result.getString("uuid");
                boolean isActive = DEVICE_ACTIVE.equals(result.getString("status"));
                boolean isImage = "image".equals(result.getString("image_type"));
                String name = result.getString("name");
                String osType = result.getString("os_type");

                if (isActive && isImage) {
                    JSONObject device = JSONObject.parseObject(DEVICE_TEMPLATE);
                    device.put("id", index++);
                    device.put("vendor", getVendor());
                    device.put("uuid", imageUuid);
                    device.put("name", name);
                    device.put("image", name);
                    device.put("description", name);
                    if ("linux".equals(osType)) {
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
                    device.getJSONObject("ext_image_info").put("uuid", imageUuid);
                    device.getJSONObject("ext_image_info").put("base_image_name", name);

                    images.add(device.toJSONString());
                }
            }
            return images;
        } catch (Exception e) {
            log.error("获取厂商一靶标列表异常: " + e.getMessage(), e);
            return Collections.emptyList();
        }

    }

    @Override
    public int getVendor() {
        return 1;
    }

    @Override
    public String createVm(String imageUuid) {
        return submitCreate(imageUuid, "MP_" + TextUtils.uuid(), null);
    }

    @Override
    public String createSceneVm(String imageUuid, String sceneId, String elementId) {
        VendorFirstVmLedger.Lease existing = ledger.byOwner(sceneId, elementId);
        if (existing != null) {
            if (!imageUuid.equals(existing.getImage())) throw new MpRuntimeException("厂商一节点已有不同镜像的创建关联");
            if (StringUtils.isNotBlank(existing.getUuid())) return existing.getUuid();
            String recovered = uniqueRequestVm(existing, readInstances(null));
            if (recovered == null) throw new MpRuntimeException("厂商一原创建请求结果仍待确认，未重复提交，request_name=" + existing.getName());
            ledger.created(existing, recovered);
            return recovered;
        }
        // Validate the image before creating any persistent in-flight record.
        requireVmId(imageUuid);
        JSONObject image = getImageInfoByImageUuid(imageUuid);
        VendorFirstVmLedger.Lease lease = ledger.reserve(sceneId, elementId, imageUuid, "MP_" + TextUtils.uuid());
        return submitPreparedCreate(imageUuid, image, lease.getName(), lease);
    }

    private String submitCreate(String imageUuid, String name, VendorFirstVmLedger.Lease lease) {
        requireVmId(imageUuid);
        JSONObject image = getImageInfoByImageUuid(imageUuid);
        return submitPreparedCreate(imageUuid, image, name, lease);
    }

    private String submitPreparedCreate(String imageUuid, JSONObject image, String name, VendorFirstVmLedger.Lease lease) {
        JSONObject payload = JSONObject.parseObject(IMAGE_CREATE_TEMPLATE);
        payload.put("name", name);
        payload.put("image_id", imageUuid);
        payload.put("image", image);
        VendorFirstVmCreateHttpData data = new VendorFirstVmCreateHttpData(payload);

        // A malformed or lost response can occur after the VM has been created.
        // Do not repeat the create request with a new name in that case.
        try {
            HttpResult response = authenticated(headers -> getClient().post(INSTANCE_PATH + "?" + CLUSTER_QUERY,
                    data, null, headers, 30000));
            JSONArray instances = requireResponse(response, "创建").getJSONArray("instances");
            if (instances == null || instances.size() != 1 || !(instances.get(0) instanceof String)) {
                throw new MpRuntimeException("厂商一创建响应未确认唯一UUID");
            }
            String id = instances.getString(0);
            requireVmId(id);
            if (lease != null) ledger.created(lease, id);
            return id;
        } catch (RuntimeException e) {
            if (lease != null) ledger.uncertain(lease);
            log.warn("厂商一创建结果未确认，request_name={}，请核查原请求", name);
            throw new MpRuntimeException("厂商一创建结果未确认，request_name=" + name + "，" + e.getMessage()).e(e);
        }
    }

    private JSONObject getImageInfoByImageUuid(String imageUuid) {
        JSONObject inventory = requireResponse(getClient().get("/api/cloud/images/?page=1&page_size=10000&" + CLUSTER_QUERY,
                null, getHeaders(), 30000), "镜像查询");
        JSONArray images = inventory.getJSONArray("results");
        if (images == null) throw new MpRuntimeException("厂商一镜像清单缺失");
        for (Object entry : images) {
            if (!(entry instanceof JSONObject)) continue;
            JSONObject result = (JSONObject) entry;
            if (imageUuid.equals(result.getString("uuid"))) {
                if (!DEVICE_ACTIVE.equalsIgnoreCase(result.getString("status"))
                        || !"image".equals(result.getString("image_type"))) {
                    throw new MpRuntimeException("厂商一指定镜像不可用");
                }
                return result;
            }
        }
        throw new MpRuntimeException("厂商一指定镜像未找到");
    }

    private JSONObject findInstance(String vmUuid) {
        requireVmId(vmUuid);
        return readInstances(vmUuid).get(vmUuid);
    }

    /** A null target reads the complete inventory; ordinary status queries may stop at the exact UUID. */
    private Map<String, JSONObject> readInstances(String target) {
        final int size = 1000;
        Map<String, JSONObject> seen = new LinkedHashMap<>();
        for (int page = 1; page <= 100; page++) {
            String url = INSTANCE_PATH + "?page=" + page + "&page_size=" + size + "&" + CLUSTER_QUERY;
            JSONObject result = requireResponse(authenticated(headers -> getClient().get(url, null, headers, 30000)), "状态查询");
            JSONArray instances = result.getJSONArray("results");
            if (instances == null) throw new MpRuntimeException("厂商一虚拟机清单缺失");
            for (Object entry : instances) {
                if (!(entry instanceof JSONObject)) throw new MpRuntimeException("厂商一虚拟机清单格式异常");
                JSONObject instance = (JSONObject) entry;
                String id = instance.getString("uuid");
                if (StringUtils.isBlank(id) || seen.putIfAbsent(id, instance) != null) throw new MpRuntimeException("厂商一虚拟机清单不完整或存在重复记录");
                if (target != null && target.equals(id)) return seen;
            }
            Integer count = result.getInteger("count");
            boolean more = StringUtils.isNotBlank(result.getString("next"));
            if (count != null) {
                if (count < seen.size()) throw new MpRuntimeException("厂商一虚拟机清单数量异常");
                if (count == seen.size() && !more) return seen;
                if (instances.isEmpty()) throw new MpRuntimeException("厂商一虚拟机清单不完整");
            } else if (!more && instances.size() < size) {
                return seen;
            } else if (instances.isEmpty()) {
                throw new MpRuntimeException("厂商一虚拟机清单不完整");
            }
        }
        throw new MpRuntimeException("厂商一虚拟机清单未完整读取");
    }

    @Override
    public Map<String, String> readReclaimInventory() {
        Map<String, String> names = new LinkedHashMap<>();
        readInstances(null).forEach((id, vm) -> names.put(id, vm.getString("name")));
        return names;
    }

    @Override
    public boolean matchesReclaimIdentity(String vmUuid, String name, String sceneId, String elementId) {
        return MultiCloudService.isProviderUuid(vmUuid) && MultiCloudService.isMpCreatedName(name);
    }

    @Override
    public void reclaimClosedVm(String vmUuid, String name, String sceneId, String elementId) {
        if (!matchesReclaimIdentity(vmUuid, name, sceneId, elementId))
            throw new MpRuntimeException("厂商一残留资源身份未确认");
        Map<String, String> current = readReclaimInventory();
        long sameName = current.values().stream().filter(name::equals).count();
        if (!current.containsKey(vmUuid) && sameName == 0) return;
        if (!name.equals(current.get(vmUuid)) || sameName != 1)
            throw new MpRuntimeException("厂商一残留资源身份已变化或名称不唯一");
        deleteManagedVm(vmUuid, sceneId, elementId);
    }

    @Override
    public String[] getVmIpAndMac(String vmUuid) {
        requireVmId(vmUuid);
        try {
            JSONObject instance = findInstance(vmUuid);
            if (power(instance) != VmPowerState.RUNNING) return null;
            JSONObject addresses = instance.getJSONObject("address");
            JSONObject network = addresses == null ? null : addresses.getJSONObject("network1");
            JSONArray fixed = network == null ? null : network.getJSONArray("fixed");
            if (fixed == null) return null;
            for (Object item : fixed) {
                if (!(item instanceof JSONObject)) continue;
                JSONObject entry = (JSONObject) item;
                String ip = entry.getString("addr");
                String mac = entry.getString("OS-EXT-IPS-MAC:mac_addr");
                try {
                    long numeric = VendorSecondIpRange.number(ip);
                    if (numeric != 0 && numeric != 0xffffffffL && mac != null
                            && mac.matches("(?i)([0-9a-f]{2}:){5}[0-9a-f]{2}")) return new String[]{ip, mac};
                } catch (IllegalArgumentException ignored) { }
            }
            return null;
        } catch (RuntimeException e) {
            log.warn("厂商一 VM {} 地址暂未确认：{}", vmUuid, e.getMessage());
            return null;
        }
    }

    private VmPowerState power(JSONObject instance) {
        String status = instance == null ? null : instance.getString("status");
        if ("ACTIVE".equalsIgnoreCase(status)) return VmPowerState.RUNNING;
        if ("SHUTOFF".equalsIgnoreCase(status)) return VmPowerState.STOPPED;
        return VmPowerState.UNKNOWN;
    }

    @Override
    public VmPowerState getVmPowerState(String vmUuid) {
        return power(findInstance(vmUuid));
    }

    @Override
    protected void submitPowerAction(String vmUuid, VmPowerState target) {
        requireVmId(vmUuid);
        String url = INSTANCE_PATH + vmUuid + "/action/?" + CLUSTER_QUERY;
        JSONObject payload = new JSONObject();
        payload.put("action", target == VmPowerState.RUNNING ? "power_on" : "power_off");
        StringHttpData body = new StringHttpData(payload.toJSONString());
        JSONObject result = requireResponse(authenticated(headers ->
                getClient().post(url, body, null, headers, 30000)), "电源操作");
        if (!Boolean.TRUE.equals(result.getBoolean("success"))) {
            throw new MpRuntimeException("厂商一电源操作业务未确认，uuid=" + vmUuid);
        }
    }

    private int httpStatus(HttpResult response) {
        return response == null || response.getResponse() == null || response.getException() != null
                ? 0 : response.getResponse().getStatusLine().getStatusCode();
    }

    private JSONObject requireResponse(HttpResult response, String operation) {
        if (response == null || response.getException() != null || !response.isSuccess()) {
            throw new MpRuntimeException("厂商一" + operation + "请求未确认，HTTP=" + httpStatus(response));
        }
        JSONObject result;
        try { result = JSONObject.parseObject(response.getResult()); }
        catch (RuntimeException e) { throw new MpRuntimeException("厂商一" + operation + "响应格式异常").e(e); }
        if (result == null || Boolean.FALSE.equals(result.getBoolean("success"))
                || (result.getInteger("code") != null && result.getInteger("code") >= 400)) {
            throw new MpRuntimeException("厂商一" + operation + "业务失败");
        }
        return result;
    }

    protected long deleteConfirmTimeoutMillis() { return 30000; }
    protected long deleteConfirmPollMillis() { return 2000; }

    private String uniqueRequestVm(VendorFirstVmLedger.Lease lease, Map<String, JSONObject> inventory) {
        List<String> matches = new ArrayList<>();
        inventory.forEach((id, vm) -> { if (lease.getName().equals(vm.getString("name"))) matches.add(id); });
        if (matches.size() > 1) throw new MpRuntimeException("厂商一原创建名称不唯一，保留关联待核查");
        return matches.isEmpty() ? null : matches.get(0);
    }

    @Override
    public boolean reconcileAbsentVm(String vmUuid, String sceneId, String elementId) {
        VendorFirstVmLedger.Lease lease = ledger.byOwner(sceneId, elementId);
        if (lease == null || !vmUuid.equals(lease.getUuid())) return false;
        Map<String, JSONObject> current = readInstances(null);
        if (current.containsKey(vmUuid) || uniqueRequestVm(lease, current) != null) return false;
        ledger.deleted(lease);
        return true;
    }

    @Override
    public void deleteManagedVm(String vmUuid, String sceneId, String elementId) {
        VendorFirstVmLedger.Lease lease = ledger.byOwner(sceneId, elementId);
        if (lease != null) {
            if (StringUtils.isNotBlank(vmUuid) && StringUtils.isNotBlank(lease.getUuid()) && !vmUuid.equals(lease.getUuid()))
                throw new MpRuntimeException("厂商一场景资源UUID与创建台账不一致");
            if (StringUtils.isBlank(lease.getUuid())) {
                String recovered = uniqueRequestVm(lease, readInstances(null));
                if (recovered == null) throw new MpRuntimeException("厂商一原创建请求尚未核实，保留场景关联");
                if (StringUtils.isNotBlank(vmUuid) && !vmUuid.equals(recovered))
                    throw new MpRuntimeException("厂商一场景UUID与原创建名称不一致");
                ledger.created(lease, recovered);
                vmUuid = recovered;
            } else {
                vmUuid = lease.getUuid();
            }
        }
        if (StringUtils.isBlank(vmUuid)) {
            readInstances(null);
            log.warn("厂商一历史节点无UUID及创建台账，完成本地节点回收，scene={}, element={}", sceneId, elementId);
            return;
        }
        deleteVm(vmUuid);
        if (lease != null) ledger.deleted(lease);
    }

    @Override
    public void deleteVm(String vmUuid) {
        requireVmId(vmUuid);
        HttpResult response = authenticated(headers -> getClient().delete(INSTANCE_PATH + vmUuid + "/?" + CLUSTER_QUERY,
                null, null, headers, 30000));
        // Only a structured not-found response from the requested instance
        // is idempotent. A proxy/server 404 or auth failure is not proof.
        if (httpStatus(response) == 404) {
            try {
                JSONObject body = JSONObject.parseObject(response.getResult());
                if (body != null && Integer.valueOf(404).equals(body.getInteger("code"))
                        && "NotFound".equals(body.getString("error"))
                        && StringUtils.contains(body.getString("message"), vmUuid)) return;
            } catch (RuntimeException ignored) { }
            throw new MpRuntimeException("厂商一删除结果未确认，HTTP=404");
        }
        if (response == null || response.getException() != null || !response.isSuccess()) {
            throw new MpRuntimeException("厂商一删除结果未确认，HTTP=" + httpStatus(response));
        }
        if (StringUtils.isNotBlank(response.getResult())) requireResponse(response, "删除");
        long until = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(deleteConfirmTimeoutMillis());
        do {
            try {
                if (findInstance(vmUuid) == null) return;
            } catch (RuntimeException e) {
                log.warn("厂商一 VM {} 回收状态暂不可确认", vmUuid);
            }
            long remaining = TimeUnit.NANOSECONDS.toMillis(until - System.nanoTime());
            if (remaining <= 0) break;
            try { Thread.sleep(Math.min(remaining, deleteConfirmPollMillis())); }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MpRuntimeException("厂商一删除核对中断，保留资源关联").e(e);
            }
        } while (System.nanoTime() < until);
        throw new MpRuntimeException("厂商一资源删除尚未确认，保留UUID=" + vmUuid);
    }

    @Override
    public String getConnection(String vmUuid) {
        requireVmId(vmUuid);
        String url = INSTANCE_PATH + vmUuid + "/vnc-console/?" + CLUSTER_QUERY;
        String result = requireResponse(authenticated(headers -> getClient().get(url, null, headers, 30000)), "控制台查询")
                .getString("vnc_url");
        if (StringUtils.isBlank(result)) throw new MpRuntimeException("厂商一控制台地址未确认");
        return result;
    }
}
