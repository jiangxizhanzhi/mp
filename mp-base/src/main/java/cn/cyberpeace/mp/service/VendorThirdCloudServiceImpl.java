package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.MultiCloudClient;
import cn.cyberpeace.mp.common.xd.XdThreeCookieRequest;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import java.util.*;
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
        JSONObject body = JSONObject.parseObject("{\n" +
                "\"newid\": 104,\n" +
                "\"name\": \"temp-2\",\n" +
                "\"target\": \"hci\"\n" +
                "}");
        body.put("name", "MP_CREATE_" + TextUtils.uuid());
        body.put("newid", newId);
        body.put("target", templateNode);


        StringHttpData createVmObject = new StringHttpData(body.toJSONString());
        String result = getClient().post(url, createVmObject, null, getHeaders(), 30000).getResult();

        log.info("创建虚机结果： {}", result);

        String vmNode = getVmNode(String.valueOf(newId));

        try {
            Thread.sleep(120*1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        getClient().post("/api2/json/nodes/" + vmNode + "/qemu/" + newId + "/status/start", null, null, getHeaders(), 30000);

//        try {
//            Thread.sleep(180*1000);
//        } catch (InterruptedException e) {
//            log.error(e.getMessage(), e);
//        }

        String resultConfig = getClient().get("/api2/json/nodes/" + vmNode + "/qemu/" + newId + "/config", null, getHeaders(), 30000).getResult();
        log.info("查询config的结果： {}", resultConfig);

        if (JSONObject.parseObject(resultConfig).getJSONObject("data") != null && StringUtils.isNotEmpty(JSONObject.parseObject(resultConfig).getJSONObject("data").getString("digest"))) {
            String digest = JSONObject.parseObject(resultConfig).getJSONObject("data").getString("digest");

            String networkStr = "{\"digest\":\"" + digest + "\",\"vnet0\":\"virtio,network=36eca7b1-5fe7-48a5-be8e-19f1a01017d2\"}";

            String putConfigResult = getClient().put("/api2/json/nodes/" + vmNode + "/qemu/" + newId + "/config", new StringHttpData(networkStr), null, getHeaders(), 30000).getResult();
            log.info("配置config的结果： {}", putConfigResult);
        }


        return newId + "";
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
