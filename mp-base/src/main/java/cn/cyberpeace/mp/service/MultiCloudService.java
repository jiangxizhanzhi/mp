package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.http.HttpClient;

import java.util.List;
import java.util.Map;

public interface MultiCloudService {
    public static final String DEVICE_TEMPLATE = "{\n" +
            "                \"success\": false,\n" +
            "                \"request_id\": null,\n" +
            "                \"total\": 0,\n" +
            "                \"message\": null,\n" +
            "                \"message_zh\": null,\n" +
            "                \"error_message\": null,\n" +
            "                \"code\": null,\n" +
            "                \"tmpdir_uuid\": null,\n" +
            "                \"file_names\": null,\n" +
            "                \"file_name\": null,\n" +
            "                \"msg\": null,\n" +
            "                \"msg_zh\": null,\n" +
            "                \"url\": null,\n" +
            "                \"nginx_file_url\": null,\n" +
            "                \"id\": 212,\n" +
            "                \"uuid\": \"d8806f6a-7685-4c3a-8274-a9c0a36d3d04\",\n" +
            "                \"updated_at\": \"2025-08-19T07:26:20Z\",\n" +
            "                \"deleted\": false,\n" +
            "                \"created_at\": \"2025-08-19T07:26:20Z\",\n" +
            "                \"deleted_at\": null,\n" +
            "                \"name\": \"cb2b3901-ca5a-4bb8-8d45-b41998735c4aWin10-360-reskdesk-240321-v1\",\n" +
            "                \"image\": null,\n" +
            "                \"description\": \"Win10-basics-Lv\",\n" +
            "                \"logo\": \"/api/mp/file/295/427/4c764ad0d2c1a678d5a3c84f06076e74.png\",\n" +
            "                \"image_type\": \"vm\",\n" +
            "                \"maintype\": 0,\n" +
            "                \"role\": 4,\n" +
            "                \"category\": 1,\n" +
            "                \"second_category\": 1,\n" +
            "                \"l3_category\": 106,\n" +
            "                \"is_import\": true,\n" +
            "                \"is_internal\": false,\n" +
            "                \"is_init\": false,\n" +
            "                \"ext_entity\": null,\n" +
            "                \"ext_entity_access\": null,\n" +
            "                \"ext_system_info\": {\n" +
            "                    \"parent_system_type\": 1,\n" +
            "                    \"system_type\": 106,\n" +
            "                    \"system_architecture\": \"64\",\n" +
            "                    \"language\": \"cn\",\n" +
            "                    \"system_type_name\": null,\n" +
            "                    \"system_architecture_name\": null,\n" +
            "                    \"language_name\": null\n" +
            "                },\n" +
            "                \"ext_gateway\": null,\n" +
            "                \"ext_vm\": {\n" +
            "                    \"flavor\": \"m4.2c-2048m-40g\",\n" +
            "                    \"init_support\": true,\n" +
            "                    \"quick_create_support\": false,\n" +
            "                    \"access_protocols\": [\n" +
            "                        {\n" +
            "                            \"protocol\": \"console\",\n" +
            "                            \"auth_protocol\": null,\n" +
            "                            \"hostname\": null,\n" +
            "                            \"port\": null,\n" +
            "                            \"username\": \"administrator\",\n" +
            "                            \"password\": \"NMkk3kFeDe5Wxi1vEhCG\",\n" +
            "                            \"private_key_enable\": null,\n" +
            "                            \"private_key_path\": null,\n" +
            "                            \"private_key_file_name\": null,\n" +
            "                            \"description\": null,\n" +
            "                            \"manage_mode\": null\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"protocol\": \"rdp\",\n" +
            "                            \"auth_protocol\": null,\n" +
            "                            \"hostname\": null,\n" +
            "                            \"port\": 3389,\n" +
            "                            \"username\": \"administrator\",\n" +
            "                            \"password\": \"NMkk3kFeDe5Wxi1vEhCG\",\n" +
            "                            \"private_key_enable\": null,\n" +
            "                            \"private_key_path\": null,\n" +
            "                            \"private_key_file_name\": null,\n" +
            "                            \"description\": null,\n" +
            "                            \"manage_mode\": null\n" +
            "                        }\n" +
            "                    ],\n" +
            "                    \"quick_create_params\": [],\n" +
            "                    \"ext_info\": {},\n" +
            "                    \"instance_exist\": false,\n" +
            "                    \"snapshot_count\": 1,\n" +
            "                    \"instance_uuid\": \"\",\n" +
            "                    \"instance_state\": 0,\n" +
            "                    \"cpu\": 2,\n" +
            "                    \"ram\": 2048,\n" +
            "                    \"disk\": 40,\n" +
            "                    \"iso_attach\": false,\n" +
            "                    \"iso_attach_filename\": \"\",\n" +
            "                    \"iso_tmpdir_uuid\": \"\"\n" +
            "                },\n" +
            "                \"ext_image_info\": {\n" +
            "                    \"uuid\": \"e0240061-fcdf-47ab-ba94-87f4bd83c2ca\",\n" +
            "                    \"base_image_name\": \"cb2b3901-ca5a-4bb8-8d45-b41998735c4aWin10-360-reskdesk-240321-v1\",\n" +
            "                    \"base_image_support\": true,\n" +
            "                    \"meta_data\": {\n" +
            "                        \"hw_disk_bus\": null,\n" +
            "                        \"hw_vif_model\": null,\n" +
            "                        \"hw_video_model\": null,\n" +
            "                        \"environment_str\": null,\n" +
            "                        \"host_config_ex_str\": null,\n" +
            "                        \"interactive\": null,\n" +
            "                        \"tty\": null,\n" +
            "                        \"hw_firmware_type\": null,\n" +
            "                        \"options\": null\n" +
            "                    },\n" +
            "                    \"image_status\": 2,\n" +
            "                    \"error\": \"\",\n" +
            "                    \"image_source\": 2,\n" +
            "                    \"upload_type\": \"\",\n" +
            "                    \"disk_format\": \"\",\n" +
            "                    \"tmpdir_uuid\": null,\n" +
            "                    \"ftp_filename\": null,\n" +
            "                    \"user_filename\": \"\",\n" +
            "                    \"device_actions\": \"\"\n" +
            "                },\n" +
            "                \"ext_docker\": null,\n" +
            "                \"ext_entity_usb\": null,\n" +
            "                \"is_build_in\": true,\n" +
            "                \"is_hide\": false,\n" +
            "                \"default_launch_image_name\": \"d8806f6a-7685-4c3a-8274-a9c0a36d3d04快照\",\n" +
            "                \"support_export\": null\n" +
            "            }";

    Map<String, String> getHeaders();

    HttpClient getClient();

    List<String> listAllDevices();

    int getVendor();

    String createVm(String imageUuid);

    String[] getVmIpAndMac(String vmUuid);

    void deleteVm(String vmUuid);

    String getConnection(String vmUuid);


}
