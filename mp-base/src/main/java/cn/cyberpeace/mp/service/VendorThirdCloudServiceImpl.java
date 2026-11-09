package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
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
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@AllArgsConstructor
public class VendorThirdCloudServiceImpl implements MultiCloudService {
    private final MultiCloudClient multiCloudClient;
    private static String TOKEN = "";
    private static AtomicInteger maxId = new AtomicInteger(1000);

    @Override
    public Map<String, String> getHeaders() {
        String result = getClient().post("/api2/json/access/ticket", new XdThreeCookieRequest(), null, new HashMap<>(), 30000).getResult();
        HashMap<String, String> map = new HashMap<>();
        TOKEN = JSONObject.parseObject(result).getJSONObject("data").getString("ticket");
        map.put("Cookie", "HCICookie=" + TOKEN);
        return map;
    }

    @Override
    public HttpClient getClient() {
        return multiCloudClient.getClient(getVendor());
    }

    @Override
    public synchronized List<String> listAllDevices() {

        try {
            int index = 0;
            String url = "/api2/json/cluster/resources/";
            JSONObject jsonObject = JSONObject.parseObject(getClient().get(url, null, getHeaders(), 30000).getResult());
            List<String> images = new ArrayList<>();

            for (Object o : jsonObject.getJSONArray("data")) {
                JSONObject image = (JSONObject) o;
                boolean isTemplate = 1 == Optional.ofNullable(image.getInteger("template")).orElse(0);
                String name = Optional.ofNullable(image.getString("name")).orElse("");


                if (isTemplate) {
                    int vmId = image.getInteger("vmid");
                    maxId = new AtomicInteger(Math.max(maxId.get(), vmId));
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

    private final Object lock = new Object();

    @Override
    public String createVm(String imageUuid) {
        this.listAllDevices();
        int newId = maxId.incrementAndGet();
        String templateNode = getTemplateNode(imageUuid);

        String url = "/api2/json/nodes/" + templateNode + "/qemu/" + imageUuid + "/clone";
        JSONObject body = new JSONObject();
        body.put("name", "MP_CREATE_" + TextUtils.uuid());
        body.put("newid", newId);
        body.put("target", templateNode);


        StringHttpData createVmObject = new StringHttpData(body.toJSONString());
        JSONObject result = requireSuccess(getClient().post(url, createVmObject, null, getHeaders(), 30000), "提交克隆", newId);
        log.info("厂商三 VM {} 克隆请求已提交，node={}, UPID={}", newId, templateNode, result.getString("data"));

        waitForVmState(newId, templateNode, "stopped", TimeUnit.MINUTES.toMillis(10));

        String vmUrl = "/api2/json/nodes/" + templateNode + "/qemu/" + newId;
        JSONObject config = requireSuccess(getClient().get(vmUrl + "/config", null, getHeaders(), 30000), "读取配置", newId).getJSONObject("data");
        if (config == null || StringUtils.isEmpty(config.getString("digest"))) {
            throw new MpRuntimeException("厂商三 VM " + newId + " 配置缺少 digest");
        }
        String networkId = "36eca7b1-5fe7-48a5-be8e-19f1a01017d2";
        JSONObject network = new JSONObject();
        network.put("digest", config.getString("digest"));
        network.put("vnet0", "virtio,network=" + networkId);
        requireSuccess(getClient().put(vmUrl + "/config", new StringHttpData(network.toJSONString()), null, getHeaders(), 30000), "配置网卡", newId);

        JSONObject configured = requireSuccess(getClient().get(vmUrl + "/config", null, getHeaders(), 30000), "回读配置", newId).getJSONObject("data");
        String vnet0 = configured == null ? null : configured.getString("vnet0");
        if (StringUtils.isEmpty(vnet0) || !(vnet0.startsWith("virtio=") || vnet0.startsWith("virtio,"))
                || !Arrays.asList(vnet0.split(",")).contains("network=" + networkId)) {
            throw new MpRuntimeException("厂商三 VM " + newId + " 网卡配置回读与目标配置不一致");
        }
        log.info("厂商三 VM {} 网卡配置已回读：{}", newId, vnet0);

        JSONObject startResult = requireSuccess(getClient().post(vmUrl + "/status/start", null, null, getHeaders(), 30000), "提交启动", newId);
        log.info("厂商三 VM {} 启动请求已提交，node={}, UPID={}", newId, templateNode, startResult.getString("data"));
        waitForVmState(newId, templateNode, "running", TimeUnit.MINUTES.toMillis(10));
        return newId + "";
    }

    void waitForVmState(int vmId, String node, String expectedStatus, long timeoutMillis) {
        String stage = "stopped".equals(expectedStatus) ? "等待克隆关机就绪" : "等待启动运行";
        long started = System.nanoTime();
        while (true) {
            JSONObject vm = null;
            JSONObject resources = requireSuccess(getClient().get("/api2/json/cluster/resources/?type=vm", null, getHeaders(), 30000), stage, vmId);
            for (Object item : resources.getJSONArray("data")) {
                JSONObject resource = (JSONObject) item;
                if (resource.getIntValue("vmid") == vmId) {
                    vm = resource;
                    break;
                }
            }
            String status = vm == null ? null : vm.getString("status");
            String vmLock = vm == null ? null : vm.getString("lock");
            if (expectedStatus.equals(status) && (!"stopped".equals(expectedStatus) || StringUtils.isEmpty(vmLock))) {
                log.info("厂商三 VM {} {}完成，node={}, status={}, lock={}", vmId, stage, node, status, vmLock);
                return;
            }
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);
            if (elapsed >= timeoutMillis) {
                log.warn("厂商三 VM {} {}超时，继续后续流程，node={}, elapsedMs={}, found={}, status={}, lock={}；目标状态尚未确认",
                        vmId, stage, node, elapsed, vm != null, status, vmLock);
                return;
            }
            try {
                Thread.sleep(Math.min(2000, timeoutMillis - elapsed));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MpRuntimeException("厂商三 VM " + vmId + " " + stage + "被中断").e(e);
            }
        }
    }

    private JSONObject requireSuccess(HttpResult result, String stage, int vmId) {
        if (result.getException() != null || !result.isSuccess()) {
            throw new MpRuntimeException("厂商三 VM " + vmId + " " + stage + "请求失败，HTTP="
                    + (result.getResponse() == null ? "无响应" : result.getResponse().getStatusLine().getStatusCode())).e(result.getException());
        }
        JSONObject response = JSONObject.parseObject(result.getResult());
        if (response == null || response.getIntValue("success") != 1) {
            throw new MpRuntimeException("厂商三 VM " + vmId + " " + stage + "业务失败："
                    + (response == null ? "无有效响应" : response.getString("message")));
        }
        return response;
    }

    private String getVmNode(String vmId) {
        String result = getClient().get("/api2/json/cluster/resources/?type=vm", null, getHeaders(), 30000).getResult();
        for (Object o : JSONObject.parseObject(result).getJSONArray("data")) {
            JSONObject vm = (JSONObject) o;
            if (vm.getInteger("vmid").equals(Integer.parseInt(vmId))) {
                return vm.getString("node");
            }
        }
        return "hci22";
    }

    private String getTemplateNode(String vmId) {
        String result = getClient().get("/api2/json/cluster/resources/", null, getHeaders(), 30000).getResult();
        for (Object o : JSONObject.parseObject(result).getJSONArray("data")) {
            JSONObject vm = (JSONObject) o;
            boolean isTemplate = 1 == Optional.ofNullable(vm.getInteger("template")).orElse(0);
            if (isTemplate && vm.getInteger("vmid").equals(Integer.parseInt(vmId))) {
                return vm.getString("node");
            }
        }
        return "hci22";
    }

    @Override
    public String[] getVmIpAndMac(String vmUuid) {
        return new String[0];
    }

    @Override
    public void deleteVm(String vmUuid) {
        String node = getVmNode(vmUuid);

        String result = getClient().post("/api2/json/nodes/" + node + "/qemu/" + vmUuid + "/status/stop", null, null,  getHeaders(), 30000).getResult();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getClient().delete("/api2/extjs/nodes/" + node + "/qemu/" + vmUuid + "/", null, null,  getHeaders(), 30000).getResult();


    }

    @Override
    public String getConnection(String vmUuid) {
        String node = getVmNode(vmUuid);

        return getClient().getServer() + "/?console=kvm&novnc=1&vmid=" + vmUuid + "&node=" + node + "&resize=off&cmd=&token=" + Base64.getEncoder().encodeToString(TOKEN.getBytes());
    }

    @Override
    public boolean ipAutoGen() {
        return false;
    }
}
