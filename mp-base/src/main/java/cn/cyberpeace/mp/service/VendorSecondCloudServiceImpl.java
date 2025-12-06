package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.base.util.http.MultiCloudClient;
import cn.cyberpeace.mp.base.util.http.XdTwoHttpClient;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class VendorSecondCloudServiceImpl implements MultiCloudService {
    private final MultiCloudClient multiCloudClient;

    private static final ThreadLocal<VendorSecondClientContext> vendorClientContext = new ThreadLocal<>();
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";
    private static final Integer IP_POOL_START = 125;

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
            "            \"device_id\": \"c7fbd868-e1f6-480c-851d-7b387cee13ea\",\n" +
            "            \"ip_info\": {\n" +
            "                \"ip_address\": \"192.168.140.199\",\n" +
            "                \"netmask\": \"255.255.255.0\",\n" +
            "                \"gateway\": \"192.168.140.254\"\n" +
            "            },\n" +
            "            \"port_id\": \"b2bf40b4-2637-4849-b2f2-e3b37236d16b\"\n" +
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
        try {
            JSONObject createVmObject = JSONObject.parseObject(VM_CREATE_TEMPLATE);
            createVmObject.put("image_id", imageUuid);

            JSONObject image = getTargetImage(imageUuid);
            createVmObject.put("name", "MP_" + TextUtils.uuid());

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

            StringHttpData body = new StringHttpData(createVmObject.toJSONString());
            String url = "/janus/20180725/servers";
            vendorClientContext.set(new VendorSecondClientContext(url, METHOD_POST, body));
            JSONObject result = JSONObject.parseObject(getClient().post(url, body, null, getHeaders(), 30000).getResult());
            log.info("厂商二：创建虚拟机结果: " + result);
            return  result.getJSONObject("data").getJSONArray("uuids").getString(0);
        } finally {
            vendorClientContext.remove();
        }
    }

    private JSONObject getTargetImage(String imageUuid) {
        try {
            String url = "/janus/20180725/images";
            vendorClientContext.set(new VendorSecondClientContext(url, METHOD_GET, null));
            JSONObject jsonObject = JSONObject.parseObject(getClient().get(url, null, getHeaders(), 30000).getResult());
            List<String> images = new ArrayList<>();

            for (Object o : jsonObject.getJSONObject("data").getJSONArray("data")) {
                JSONObject image = (JSONObject) o;
                String id = image.getString("id");
                if (id.equals(imageUuid)) {
                    return image;
                }

            }
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("未找到指定镜像"));

        } finally {
            vendorClientContext.remove();
        }
    }

    @Override
    public String[] getVmIpAndMac(String vmUuid) {
        try {
            String url = "/janus/20180725/servers";
            vendorClientContext.set(new VendorSecondClientContext(url, METHOD_GET, null));
            HttpResult httpResult = getClient().get(url, null, getHeaders(), 30000);

            JSONArray jsonArray = JSONObject.parseObject(httpResult.getResult())
                    .getJSONObject("data").getJSONArray("data");
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                if (jsonObject.getString("id").equals(vmUuid)) {
                    JSONObject network = (JSONObject) jsonObject.getJSONArray("networks").get(0);
                    String ip = network.getString("ip_address");
                    String mac = network.getString("mac_address");
                    return new String[]{ip, mac};
                }
            }
            return new String[2];

        } finally {
            vendorClientContext.remove();
        }
    }

    @Override
    public void deleteVm(String vmUuid) {
        try {
            String url = "/janus/20180725/servers/" + vmUuid;
            VendorSecondVmDeleteHttpData req = new VendorSecondVmDeleteHttpData();
            vendorClientContext.set(new VendorSecondClientContext(url, METHOD_DELETE, req));
            HttpResult httpResult = getClient().delete(url, req, null, getHeaders(), 30000);
            log.info("delete vm result = {}", httpResult.getResult());
        } finally {
            vendorClientContext.remove();
        }
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
}
