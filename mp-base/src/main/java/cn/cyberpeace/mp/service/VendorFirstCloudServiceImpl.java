package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.MultiCloudClient;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@AllArgsConstructor
@Slf4j
public class VendorFirstCloudServiceImpl implements MultiCloudService {
    private final MultiCloudClient multiCloudClient;
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

    @Override
    public HttpClient getClient() {
        return multiCloudClient.getClient(1);
    }

    @Override
    public List<String> listAllDevices() {
        try {
            int index = 0;
             JSONObject vendorObject = JSONObject.parseObject(getClient().get("/api/cloud/images/?page=1&page_size=10000&cluster=382ee7c7-336a-41ae-b71e-9c14365bdcd3",
                    null, getHeaders(), 30000).getResult());
             List<String> images = new ArrayList<>();
            for (Object results : vendorObject.getJSONArray("results")) {
                JSONObject result = (JSONObject) results;
                String imageUuid = result.getString("uuid");
                boolean isActive = DEVICE_ACTIVE.equals(result.getString("active"));
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
        JSONObject jsonObject = JSONObject.parseObject(IMAGE_CREATE_TEMPLATE);
        jsonObject.put("name", "MP_" + TextUtils.uuid());
        jsonObject.put("image_id", imageUuid);
        jsonObject.put("image", getImageInfoByImageUuid(imageUuid));

        VendorFirstVmCreateHttpData vendorFirstVmCreateHttpData = new VendorFirstVmCreateHttpData(jsonObject);

        String result = getClient().post("/api/cloud/instances/?cluster=382ee7c7-336a-41ae-b71e-9c14365bdcd3", vendorFirstVmCreateHttpData, null, getHeaders(), 30000)
                .getResult();
        if (result != null) {
            JSONObject createResult = JSONObject.parseObject(result);
            return createResult.getJSONArray("instances").get(0).toString();
        }
        return "";
    }

    private JSONObject getImageInfoByImageUuid(String imageUuid) {
        JSONObject vendorObject = JSONObject.parseObject(getClient().get("/api/cloud/images/?page=1&page_size=10000&cluster=382ee7c7-336a-41ae-b71e-9c14365bdcd3",
                null, getHeaders(), 30000).getResult());
        for (Object results : vendorObject.getJSONArray("results")) {
            JSONObject result = (JSONObject) results;
            if (imageUuid.equals(result.getString("uuid"))) {
                return result;
            }

        }
        return null;
    }

    @Override
    public String[] getVmIpAndMac(String vmUuid) {
        try {
            String result = getClient().get("/api/cloud/instances/?page=1&page_size=1000&cluster=382ee7c7-336a-41ae-b71e-9c14365bdcd3", null, getHeaders(), 30000)
                    .getResult();
            JSONObject jsonObject = JSONObject.parseObject(result);
            for (Object vmObject : jsonObject.getJSONArray("results")) {
                JSONObject vmJsonObject = (JSONObject) vmObject;
                if (vmUuid.equals(vmJsonObject.getString("uuid"))) {
                    JSONObject finalObject = vmJsonObject.getJSONObject("address")
                            .getJSONObject("network1").getJSONArray("fixed")
                            .getJSONObject(0);
                    String ip = finalObject.getString("addr");
                    String mac = finalObject.getString("OS-EXT-IPS-MAC:mac_addr");
                    return new String[]{ip, mac};
                }
            }
            return null;
        } catch (Exception e){
            log.error("获取vm ip和mac异常: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void deleteVm(String vmUuid) {
        try {
            getClient().delete("/api/cloud/instances/" + vmUuid + "/?cluster=382ee7c7-336a-41ae-b71e-9c14365bdcd3", null, null, getHeaders(), 30000);
        } catch (Exception e) {
            log.error("删除vm异常: " + e.getMessage(), e);
        }
    }


}
