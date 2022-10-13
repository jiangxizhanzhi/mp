package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.InitRegisterService;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.TargetConverter;
import cn.cyberpeace.mp.devopscluster.converter.TypeConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.request.TargetMultiAsyncRequest;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devopscluster.service.upload.TargetIconUploadService;
import cn.cyberpeace.mp.devopscluster.thread.ITask;
import cn.cyberpeace.mp.devopscluster.utils.MultiThreadUtils;
import cn.cyberpeace.mp.devopscluster.vo.DeviceTargetByTypeVO;
import cn.cyberpeace.mp.devopscluster.vo.DeviceTargetVO;
import cn.cyberpeace.mp.devopscluster.vo.DeviceVO;
import cn.cyberpeace.mp.devopscluster.vo.TargetSceneVO;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import cn.cyberpeace.mp.devopscluster.vo.convert.DeviceVoConverter;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 靶标管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/target")
@Api(tags = "靶标管理")
@RequiredArgsConstructor
public class DevopsTargetController {

    private final TargetConverter targetConverter;

    private final TypeConverter typeConverter;

    private final DevopsClusterConfig devopsClusterConfig;

    private final DeviceVoConverter deviceVoConverter;

    private final ITask asyncTargetTask;

    private final InitRegisterService initRegisterService;

    private final TargetIconUploadService targetIconUploadService;

    private final DevopsTargetService devopsTargetService;

    /**
     * 查询FTP信息
     * @return
     */
    @ApiOperation("查询FTP信息")
    @GetMapping("/ftp-info")
    public Result<String> getFtpAddress(){
        String formatUrl = devopsClusterConfig.getCpIp();
        DevopsClusterConfig config = BeanCommon.getBean(DevopsClusterConfig.class);
        if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
            if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                formatUrl = config.getPublicNetProxyIp();
            }
        }
        return new Result<>(formatUrl);
    }

    /**
     * 查询所有靶标信息
     * @param request
     * @return
     */
    @ApiOperation("查询所有靶标信息")
    @GetMapping("/list")
    public ServiceResult<TargetSceneVO> list(TargetSceneRequest request) {
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        List<TypeConvertModel> firstTypeList = l1AndL2AndL3Type.getFirstTypeList();
        List<TypeConvertModel> secondTypeList = l1AndL2AndL3Type.getSecondTypeList();

        Integer category = request.getCategory();
        if (Objects.nonNull(category)) {
            firstTypeList = firstTypeList.stream().filter(v -> v.getId().equals(category)).collect(Collectors.toList());
        }
        Integer secondCategory = request.getSecondCategory();
        if (Objects.nonNull(secondCategory)) {
            secondTypeList = secondTypeList.stream().filter(v -> v.getId().equals(secondCategory)).collect(Collectors.toList());
        }

        List<TargetSceneVO> targetSceneVOS = new ArrayList<>();
        for (TypeConvertModel first : firstTypeList) {
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = secondTypeList.stream()
                    .filter(v -> v.getL1ParentId().equals(firstId)).collect(Collectors.toList());
            List<Integer> secondCategories = typeConvertModels.stream()
                    .map(model -> model.getId())
                    .collect(Collectors.toList());
            request.setCategory(firstId);
            request.setSecondCategories(secondCategories);
            request.setPage(null);
            request.setPerPage(null);
            BasePageBO<Device> targetSceneConvertModel = targetConverter.allTarget(new ImagePageDTO(request));
            if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())) {
                continue;
            }
            List<Device> secondDeviceList = targetSceneConvertModel.getList();
            typeConvertModels.forEach(typeConvertModel -> {
                TargetSceneVO targetSceneVO = new TargetSceneVO();
                targetSceneVO.setFirstTypeId(firstId);
                targetSceneVO.setFirstTypeName(first.getName());
                targetSceneVO.setSecondTypeId(typeConvertModel.getId());
                targetSceneVO.setSecondTypeName(typeConvertModel.getName());
                targetSceneVO.setDeviceList(secondDeviceList.stream()
                        .filter(s -> s.getSecondCategory().equals(typeConvertModel.getId()))
                        .collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(targetSceneVO.getDeviceList())) {
                    targetSceneVOS.add(targetSceneVO);
                }
            });
        }
        return new Result(new ListResult<>(targetSceneVOS));
    }

    /**
     * 查询所有靶标信息
     * @param request
     * @return
     */
    @ApiOperation("查询所有靶标信息(带过滤条件)")
    @GetMapping("/listAll")
    public ServiceResult<Device> listAll(TargetSceneRequest request){
        /*
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        List<TypeConvertModel> firstTypeList = l1AndL2AndL3Type.getFirstTypeList();
        List<TypeConvertModel> secondTypeList = l1AndL2AndL3Type.getSecondTypeList();

        Integer category = request.getCategory();
        if (Objects.nonNull(category)) {
            firstTypeList = firstTypeList.stream().filter(v -> v.getId().equals(category)).collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(request.getSecondCategoryList())) {
            secondTypeList = secondTypeList.stream().filter(v -> request.getSecondCategoryList().contains(v.getId())).collect(Collectors.toList());
        }

        List<Device> deviceList = new ArrayList<>();
        for (TypeConvertModel first : firstTypeList){
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = secondTypeList.stream()
                    .filter(v -> v.getL1ParentId().equals(firstId)).collect(Collectors.toList());
            List<Integer> secondCategories = typeConvertModels.stream()
                    .map(model -> model.getId())
                    .collect(Collectors.toList());
            request.setCategory(firstId);
            request.setSecondCategories(secondCategories);
            request.setPage(null);
            request.setPerPage(null);
            BasePageBO<Device> targetSceneConvertModel = targetConverter.allTarget(new ImagePageDTO(request));
            if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())) {
                continue;
            }
            List<Device> secondDeviceList = targetSceneConvertModel.getList();
            deviceList.addAll(secondDeviceList);
        }
        */

        ListResult listResult = mockAllDevices(request.getParentSystemType());
        return new Result(listResult);
    }

    private ListResult mockAllDevices(Integer systemType) {
        String win = "[\n" +
                "    {\n" +
                "        \"success\": false,\n" +
                "        \"request_id\": null,\n" +
                "        \"total\": 0,\n" +
                "        \"message\": null,\n" +
                "        \"message_zh\": null,\n" +
                "        \"error_message\": null,\n" +
                "        \"code\": null,\n" +
                "        \"tmpdir_uuid\": null,\n" +
                "        \"file_names\": null,\n" +
                "        \"msg\": null,\n" +
                "        \"msg_zh\": null,\n" +
                "        \"url\": null,\n" +
                "        \"id\": 52,\n" +
                "        \"uuid\": \"5f3eed2f-fd42-4347-a897-dea0b2c9f3e1\",\n" +
                "        \"updated_at\": \"2022-10-09T07:28:31Z\",\n" +
                "        \"deleted\": false,\n" +
                "        \"created_at\": \"2022-10-09T07:28:31Z\",\n" +
                "        \"deleted_at\": null,\n" +
                "        \"name\": \"test_delete\",\n" +
                "        \"image\": null,\n" +
                "        \"description\": \"123123123\",\n" +
                "        \"logo\": \"/fdfs/group1/M00/00/09/CmQHAWM2rw-ABSGVAAAK5eLzl7s052.png\",\n" +
                "        \"image_type\": \"vm\",\n" +
                "        \"maintype\": 0,\n" +
                "        \"role\": 4,\n" +
                "        \"category\": 1,\n" +
                "        \"second_category\": 1,\n" +
                "        \"l3_category\": 97,\n" +
                "        \"is_import\": false,\n" +
                "        \"is_internal\": false,\n" +
                "        \"is_init\": false,\n" +
                "        \"ext_entity\": null,\n" +
                "        \"ext_entity_access\": null,\n" +
                "        \"ext_system_info\": {\n" +
                "            \"parent_system_type\": 1,\n" +
                "            \"system_type\": 97,\n" +
                "            \"system_architecture\": \"64\",\n" +
                "            \"language\": \"cn\",\n" +
                "            \"system_type_name\": null,\n" +
                "            \"system_architecture_name\": null,\n" +
                "            \"language_name\": null\n" +
                "        },\n" +
                "        \"ext_gateway\": null,\n" +
                "        \"ext_vm\": {\n" +
                "            \"flavor\": \"\",\n" +
                "            \"init_support\": false,\n" +
                "            \"quick_create_support\": false,\n" +
                "            \"access_protocols\": [\n" +
                "                {\n" +
                "                    \"protocol\": \"http\",\n" +
                "                    \"auth_protocol\": \"\",\n" +
                "                    \"hostname\": \"\",\n" +
                "                    \"port\": 80,\n" +
                "                    \"username\": \"\",\n" +
                "                    \"password\": \"\",\n" +
                "                    \"private_key_enable\": false,\n" +
                "                    \"private_key_path\": \"\",\n" +
                "                    \"private_key_file_name\": \"\",\n" +
                "                    \"description\": \"\",\n" +
                "                    \"manage_mode\": false\n" +
                "                }\n" +
                "            ],\n" +
                "            \"quick_create_params\": [],\n" +
                "            \"ext_info\": {},\n" +
                "            \"instance_exist\": false,\n" +
                "            \"snapshot_count\": 0,\n" +
                "            \"instance_uuid\": \"\",\n" +
                "            \"instance_state\": 0,\n" +
                "            \"cpu\": 0,\n" +
                "            \"ram\": 0,\n" +
                "            \"disk\": 0\n" +
                "        },\n" +
                "        \"ext_image_info\": {\n" +
                "            \"uuid\": \"13dbaf4c-ba25-494a-9389-ad36b4698c25\",\n" +
                "            \"base_image_name\": \"piakqiu\",\n" +
                "            \"base_image_support\": false,\n" +
                "            \"meta_data\": {\n" +
                "                \"hw_disk_bus\": \"\",\n" +
                "                \"hw_vif_model\": \"\",\n" +
                "                \"hw_video_model\": \"\"\n" +
                "            },\n" +
                "            \"image_status\": 2,\n" +
                "            \"error\": \"\",\n" +
                "            \"image_source\": 1,\n" +
                "            \"upload_type\": \"\",\n" +
                "            \"disk_format\": \"\",\n" +
                "            \"tmpdir_uuid\": null,\n" +
                "            \"ftp_filename\": null,\n" +
                "            \"user_filename\": \"\",\n" +
                "            \"device_actions\": null\n" +
                "        },\n" +
                "        \"ext_docker\": null,\n" +
                "        \"is_build_in\": false,\n" +
                "        \"is_hide\": false,\n" +
                "        \"default_launch_image_name\": \"piakqiu\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"success\": false,\n" +
                "        \"request_id\": null,\n" +
                "        \"total\": 0,\n" +
                "        \"message\": null,\n" +
                "        \"message_zh\": null,\n" +
                "        \"error_message\": null,\n" +
                "        \"code\": null,\n" +
                "        \"tmpdir_uuid\": null,\n" +
                "        \"file_names\": null,\n" +
                "        \"msg\": null,\n" +
                "        \"msg_zh\": null,\n" +
                "        \"url\": null,\n" +
                "        \"id\": 48,\n" +
                "        \"uuid\": \"f5ba336f-ac95-4ba7-b256-38b4621378bb\",\n" +
                "        \"updated_at\": \"2022-10-09T06:07:44Z\",\n" +
                "        \"deleted\": false,\n" +
                "        \"created_at\": \"2022-10-09T06:07:44Z\",\n" +
                "        \"deleted_at\": null,\n" +
                "        \"name\": \"攻防赛pipipiti\",\n" +
                "        \"image\": null,\n" +
                "        \"description\": \"攻防赛pipipiti\",\n" +
                "        \"logo\": \"/fdfs/group1/M00/00/08/CmQHAWM2rw-AH--pAAAKynWs2Mg774.png\",\n" +
                "        \"image_type\": \"vm\",\n" +
                "        \"maintype\": 0,\n" +
                "        \"role\": 4,\n" +
                "        \"category\": 1,\n" +
                "        \"second_category\": 1,\n" +
                "        \"l3_category\": 97,\n" +
                "        \"is_import\": false,\n" +
                "        \"is_internal\": false,\n" +
                "        \"is_init\": false,\n" +
                "        \"ext_entity\": null,\n" +
                "        \"ext_entity_access\": null,\n" +
                "        \"ext_system_info\": {\n" +
                "            \"parent_system_type\": 1,\n" +
                "            \"system_type\": 97,\n" +
                "            \"system_architecture\": \"64\",\n" +
                "            \"language\": \"cn\",\n" +
                "            \"system_type_name\": null,\n" +
                "            \"system_architecture_name\": null,\n" +
                "            \"language_name\": null\n" +
                "        },\n" +
                "        \"ext_gateway\": null,\n" +
                "        \"ext_vm\": {\n" +
                "            \"flavor\": \"\",\n" +
                "            \"init_support\": false,\n" +
                "            \"quick_create_support\": false,\n" +
                "            \"access_protocols\": [],\n" +
                "            \"quick_create_params\": [],\n" +
                "            \"ext_info\": {},\n" +
                "            \"instance_exist\": false,\n" +
                "            \"snapshot_count\": 0,\n" +
                "            \"instance_uuid\": \"\",\n" +
                "            \"instance_state\": 0,\n" +
                "            \"cpu\": 0,\n" +
                "            \"ram\": 0,\n" +
                "            \"disk\": 0\n" +
                "        },\n" +
                "        \"ext_image_info\": {\n" +
                "            \"uuid\": \"df0339ac-7a13-4013-8e3f-05b8e8dbe0f8\",\n" +
                "            \"base_image_name\": \"攻防赛pipipiti\",\n" +
                "            \"base_image_support\": true,\n" +
                "            \"meta_data\": {\n" +
                "                \"hw_disk_bus\": \"\",\n" +
                "                \"hw_vif_model\": \"\",\n" +
                "                \"hw_video_model\": \"\"\n" +
                "            },\n" +
                "            \"image_status\": 2,\n" +
                "            \"error\": \"\",\n" +
                "            \"image_source\": 2,\n" +
                "            \"upload_type\": \"\",\n" +
                "            \"disk_format\": \"\",\n" +
                "            \"tmpdir_uuid\": null,\n" +
                "            \"ftp_filename\": null,\n" +
                "            \"user_filename\": \"\",\n" +
                "            \"device_actions\": null\n" +
                "        },\n" +
                "        \"ext_docker\": null,\n" +
                "        \"is_build_in\": false,\n" +
                "        \"is_hide\": false,\n" +
                "        \"default_launch_image_name\": \"攻防赛pipipiti\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"success\": false,\n" +
                "        \"request_id\": null,\n" +
                "        \"total\": 0,\n" +
                "        \"message\": null,\n" +
                "        \"message_zh\": null,\n" +
                "        \"error_message\": null,\n" +
                "        \"code\": null,\n" +
                "        \"tmpdir_uuid\": null,\n" +
                "        \"file_names\": null,\n" +
                "        \"msg\": null,\n" +
                "        \"msg_zh\": null,\n" +
                "        \"url\": null,\n" +
                "        \"id\": 42,\n" +
                "        \"uuid\": \"d2915908-a1cc-4981-bb75-2f155b48259a\",\n" +
                "        \"updated_at\": \"2022-10-08T09:50:51Z\",\n" +
                "        \"deleted\": false,\n" +
                "        \"created_at\": \"2022-10-08T09:50:51Z\",\n" +
                "        \"deleted_at\": null,\n" +
                "        \"name\": \"piakqiu\",\n" +
                "        \"image\": null,\n" +
                "        \"description\": \"piakqiu\",\n" +
                "        \"logo\": \"/fdfs/group1/M00/00/08/CmQHAWM2rw-AH--pAAAKynWs2Mg774.png\",\n" +
                "        \"image_type\": \"vm\",\n" +
                "        \"maintype\": 0,\n" +
                "        \"role\": 4,\n" +
                "        \"category\": 1,\n" +
                "        \"second_category\": 1,\n" +
                "        \"l3_category\": 97,\n" +
                "        \"is_import\": false,\n" +
                "        \"is_internal\": false,\n" +
                "        \"is_init\": false,\n" +
                "        \"ext_entity\": null,\n" +
                "        \"ext_entity_access\": null,\n" +
                "        \"ext_system_info\": {\n" +
                "            \"parent_system_type\": 1,\n" +
                "            \"system_type\": 97,\n" +
                "            \"system_architecture\": \"32\",\n" +
                "            \"language\": \"cn\",\n" +
                "            \"system_type_name\": null,\n" +
                "            \"system_architecture_name\": null,\n" +
                "            \"language_name\": null\n" +
                "        },\n" +
                "        \"ext_gateway\": null,\n" +
                "        \"ext_vm\": {\n" +
                "            \"flavor\": \"m1.1c-512m-8g\",\n" +
                "            \"init_support\": false,\n" +
                "            \"quick_create_support\": false,\n" +
                "            \"access_protocols\": [],\n" +
                "            \"quick_create_params\": [],\n" +
                "            \"ext_info\": {},\n" +
                "            \"instance_exist\": false,\n" +
                "            \"snapshot_count\": 0,\n" +
                "            \"instance_uuid\": \"\",\n" +
                "            \"instance_state\": 0,\n" +
                "            \"cpu\": 1,\n" +
                "            \"ram\": 512,\n" +
                "            \"disk\": 8\n" +
                "        },\n" +
                "        \"ext_image_info\": {\n" +
                "            \"uuid\": \"4db487fb-9770-46b8-a929-c7ef23f88655\",\n" +
                "            \"base_image_name\": \"piakqiu\",\n" +
                "            \"base_image_support\": true,\n" +
                "            \"meta_data\": {\n" +
                "                \"hw_disk_bus\": \"\",\n" +
                "                \"hw_vif_model\": \"\",\n" +
                "                \"hw_video_model\": \"\"\n" +
                "            },\n" +
                "            \"image_status\": 2,\n" +
                "            \"error\": \"\",\n" +
                "            \"image_source\": 2,\n" +
                "            \"upload_type\": \"\",\n" +
                "            \"disk_format\": \"\",\n" +
                "            \"tmpdir_uuid\": null,\n" +
                "            \"ftp_filename\": null,\n" +
                "            \"user_filename\": \"\",\n" +
                "            \"device_actions\": \"\"\n" +
                "        },\n" +
                "        \"ext_docker\": null,\n" +
                "        \"is_build_in\": false,\n" +
                "        \"is_hide\": false,\n" +
                "        \"default_launch_image_name\": \"piakqiu\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"success\": false,\n" +
                "        \"request_id\": null,\n" +
                "        \"total\": 0,\n" +
                "        \"message\": null,\n" +
                "        \"message_zh\": null,\n" +
                "        \"error_message\": null,\n" +
                "        \"code\": null,\n" +
                "        \"tmpdir_uuid\": null,\n" +
                "        \"file_names\": null,\n" +
                "        \"msg\": null,\n" +
                "        \"msg_zh\": null,\n" +
                "        \"url\": null,\n" +
                "        \"id\": 23,\n" +
                "        \"uuid\": \"87d24837-79a1-4ea4-9f60-d2f250fd3abc\",\n" +
                "        \"updated_at\": \"2022-09-29T03:56:42Z\",\n" +
                "        \"deleted\": false,\n" +
                "        \"created_at\": \"2022-09-29T03:56:42Z\",\n" +
                "        \"deleted_at\": null,\n" +
                "        \"name\": \"Win8.1-Ent-x86-en\",\n" +
                "        \"image\": null,\n" +
                "        \"description\": \"Windows 8.1是微软公司在2012年10月推出Windows 8之后，着手开发的 Windows 8更新。在代号为“Blue”的项目中，微软将实现操作系统升级标准化，以便向用户提供更常规的升级。Windows 8.1 具有承上启下的作用，为Windows 10铺路。\",\n" +
                "        \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1F2yAWrYPAAAZn9t5Kks65.jpeg\",\n" +
                "        \"image_type\": \"vm\",\n" +
                "        \"maintype\": 0,\n" +
                "        \"role\": 4,\n" +
                "        \"category\": 1,\n" +
                "        \"second_category\": 1,\n" +
                "        \"l3_category\": 99,\n" +
                "        \"is_import\": true,\n" +
                "        \"is_internal\": false,\n" +
                "        \"is_init\": false,\n" +
                "        \"ext_entity\": null,\n" +
                "        \"ext_entity_access\": null,\n" +
                "        \"ext_system_info\": {\n" +
                "            \"parent_system_type\": 1,\n" +
                "            \"system_type\": 99,\n" +
                "            \"system_architecture\": \"64\",\n" +
                "            \"language\": \"en\",\n" +
                "            \"system_type_name\": null,\n" +
                "            \"system_architecture_name\": null,\n" +
                "            \"language_name\": null\n" +
                "        },\n" +
                "        \"ext_gateway\": null,\n" +
                "        \"ext_vm\": {\n" +
                "            \"flavor\": \"m3.1c-2048m-20g\",\n" +
                "            \"init_support\": true,\n" +
                "            \"quick_create_support\": false,\n" +
                "            \"access_protocols\": [\n" +
                "                {\n" +
                "                    \"protocol\": \"rdp\",\n" +
                "                    \"auth_protocol\": null,\n" +
                "                    \"hostname\": null,\n" +
                "                    \"port\": 3389,\n" +
                "                    \"username\": \"Administrator\",\n" +
                "                    \"password\": \"123456\",\n" +
                "                    \"private_key_enable\": null,\n" +
                "                    \"private_key_path\": null,\n" +
                "                    \"private_key_file_name\": null,\n" +
                "                    \"description\": null,\n" +
                "                    \"manage_mode\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"quick_create_params\": [],\n" +
                "            \"ext_info\": {},\n" +
                "            \"instance_exist\": false,\n" +
                "            \"snapshot_count\": 0,\n" +
                "            \"instance_uuid\": \"\",\n" +
                "            \"instance_state\": 0,\n" +
                "            \"cpu\": 1,\n" +
                "            \"ram\": 2048,\n" +
                "            \"disk\": 20\n" +
                "        },\n" +
                "        \"ext_image_info\": {\n" +
                "            \"uuid\": \"4332f2a7-7e19-45e8-8d35-0a72882a5c92\",\n" +
                "            \"base_image_name\": \"Win8.1-Ent-x86-en\",\n" +
                "            \"base_image_support\": true,\n" +
                "            \"meta_data\": null,\n" +
                "            \"image_status\": 2,\n" +
                "            \"error\": \"\",\n" +
                "            \"image_source\": 2,\n" +
                "            \"upload_type\": \"\",\n" +
                "            \"disk_format\": \"\",\n" +
                "            \"tmpdir_uuid\": null,\n" +
                "            \"ftp_filename\": null,\n" +
                "            \"user_filename\": \"\",\n" +
                "            \"device_actions\": \"\"\n" +
                "        },\n" +
                "        \"ext_docker\": null,\n" +
                "        \"is_build_in\": true,\n" +
                "        \"is_hide\": false,\n" +
                "        \"default_launch_image_name\": \"Win8.1-Ent-x86-en\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"success\": false,\n" +
                "        \"request_id\": null,\n" +
                "        \"total\": 0,\n" +
                "        \"message\": null,\n" +
                "        \"message_zh\": null,\n" +
                "        \"error_message\": null,\n" +
                "        \"code\": null,\n" +
                "        \"tmpdir_uuid\": null,\n" +
                "        \"file_names\": null,\n" +
                "        \"msg\": null,\n" +
                "        \"msg_zh\": null,\n" +
                "        \"url\": null,\n" +
                "        \"id\": 22,\n" +
                "        \"uuid\": \"7f442b07-a811-492f-9a3c-738f97b29c70\",\n" +
                "        \"updated_at\": \"2022-09-29T03:56:28Z\",\n" +
                "        \"deleted\": false,\n" +
                "        \"created_at\": \"2022-09-29T03:56:28Z\",\n" +
                "        \"deleted_at\": null,\n" +
                "        \"name\": \"Win10-Pro-1803-x64-cn\",\n" +
                "        \"image\": null,\n" +
                "        \"description\": \"Windows 10是由美国微软公司开发的应用于计算机和平板电脑的操作系统，于2015年7月29日发布正式版。Windows 10操作系统在易用性和安全性方面有了极大的提升，除了针对云服务、智能移动设备、自然人机交互等新技术进行融合外，还对固态硬盘、生物识别、高分辨率屏幕等硬件进行了优化完善与支持。\",\n" +
                "        \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1F1uAUv0sAABsCv9ke8g258.png\",\n" +
                "        \"image_type\": \"vm\",\n" +
                "        \"maintype\": 0,\n" +
                "        \"role\": 4,\n" +
                "        \"category\": 1,\n" +
                "        \"second_category\": 1,\n" +
                "        \"l3_category\": 106,\n" +
                "        \"is_import\": true,\n" +
                "        \"is_internal\": false,\n" +
                "        \"is_init\": false,\n" +
                "        \"ext_entity\": null,\n" +
                "        \"ext_entity_access\": null,\n" +
                "        \"ext_system_info\": {\n" +
                "            \"parent_system_type\": 1,\n" +
                "            \"system_type\": 106,\n" +
                "            \"system_architecture\": \"64\",\n" +
                "            \"language\": \"cn\",\n" +
                "            \"system_type_name\": null,\n" +
                "            \"system_architecture_name\": null,\n" +
                "            \"language_name\": null\n" +
                "        },\n" +
                "        \"ext_gateway\": null,\n" +
                "        \"ext_vm\": {\n" +
                "            \"flavor\": \"m3.1c-2048m-20g\",\n" +
                "            \"init_support\": true,\n" +
                "            \"quick_create_support\": false,\n" +
                "            \"access_protocols\": [\n" +
                "                {\n" +
                "                    \"protocol\": \"rdp\",\n" +
                "                    \"auth_protocol\": null,\n" +
                "                    \"hostname\": null,\n" +
                "                    \"port\": 3389,\n" +
                "                    \"username\": \"Administrator\",\n" +
                "                    \"password\": \"123456\",\n" +
                "                    \"private_key_enable\": null,\n" +
                "                    \"private_key_path\": null,\n" +
                "                    \"private_key_file_name\": null,\n" +
                "                    \"description\": null,\n" +
                "                    \"manage_mode\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"quick_create_params\": [],\n" +
                "            \"ext_info\": {},\n" +
                "            \"instance_exist\": false,\n" +
                "            \"snapshot_count\": 1,\n" +
                "            \"instance_uuid\": \"\",\n" +
                "            \"instance_state\": 0,\n" +
                "            \"cpu\": 1,\n" +
                "            \"ram\": 2048,\n" +
                "            \"disk\": 20\n" +
                "        },\n" +
                "        \"ext_image_info\": {\n" +
                "            \"uuid\": \"130f3c45-b5fc-493d-b505-a6845e7372cc\",\n" +
                "            \"base_image_name\": \"Win10-Pro-1803-x64-cn\",\n" +
                "            \"base_image_support\": true,\n" +
                "            \"meta_data\": null,\n" +
                "            \"image_status\": 2,\n" +
                "            \"error\": \"\",\n" +
                "            \"image_source\": 2,\n" +
                "            \"upload_type\": \"\",\n" +
                "            \"disk_format\": \"\",\n" +
                "            \"tmpdir_uuid\": null,\n" +
                "            \"ftp_filename\": null,\n" +
                "            \"user_filename\": \"\",\n" +
                "            \"device_actions\": null\n" +
                "        },\n" +
                "        \"ext_docker\": null,\n" +
                "        \"is_build_in\": true,\n" +
                "        \"is_hide\": false,\n" +
                "        \"default_launch_image_name\": \"Win10-Pro-1803-x64-cn\"\n" +
                "    }\n" +
                "]";

        String linux = "[\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 65,\n" +
                "                \"uuid\": \"fbd52d29-2a46-4729-982c-a16445c77b69\",\n" +
                "                \"updated_at\": \"2022-10-12T02:46:50Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-10-12T02:46:50Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"ISP_Ubuntu222\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"ISP_Ubuntu222\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/08/CmQHAWM2rw-AH--pAAAKynWs2Mg774.png\",\n" +
                "                \"image_type\": \"vm\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 306,\n" +
                "                \"is_import\": false,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 304,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"cn\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": {\n" +
                "                    \"flavor\": \"\",\n" +
                "                    \"init_support\": false,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"access_protocols\": [],\n" +
                "                    \"quick_create_params\": [],\n" +
                "                    \"ext_info\": {},\n" +
                "                    \"instance_exist\": false,\n" +
                "                    \"snapshot_count\": 0,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"cpu\": 0,\n" +
                "                    \"ram\": 0,\n" +
                "                    \"disk\": 0\n" +
                "                },\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"420e56a2-4101-4300-bbe1-8e15e25cc7fe\",\n" +
                "                    \"base_image_name\": \"ISP_Ubuntu222\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": {\n" +
                "                        \"hw_disk_bus\": \"\",\n" +
                "                        \"hw_vif_model\": \"\",\n" +
                "                        \"hw_video_model\": \"\"\n" +
                "                    },\n" +
                "                    \"image_status\": 2,\n" +
                "                    \"error\": \"\",\n" +
                "                    \"image_source\": 2,\n" +
                "                    \"upload_type\": \"\",\n" +
                "                    \"disk_format\": \"\",\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"ftp_filename\": null,\n" +
                "                    \"user_filename\": \"\",\n" +
                "                    \"device_actions\": null\n" +
                "                },\n" +
                "                \"ext_docker\": null,\n" +
                "                \"is_build_in\": false,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"ISP_Ubuntu222\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 62,\n" +
                "                \"uuid\": \"934bb9db-44d6-404a-8fe1-a3d20486cd08\",\n" +
                "                \"updated_at\": \"2022-10-11T08:56:12Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-10-11T08:56:12Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"ISP_Kali\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"ISP_Kali\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/08/CmQHAWM2rw-AHXa3AAAGWNSr8jg376.png\",\n" +
                "                \"image_type\": \"vm\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 309,\n" +
                "                \"is_import\": false,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 309,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"cn\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": {\n" +
                "                    \"flavor\": \"m5.4c-4096m-80g\",\n" +
                "                    \"init_support\": false,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"console\",\n" +
                "                            \"auth_protocol\": \"\",\n" +
                "                            \"hostname\": \"\",\n" +
                "                            \"port\": null,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"123456\",\n" +
                "                            \"private_key_enable\": null,\n" +
                "                            \"private_key_path\": null,\n" +
                "                            \"private_key_file_name\": null,\n" +
                "                            \"description\": \"\",\n" +
                "                            \"manage_mode\": false\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"quick_create_params\": [],\n" +
                "                    \"ext_info\": {},\n" +
                "                    \"instance_exist\": false,\n" +
                "                    \"snapshot_count\": 0,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"cpu\": 4,\n" +
                "                    \"ram\": 4096,\n" +
                "                    \"disk\": 80\n" +
                "                },\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"355effeb-4bb7-4d32-872f-8b077f06b917\",\n" +
                "                    \"base_image_name\": \"ISP_Kali\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": {\n" +
                "                        \"hw_disk_bus\": \"ide\",\n" +
                "                        \"hw_vif_model\": \"rtl8139\",\n" +
                "                        \"hw_video_model\": \"vga\"\n" +
                "                    },\n" +
                "                    \"image_status\": 2,\n" +
                "                    \"error\": \"\",\n" +
                "                    \"image_source\": 3,\n" +
                "                    \"upload_type\": \"ftp\",\n" +
                "                    \"disk_format\": \"vmdk\",\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"ftp_filename\": null,\n" +
                "                    \"user_filename\": \"kali_k_2c4g80g_client1.vmdk\",\n" +
                "                    \"device_actions\": \"\"\n" +
                "                },\n" +
                "                \"ext_docker\": null,\n" +
                "                \"is_build_in\": false,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"ISP_Kali\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 33,\n" +
                "                \"uuid\": \"5a19b882-58a6-49f7-a33e-bcd82b572ad0\",\n" +
                "                \"updated_at\": \"2022-10-01T10:49:47Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-30T09:37:04Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"secops002\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"secops002\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/08/CmQHAWM2rw-AGJskAAAGlFhInrM812.png\",\n" +
                "                \"image_type\": \"container\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 306,\n" +
                "                \"is_import\": false,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 306,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": null,\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"243a4337-ee4e-47cf-8235-6cd7d7fe8c71\",\n" +
                "                    \"base_image_name\": \"secops002\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": {\n" +
                "                        \"hw_disk_bus\": \"\",\n" +
                "                        \"hw_vif_model\": \"\",\n" +
                "                        \"hw_video_model\": \"\"\n" +
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
                "                \"ext_docker\": {\n" +
                "                    \"file\": null,\n" +
                "                    \"settings\": null,\n" +
                "                    \"sync_status\": null,\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"user_filename\": null,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"init_support\": true,\n" +
                "                    \"quick_create_support\": true,\n" +
                "                    \"snapshot_count\": 0,\n" +
                "                    \"ext_info\": {},\n" +
                "                    \"access_protocols\": []\n" +
                "                },\n" +
                "                \"is_build_in\": false,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"secops002\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 24,\n" +
                "                \"uuid\": \"f5420146-d00e-45a2-b137-9f29c258ce0e\",\n" +
                "                \"updated_at\": \"2022-09-29T09:26:01Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T09:26:01Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"test\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"1\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/08/CmQHAWM2rw-ANy_oAAAOoN5z58E912.png\",\n" +
                "                \"image_type\": \"vm\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 306,\n" +
                "                \"is_import\": false,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 306,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"cn\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": {\n" +
                "                    \"flavor\": \"\",\n" +
                "                    \"init_support\": false,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"access_protocols\": [],\n" +
                "                    \"quick_create_params\": [],\n" +
                "                    \"ext_info\": {},\n" +
                "                    \"instance_exist\": false,\n" +
                "                    \"snapshot_count\": 0,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"cpu\": 0,\n" +
                "                    \"ram\": 0,\n" +
                "                    \"disk\": 0\n" +
                "                },\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"aafd0393-c63d-4127-8859-bbd3e5d9f8b4\",\n" +
                "                    \"base_image_name\": \"test\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": {\n" +
                "                        \"hw_disk_bus\": \"\",\n" +
                "                        \"hw_vif_model\": \"\",\n" +
                "                        \"hw_video_model\": \"\"\n" +
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
                "                \"is_build_in\": false,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"test\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 20,\n" +
                "                \"uuid\": \"d69b3c86-dfea-47a7-990b-5a944505c5e9\",\n" +
                "                \"updated_at\": \"2022-09-29T03:56:01Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:56:01Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"Ubuntu-18.04-x64-server\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"Ubuntu 是一个由全球化的专业开发团队建造的操作系统。它包含了所有您需要的应用程序：浏览器、Office 套件、多媒体程序、即时消息等。Ubuntu 是一个 Windows 和 Office 的开源替代品。Ubuntu 的名称来自非洲南部祖鲁语或豪萨语的“ubuntu”一词（译为吾帮托或乌班图），意思是“人性 ”、“我的存在是因为大家的存在”。Ubuntu 基于 Debian 发行版和 GNOME 桌面环境，与 Debian 的不同在于它每 6 个月会发布一个新版本。\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1F0aAXf5dAAAlhmG94tA36.jpeg\",\n" +
                "                \"image_type\": \"vm\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 304,\n" +
                "                \"is_import\": true,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 304,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": {\n" +
                "                    \"flavor\": \"m2.1c-1024m-10g\",\n" +
                "                    \"init_support\": true,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": null,\n" +
                "                            \"hostname\": null,\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"123456\",\n" +
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
                "                    \"snapshot_count\": 0,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"cpu\": 1,\n" +
                "                    \"ram\": 1024,\n" +
                "                    \"disk\": 10\n" +
                "                },\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"acf7abe1-9491-458b-8543-a1bbe2c09451\",\n" +
                "                    \"base_image_name\": \"Ubuntu-18.04-x64-server\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": null,\n" +
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
                "                \"is_build_in\": true,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"Ubuntu-18.04-x64-server\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 19,\n" +
                "                \"uuid\": \"9effcaad-3d2b-433a-ba23-39376aa50d4d\",\n" +
                "                \"updated_at\": \"2022-09-29T03:55:50Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:55:50Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"Redhat-7.5\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"Red Hat（红帽）公司（NYSE：RHT）是一家开源解决方案供应商，也是标准普尔500指数成员。总部位于美国北卡罗来纳州的罗利市，截止2015年3月3日，共有80多个分公司。红帽公司为诸多重要IT技术如操作系统、存储、中间件、虚拟化和云计算提供关键任务的软件与服务。红帽的开放源码模式提供跨物理、虚拟和云端环境的企业运算解决方案，以帮助企业降低成本并提升效能、稳定性与安全性。红帽公司同时也为全球客户或通过领先合作伙伴为客户提供技术支持、培训和咨询服务。\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1FzqAf1BWAAAozl6XKR810.jpeg\",\n" +
                "                \"image_type\": \"vm\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 312,\n" +
                "                \"is_import\": true,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 312,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": {\n" +
                "                    \"flavor\": \"m2.1c-1024m-10g\",\n" +
                "                    \"init_support\": true,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": null,\n" +
                "                            \"hostname\": null,\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"password\",\n" +
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
                "                    \"snapshot_count\": 0,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"cpu\": 1,\n" +
                "                    \"ram\": 1024,\n" +
                "                    \"disk\": 10\n" +
                "                },\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"aa281558-b4b9-4519-a531-2ee632df786a\",\n" +
                "                    \"base_image_name\": \"Redhat-7.5\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": null,\n" +
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
                "                \"is_build_in\": true,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"Redhat-7.5\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 18,\n" +
                "                \"uuid\": \"ed366e25-167a-41d4-b135-27ca6ca99206\",\n" +
                "                \"updated_at\": \"2022-09-29T03:55:38Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:55:38Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"OpenSUSE-42.3-x64\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"openSUSE项目是Novell公司资助的社区计划。为在方方面面促进Linux的普及应用，该计划提供了对openSUSE这份完整Linux发行 的免费、简便的获取访问。openSUSE项目有三个主要目标：让openSUSE成为任何人都能最容易获得且最广泛使用的Linux发行，利用开源软件 的联合来使openSUSE成为世界上可用性最强的Linux发行及新手和资深Linux用户们的桌面环境，显著地简化并开放其开发及打包过程以使 openSUSE成为Linux开发人员及软件提供商所选择的平台。\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1FyiAZI8tAACNJnQouW0654.png\",\n" +
                "                \"image_type\": \"vm\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 313,\n" +
                "                \"is_import\": true,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 313,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": {\n" +
                "                    \"flavor\": \"m2.1c-1024m-10g\",\n" +
                "                    \"init_support\": true,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": null,\n" +
                "                            \"hostname\": null,\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"123456\",\n" +
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
                "                    \"snapshot_count\": 0,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"cpu\": 1,\n" +
                "                    \"ram\": 1024,\n" +
                "                    \"disk\": 10\n" +
                "                },\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"580cccb0-b3bb-4983-9e1e-0aac58aa3d10\",\n" +
                "                    \"base_image_name\": \"OpenSUSE-42.3-x64\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": null,\n" +
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
                "                \"is_build_in\": true,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"OpenSUSE-42.3-x64\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 16,\n" +
                "                \"uuid\": \"262b2d7d-803a-4e39-93f9-1d1bb1b10e7e\",\n" +
                "                \"updated_at\": \"2022-09-29T03:55:09Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:55:09Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"Fedora-Server-31-x64\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"Fedora 是一个 Linux 发行版，是一款由全球社区爱好者构建的面向日常应用的快速、稳定、强大的操作系统。 [1]  它允许任何人自由地使用、修改和重发布，无论现在还是将来。它由一个强大的社群开发，这个社群的成员以自己的不懈努力，提供并维护自由、开放源码的软件和开放的标准。Fedora 项目由 Fedora 基金会管理和控制，得到了 Red Hat 的支持。Fedora （第七版以前为Fedora Core）是一款基于 Linux 的操作系统，也是一组维持计算机正常运行的软件集合。Fedora 由Fedora Project社区开发、红帽公司赞助，目标是创建一套新颖、多功能并且自由和开源的操作系统。Fedora 项目以社区的方式工作，引领创新并传播自由代码和内容，是世界各地爱好、使用和构建自由软件的社区朋友的代名词。Fedora基于Red Hat Linux，在Red Hat Linux终止发行后，红帽公司计划以Fedora来取代Red Hat Linux在个人领域的应用，而另外发行的Red Hat Enterprise Linux（Red Hat企业版Linux，RHEL）则取代Red Hat Linux在商业应用的领域。\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1FxGAQ7f5AAEOtKWDqVc709.png\",\n" +
                "                \"image_type\": \"vm\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 310,\n" +
                "                \"is_import\": true,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 310,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": {\n" +
                "                    \"flavor\": \"m2.1c-1024m-10g\",\n" +
                "                    \"init_support\": true,\n" +
                "                    \"quick_create_support\": true,\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": null,\n" +
                "                            \"hostname\": null,\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"123456\",\n" +
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
                "                    \"snapshot_count\": 0,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"cpu\": 1,\n" +
                "                    \"ram\": 1024,\n" +
                "                    \"disk\": 10\n" +
                "                },\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"4304535b-349e-4255-8d33-da1ebf8da8f9\",\n" +
                "                    \"base_image_name\": \"Fedora-Server-31-x64\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": null,\n" +
                "                    \"image_status\": 2,\n" +
                "                    \"error\": \"\",\n" +
                "                    \"image_source\": 2,\n" +
                "                    \"upload_type\": \"\",\n" +
                "                    \"disk_format\": \"\",\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"ftp_filename\": null,\n" +
                "                    \"user_filename\": \"\",\n" +
                "                    \"device_actions\": null\n" +
                "                },\n" +
                "                \"ext_docker\": null,\n" +
                "                \"is_build_in\": true,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"Fedora-Server-31-x64\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 15,\n" +
                "                \"uuid\": \"cd745663-bad8-42be-bf39-89f75488ffcc\",\n" +
                "                \"updated_at\": \"2022-09-29T03:54:57Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:54:57Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"docker-nginx\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"docker-nginx\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1Fw6AB4EBAAA73VCukSk69.jpeg\",\n" +
                "                \"image_type\": \"container\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 306,\n" +
                "                \"is_import\": true,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 306,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": null,\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"8bd54395-ad15-4d19-a8db-ddd0f6d589ba\",\n" +
                "                    \"base_image_name\": \"docker-nginx\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": null,\n" +
                "                    \"image_status\": 2,\n" +
                "                    \"error\": \"\",\n" +
                "                    \"image_source\": 2,\n" +
                "                    \"upload_type\": \"\",\n" +
                "                    \"disk_format\": \"\",\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"ftp_filename\": null,\n" +
                "                    \"user_filename\": \"\",\n" +
                "                    \"device_actions\": null\n" +
                "                },\n" +
                "                \"ext_docker\": {\n" +
                "                    \"file\": null,\n" +
                "                    \"settings\": null,\n" +
                "                    \"sync_status\": null,\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"user_filename\": null,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"init_support\": false,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"snapshot_count\": 0,\n" +
                "                    \"ext_info\": {},\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": null,\n" +
                "                            \"hostname\": null,\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"password\",\n" +
                "                            \"private_key_enable\": null,\n" +
                "                            \"private_key_path\": null,\n" +
                "                            \"private_key_file_name\": null,\n" +
                "                            \"description\": null,\n" +
                "                            \"manage_mode\": null\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                \"is_build_in\": true,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"docker-nginx\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 14,\n" +
                "                \"uuid\": \"fe2c3350-7030-4458-bfc5-6c63ce6bc8c4\",\n" +
                "                \"updated_at\": \"2022-09-29T03:54:54Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:54:54Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"docker-httpd\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"docker-httpd\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1FwyARUVyAAA73VCukSk27.jpeg\",\n" +
                "                \"image_type\": \"container\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 306,\n" +
                "                \"is_import\": true,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 306,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": null,\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"46633496-8127-412b-83bf-bc7a33ad936b\",\n" +
                "                    \"base_image_name\": \"docker-httpd\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": null,\n" +
                "                    \"image_status\": 2,\n" +
                "                    \"error\": \"\",\n" +
                "                    \"image_source\": 2,\n" +
                "                    \"upload_type\": \"\",\n" +
                "                    \"disk_format\": \"\",\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"ftp_filename\": null,\n" +
                "                    \"user_filename\": \"\",\n" +
                "                    \"device_actions\": null\n" +
                "                },\n" +
                "                \"ext_docker\": {\n" +
                "                    \"file\": null,\n" +
                "                    \"settings\": null,\n" +
                "                    \"sync_status\": null,\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"user_filename\": null,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"init_support\": false,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"snapshot_count\": 0,\n" +
                "                    \"ext_info\": {},\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": null,\n" +
                "                            \"hostname\": null,\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"password\",\n" +
                "                            \"private_key_enable\": null,\n" +
                "                            \"private_key_path\": null,\n" +
                "                            \"private_key_file_name\": null,\n" +
                "                            \"description\": null,\n" +
                "                            \"manage_mode\": null\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                \"is_build_in\": true,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"docker-httpd\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 13,\n" +
                "                \"uuid\": \"7ec48fd6-db8e-4690-a906-13b1d2870d05\",\n" +
                "                \"updated_at\": \"2022-09-29T03:54:52Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:54:52Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"docker-cirros\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"docker-cirros\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1FwqAZW58AAA73VCukSk84.jpeg\",\n" +
                "                \"image_type\": \"container\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 306,\n" +
                "                \"is_import\": true,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 306,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": null,\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"a8c435bc-386a-41d5-be81-8ee6a374d987\",\n" +
                "                    \"base_image_name\": \"docker-cirros\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": null,\n" +
                "                    \"image_status\": 2,\n" +
                "                    \"error\": \"\",\n" +
                "                    \"image_source\": 2,\n" +
                "                    \"upload_type\": \"\",\n" +
                "                    \"disk_format\": \"\",\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"ftp_filename\": null,\n" +
                "                    \"user_filename\": \"\",\n" +
                "                    \"device_actions\": null\n" +
                "                },\n" +
                "                \"ext_docker\": {\n" +
                "                    \"file\": null,\n" +
                "                    \"settings\": null,\n" +
                "                    \"sync_status\": null,\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"user_filename\": null,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"init_support\": false,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"snapshot_count\": 0,\n" +
                "                    \"ext_info\": {},\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": null,\n" +
                "                            \"hostname\": null,\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"password\",\n" +
                "                            \"private_key_enable\": null,\n" +
                "                            \"private_key_path\": null,\n" +
                "                            \"private_key_file_name\": null,\n" +
                "                            \"description\": null,\n" +
                "                            \"manage_mode\": null\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                \"is_build_in\": true,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"docker-cirros\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 12,\n" +
                "                \"uuid\": \"2a08123e-3316-45c9-b24f-0297fb2d1367\",\n" +
                "                \"updated_at\": \"2022-09-29T03:54:50Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:54:50Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"Debian-8-x64\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"Debian (英语发音：/ˈdɛbiən/) 作为最早最老牌的Linux发行版之一，它在创建之初便紧密遵循 GNU 规范并坚守 Unix 和开源自由软件的精神。最大优点是极其稳定快速，同时拥有强大的 APT 包管理机制和丰富的软件源，可以方便地安装和平滑升级软件和系统，省去很多编译安装和版本升级的麻烦。\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1FvqAWND5AAAaYLjgSK4183.png\",\n" +
                "                \"image_type\": \"vm\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 307,\n" +
                "                \"is_import\": true,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 307,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": {\n" +
                "                    \"flavor\": \"m2.1c-1024m-10g\",\n" +
                "                    \"init_support\": true,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": null,\n" +
                "                            \"hostname\": null,\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"123456\",\n" +
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
                "                    \"snapshot_count\": 0,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"cpu\": 1,\n" +
                "                    \"ram\": 1024,\n" +
                "                    \"disk\": 10\n" +
                "                },\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"eace2bce-8d58-45bc-b874-348be7fd13d1\",\n" +
                "                    \"base_image_name\": \"Debian-8-x64\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": null,\n" +
                "                    \"image_status\": 2,\n" +
                "                    \"error\": \"\",\n" +
                "                    \"image_source\": 2,\n" +
                "                    \"upload_type\": \"\",\n" +
                "                    \"disk_format\": \"\",\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"ftp_filename\": null,\n" +
                "                    \"user_filename\": \"\",\n" +
                "                    \"device_actions\": null\n" +
                "                },\n" +
                "                \"ext_docker\": null,\n" +
                "                \"is_build_in\": true,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"Debian-8-x64\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 11,\n" +
                "                \"uuid\": \"ca54ce40-e3d4-4efd-adca-a59fcbd0da7c\",\n" +
                "                \"updated_at\": \"2022-09-29T03:54:34Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:54:34Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"cirros_import\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"cirros import\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1FvKAF-O0AAA73VCukSk71.jpeg\",\n" +
                "                \"image_type\": \"vm\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 306,\n" +
                "                \"is_import\": true,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 306,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": {\n" +
                "                    \"flavor\": \"m1.1c-1024m-1g\",\n" +
                "                    \"init_support\": true,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": null,\n" +
                "                            \"hostname\": null,\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"password\",\n" +
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
                "                    \"snapshot_count\": 0,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"cpu\": 1,\n" +
                "                    \"ram\": 1024,\n" +
                "                    \"disk\": 1\n" +
                "                },\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"f720421a-1ebb-4794-ae85-8e865f633219\",\n" +
                "                    \"base_image_name\": \"cirros_import\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": null,\n" +
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
                "                \"is_build_in\": true,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"cirros_import\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 10,\n" +
                "                \"uuid\": \"06a3facf-8b37-4d44-ab1d-2443dc2c6243\",\n" +
                "                \"updated_at\": \"2022-09-29T03:54:25Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:54:25Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"CentOS-7.6.1810-x64-server\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"CentOS（Community Enterprise Operating System，中文意思是社区企业操作系统）是Linux发行版之一，它是来自于Red Hat Enterprise Linux依照开放源代码规定释出的源代码所编译而成。由于出自同样的源代码，因此有些要求高度稳定性的服务器以CentOS替代商业版的Red Hat Enterprise Linux使用。两者的不同，在于CentOS完全开源。\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1FuGANivqAABohUhCT2k205.png\",\n" +
                "                \"image_type\": \"vm\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 306,\n" +
                "                \"is_import\": true,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 306,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": {\n" +
                "                    \"flavor\": \"m2.1c-1024m-10g\",\n" +
                "                    \"init_support\": true,\n" +
                "                    \"quick_create_support\": true,\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": null,\n" +
                "                            \"hostname\": null,\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"123456\",\n" +
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
                "                    \"snapshot_count\": 0,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"cpu\": 1,\n" +
                "                    \"ram\": 1024,\n" +
                "                    \"disk\": 10\n" +
                "                },\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"e95052ad-15d9-4ded-b002-969a80e69aad\",\n" +
                "                    \"base_image_name\": \"CentOS-7.6.1810-x64-server\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": null,\n" +
                "                    \"image_status\": 2,\n" +
                "                    \"error\": \"\",\n" +
                "                    \"image_source\": 2,\n" +
                "                    \"upload_type\": \"\",\n" +
                "                    \"disk_format\": \"\",\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"ftp_filename\": null,\n" +
                "                    \"user_filename\": \"\",\n" +
                "                    \"device_actions\": null\n" +
                "                },\n" +
                "                \"ext_docker\": null,\n" +
                "                \"is_build_in\": true,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"CentOS-7.6.1810-x64-server\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 9,\n" +
                "                \"uuid\": \"5d896d4d-8f3e-4f2c-8620-104747fb9588\",\n" +
                "                \"updated_at\": \"2022-09-29T03:54:09Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:54:09Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"CentOS-7\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"CentOS 是一个基于Red Hat Linux 提供的可自由使用源代码的企业级Linux发行版本。每个版本的 CentOS都会获得十年的支持（通过安全更新方式）。新版本的 CentOS 大约每两年发行一次，而每个版本的 CentOS 会定期（大概每六个月）更新一次，以便支持新的硬件。这样，建立一个安全、低维护、稳定、高预测性、高重复性的 Linux 环境。\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/02/CmQHAWM1FtKAGFRVAAA73VCukSk91.jpeg\",\n" +
                "                \"image_type\": \"vm\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 306,\n" +
                "                \"is_import\": true,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 306,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": {\n" +
                "                    \"flavor\": \"m2.1c-1024m-10g\",\n" +
                "                    \"init_support\": true,\n" +
                "                    \"quick_create_support\": false,\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": null,\n" +
                "                            \"hostname\": null,\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"password\",\n" +
                "                            \"private_key_enable\": false,\n" +
                "                            \"private_key_path\": null,\n" +
                "                            \"private_key_file_name\": null,\n" +
                "                            \"description\": null,\n" +
                "                            \"manage_mode\": null\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": \"\",\n" +
                "                            \"hostname\": \"\",\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"\",\n" +
                "                            \"password\": \"\",\n" +
                "                            \"private_key_enable\": true,\n" +
                "                            \"private_key_path\": \"group1/M00/00/0D/CmQHAWNGOZmAQ59gAAAGj_1WKnM040.txt\",\n" +
                "                            \"private_key_file_name\": \"id_rsa.txt\",\n" +
                "                            \"description\": \"\",\n" +
                "                            \"manage_mode\": true\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"quick_create_params\": [],\n" +
                "                    \"ext_info\": {\n" +
                "                        \"image_name\": \"CentOS-7\",\n" +
                "                        \"message\": \"\"\n" +
                "                    },\n" +
                "                    \"instance_exist\": false,\n" +
                "                    \"snapshot_count\": 0,\n" +
                "                    \"instance_uuid\": \"a82eca6c-20c1-4889-a415-cdeff89783a4\",\n" +
                "                    \"instance_state\": 2,\n" +
                "                    \"cpu\": 1,\n" +
                "                    \"ram\": 1024,\n" +
                "                    \"disk\": 10\n" +
                "                },\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"c9a88e32-6764-4cf5-b726-e010b60a426c\",\n" +
                "                    \"base_image_name\": \"CentOS-7\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": null,\n" +
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
                "                \"is_build_in\": true,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"CentOS-7\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"success\": false,\n" +
                "                \"request_id\": null,\n" +
                "                \"total\": 0,\n" +
                "                \"message\": null,\n" +
                "                \"message_zh\": null,\n" +
                "                \"error_message\": null,\n" +
                "                \"code\": null,\n" +
                "                \"tmpdir_uuid\": null,\n" +
                "                \"file_names\": null,\n" +
                "                \"msg\": null,\n" +
                "                \"msg_zh\": null,\n" +
                "                \"url\": null,\n" +
                "                \"id\": 8,\n" +
                "                \"uuid\": \"c2565bd9-89f0-4d23-aac8-d036486a182e\",\n" +
                "                \"updated_at\": \"2022-10-01T10:50:02Z\",\n" +
                "                \"deleted\": false,\n" +
                "                \"created_at\": \"2022-09-29T03:24:30Z\",\n" +
                "                \"deleted_at\": null,\n" +
                "                \"name\": \"secops001\",\n" +
                "                \"image\": null,\n" +
                "                \"description\": \"secops001\",\n" +
                "                \"logo\": \"/fdfs/group1/M00/00/08/CmQHAWM2rw-AAIEBAAAcmPDM4mc160.png\",\n" +
                "                \"image_type\": \"container\",\n" +
                "                \"maintype\": 0,\n" +
                "                \"role\": 4,\n" +
                "                \"category\": 1,\n" +
                "                \"second_category\": 2,\n" +
                "                \"l3_category\": 306,\n" +
                "                \"is_import\": false,\n" +
                "                \"is_internal\": false,\n" +
                "                \"is_init\": false,\n" +
                "                \"ext_entity\": null,\n" +
                "                \"ext_entity_access\": null,\n" +
                "                \"ext_system_info\": {\n" +
                "                    \"parent_system_type\": 2,\n" +
                "                    \"system_type\": 306,\n" +
                "                    \"system_architecture\": \"64\",\n" +
                "                    \"language\": \"en\",\n" +
                "                    \"system_type_name\": null,\n" +
                "                    \"system_architecture_name\": null,\n" +
                "                    \"language_name\": null\n" +
                "                },\n" +
                "                \"ext_gateway\": null,\n" +
                "                \"ext_vm\": null,\n" +
                "                \"ext_image_info\": {\n" +
                "                    \"uuid\": \"e4b69705-3d65-4586-b622-ee8569e42eb5\",\n" +
                "                    \"base_image_name\": \"secops001\",\n" +
                "                    \"base_image_support\": true,\n" +
                "                    \"meta_data\": {\n" +
                "                        \"hw_disk_bus\": \"\",\n" +
                "                        \"hw_vif_model\": \"\",\n" +
                "                        \"hw_video_model\": \"\"\n" +
                "                    },\n" +
                "                    \"image_status\": 2,\n" +
                "                    \"error\": \"\",\n" +
                "                    \"image_source\": 2,\n" +
                "                    \"upload_type\": \"\",\n" +
                "                    \"disk_format\": \"\",\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"ftp_filename\": null,\n" +
                "                    \"user_filename\": \"\",\n" +
                "                    \"device_actions\": null\n" +
                "                },\n" +
                "                \"ext_docker\": {\n" +
                "                    \"file\": null,\n" +
                "                    \"settings\": null,\n" +
                "                    \"sync_status\": null,\n" +
                "                    \"tmpdir_uuid\": null,\n" +
                "                    \"user_filename\": null,\n" +
                "                    \"instance_uuid\": \"\",\n" +
                "                    \"instance_state\": 0,\n" +
                "                    \"init_support\": true,\n" +
                "                    \"quick_create_support\": true,\n" +
                "                    \"snapshot_count\": 0,\n" +
                "                    \"ext_info\": {},\n" +
                "                    \"access_protocols\": [\n" +
                "                        {\n" +
                "                            \"protocol\": \"ssh\",\n" +
                "                            \"auth_protocol\": \"\",\n" +
                "                            \"hostname\": \"\",\n" +
                "                            \"port\": 22,\n" +
                "                            \"username\": \"root\",\n" +
                "                            \"password\": \"ycxx123#\",\n" +
                "                            \"private_key_enable\": false,\n" +
                "                            \"private_key_path\": \"\",\n" +
                "                            \"private_key_file_name\": \"\",\n" +
                "                            \"description\": \"\",\n" +
                "                            \"manage_mode\": false\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"protocol\": \"http\",\n" +
                "                            \"auth_protocol\": \"\",\n" +
                "                            \"hostname\": \"\",\n" +
                "                            \"port\": 80,\n" +
                "                            \"username\": \"\",\n" +
                "                            \"password\": \"\",\n" +
                "                            \"private_key_enable\": false,\n" +
                "                            \"private_key_path\": \"\",\n" +
                "                            \"private_key_file_name\": \"\",\n" +
                "                            \"description\": \"\",\n" +
                "                            \"manage_mode\": false\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"protocol\": \"console\",\n" +
                "                            \"auth_protocol\": \"\",\n" +
                "                            \"hostname\": \"\",\n" +
                "                            \"port\": null,\n" +
                "                            \"username\": \"\",\n" +
                "                            \"password\": \"\",\n" +
                "                            \"private_key_enable\": null,\n" +
                "                            \"private_key_path\": null,\n" +
                "                            \"private_key_file_name\": null,\n" +
                "                            \"description\": \"\",\n" +
                "                            \"manage_mode\": false\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                \"is_build_in\": false,\n" +
                "                \"is_hide\": false,\n" +
                "                \"default_launch_image_name\": \"secops001\"\n" +
                "            }\n" +
                "        ]";

        List<Device> winDevices = JSONObject.parseArray(win, Device.class);
        List<Device> linuxDevices = JSONObject.parseArray(linux, Device.class);
        List<Device> allDevices = new ArrayList<>();
        allDevices.addAll(winDevices);
        allDevices.addAll(linuxDevices);

        List<Device> finalList = null;
        if (Objects.equals(systemType, 1)) {
            finalList = winDevices;
        } else if (Objects.equals(systemType, 2)) {
            finalList = linuxDevices;
        } else {
            finalList = allDevices;
        }

        return new ListResult<>(finalList);
    }


    /**
     * 查询所有靶标信息
     * @param request
     * @return
     */
    @ApiOperation("查询所有靶标部分信息")
    @GetMapping("/list-part-info")
    public ServiceResult<DeviceTargetByTypeVO> listPartInfo(TargetSceneRequest request){
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        List<TypeConvertModel> firstTypeList = l1AndL2AndL3Type.getFirstTypeList();
        List<TypeConvertModel> secondTypeList = l1AndL2AndL3Type.getSecondTypeList();

        Integer category = request.getCategory();
        if (Objects.nonNull(category)) {
            firstTypeList = firstTypeList.stream().filter(v -> v.getId().equals(category)).collect(Collectors.toList());
        }
        Integer secondCategory = request.getSecondCategory();
        if (Objects.nonNull(secondCategory)) {
            secondTypeList = secondTypeList.stream().filter(v -> v.getId().equals(secondCategory)).collect(Collectors.toList());
        }

        List<DeviceTargetByTypeVO> targetScenes = new ArrayList<>();
        for (TypeConvertModel first : firstTypeList){
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = secondTypeList.stream()
                    .filter(v -> v.getL1ParentId().equals(firstId)).collect(Collectors.toList());
            List<Integer> secondCategories = typeConvertModels.stream()
                    .map(model -> model.getId())
                    .collect(Collectors.toList());
            request.setCategory(firstId);
            request.setSecondCategories(secondCategories);
            request.setPage(null);
            request.setPerPage(null);
            BasePageBO<DeviceTargetPO> targetSceneConvertModel = targetConverter.page(new ImagePageDTO(request));
            if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())) {
                continue;
            }
            List<DeviceTargetPO> deviceTargetPOList = targetSceneConvertModel.getList();

            List<DeviceTargetVO> deviceTargetVOList = deviceTargetPOList.stream().map(v -> {
                DeviceTargetVO vo = new DeviceTargetVO();
                vo.setUuid(v.getUuid());
                vo.setName(v.getName());
                vo.setLogo(v.getLogo());
                vo.setRole(v.getRole());
                vo.setCategory(v.getMainCategory());
                vo.setSecondCategory(v.getSubCategory());
                vo.setThirdCategory(v.getThirdCategory());
                vo.setImageType(v.getImageType());
                return vo;
            }).collect(Collectors.toList());
            typeConvertModels.forEach(typeConvertModel -> {
                DeviceTargetByTypeVO targetScene = new DeviceTargetByTypeVO();
                targetScene.setFirstTypeId(firstId);
                targetScene.setFirstTypeName(first.getName());
                targetScene.setSecondTypeId(typeConvertModel.getId());
                targetScene.setSecondTypeName(typeConvertModel.getName());
                targetScene.setDeviceList(deviceTargetVOList.stream()
                        .filter(vo -> vo.getSecondCategory().equals(typeConvertModel.getId()))
                        .collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(targetScene.getDeviceList())) {
                    targetScenes.add(targetScene);
                }
            });
        }
        return new Result(new ListResult<>(targetScenes));
    }

    @ApiOperation("根据UUID查询靶标信息")
    @ApiImplicitParam(name = "uuid", value = "靶标id", dataTypeClass = String.class)
    @GetMapping("/get-by-uuid")
    public Result<DeviceVO> findByUuid(@RequestParam String uuid){
        Device device = targetConverter.findByUuid(uuid);
        DeviceVO deviceVO = deviceVoConverter.sourceToTarget(device);
        return new Result<>(deviceVO);
    }

    @ApiOperation("分页查询靶标信息")
    @GetMapping("/page")
    public PageResult<DeviceVO> page(TargetSceneRequest request){
        BasePageBO<Device> targetSceneConvertModel = targetConverter.allTarget(new ImagePageDTO(request));
        if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<Device> deviceList = targetSceneConvertModel.getList();

        List<DeviceVO> deviceVOList = deviceVoConverter.sourceToTarget(deviceList);

        return new PageResult<>(deviceVOList,targetSceneConvertModel.getTotal());
    }

    @ApiOperation("查询常用靶标信息")
    @GetMapping("/common-targets")
    public ServiceResult<DeviceVO> getCommonTargets(TargetSceneRequest request){
        /*List<Device> deviceList = targetConverter.getCommonTargets("");
        List<DeviceVO> deviceVOList = deviceVoConverter.sourceToTarget(deviceList);

        Locale locale = LocaleContextHolder.getLocale();
        if (Objects.equals(locale, Locale.CHINA)) {
            // 常用靶标名称国际化处理
            deviceVOList.forEach(device -> {
                device.setImage(device.getName());
                device.setName(
                        Objects.nonNull(CommonTargetNameEnum.map.get(device.getName())) ?
                                CommonTargetNameEnum.map.get(device.getName()).toValue() : device.getName());
            });
        }

        if (Objects.nonNull(request.getImageType())) {
            deviceVOList = deviceVOList.stream().filter(v -> v.getImageType().equals(request.getImageType().toValue())).collect(Collectors.toList());
        }
        if (Objects.nonNull(request.getRole())) {
            deviceVOList = deviceVOList.stream().filter(deviceVO -> deviceVO.getRole().equals(request.getRole())).collect(Collectors.toList());
        }*/

        List<DeviceVO> deviceVOList = mockDeviceVOs();
        return new Result(new ListResult(deviceVOList));
    }

    private List<DeviceVO> mockDeviceVOs() {
        String arrStr = "[\n" +
                "  {\n" +
                "    \"id\": 6,\n" +
                "    \"uuid\": \"0e62a73f-a757-402e-8567-77a16b2e7835\",\n" +
                "    \"updated_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted\": false,\n" +
                "    \"created_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted_at\": null,\n" +
                "    \"name\": \"核心路由器\",\n" +
                "    \"image\": \"Core Router\",\n" +
                "    \"description\": \"internal, do not delete\",\n" +
                "    \"logo\": \"/fdfs/group1/M00/00/09/CmQHAWM2rw-AQv_pAAAW4uVC4D4655.png\",\n" +
                "    \"image_type\": \"vm\",\n" +
                "    \"maintype\": 0,\n" +
                "    \"role\": 21,\n" +
                "    \"category\": 3,\n" +
                "    \"second_category\": 51,\n" +
                "    \"third_category\": 0,\n" +
                "    \"is_import\": false,\n" +
                "    \"is_internal\": true,\n" +
                "    \"ext_entity\": null,\n" +
                "    \"ext_entity_access\": null,\n" +
                "    \"ext_system_info\": {\n" +
                "      \"parent_system_type\": 2,\n" +
                "      \"system_type\": 314,\n" +
                "      \"system_architecture\": \"64\",\n" +
                "      \"language\": \"en\",\n" +
                "      \"system_type_name\": null,\n" +
                "      \"system_architecture_name\": null,\n" +
                "      \"language_name\": null\n" +
                "    },\n" +
                "    \"ext_gateway\": {\n" +
                "      \"port_configs\": [],\n" +
                "      \"console_config\": {\n" +
                "        \"protocol\": \"console\",\n" +
                "        \"auth_protocol\": null,\n" +
                "        \"hostname\": null,\n" +
                "        \"port\": null,\n" +
                "        \"username\": \"vyos\",\n" +
                "        \"password\": \"vyos\",\n" +
                "        \"private_key_enable\": null,\n" +
                "        \"private_key_path\": null,\n" +
                "        \"private_key_file_name\": null,\n" +
                "        \"description\": null,\n" +
                "        \"manage_mode\": null\n" +
                "      }\n" +
                "    },\n" +
                "    \"ext_vm\": {\n" +
                "      \"flavor\": \"m2.1c-1024m-10g\",\n" +
                "      \"init_support\": false,\n" +
                "      \"quick_create_support\": false,\n" +
                "      \"access_protocols\": [],\n" +
                "      \"quick_create_params\": [],\n" +
                "      \"ext_info\": {},\n" +
                "      \"instance_exist\": false,\n" +
                "      \"snapshot_count\": 0,\n" +
                "      \"instance_uuid\": \"\",\n" +
                "      \"instance_state\": 0,\n" +
                "      \"cpu\": 1,\n" +
                "      \"ram\": 1024,\n" +
                "      \"disk\": 10\n" +
                "    },\n" +
                "    \"ext_image_info\": {\n" +
                "      \"uuid\": \"389414a7-8e35-41a5-9b57-340a6dbbb283\",\n" +
                "      \"base_image_name\": \"Core Router\",\n" +
                "      \"base_image_support\": true,\n" +
                "      \"meta_data\": null,\n" +
                "      \"image_status\": 2,\n" +
                "      \"error\": \"\",\n" +
                "      \"image_source\": 2,\n" +
                "      \"upload_type\": \"\",\n" +
                "      \"disk_format\": \"\",\n" +
                "      \"tmpdir_uuid\": null,\n" +
                "      \"ftp_filename\": null,\n" +
                "      \"user_filename\": \"\",\n" +
                "      \"device_actions\": null\n" +
                "    },\n" +
                "    \"ext_docker\": null,\n" +
                "    \"is_build_in\": true,\n" +
                "    \"is_hide\": false,\n" +
                "    \"default_launch_image_name\": null\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"uuid\": \"66e6244c-90d1-48d6-a615-0762af79e4f2\",\n" +
                "    \"updated_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted\": false,\n" +
                "    \"created_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted_at\": null,\n" +
                "    \"name\": \"交换机\",\n" +
                "    \"image\": \"Switch Simple\",\n" +
                "    \"description\": \"internal, do not delete\",\n" +
                "    \"logo\": \"/fdfs/group1/M00/00/09/CmQHAWM2rw-AYmGsAAAK_yyW5mU973.png\",\n" +
                "    \"image_type\": \"simple\",\n" +
                "    \"maintype\": 0,\n" +
                "    \"role\": 22,\n" +
                "    \"category\": 3,\n" +
                "    \"second_category\": 50,\n" +
                "    \"third_category\": 0,\n" +
                "    \"is_import\": false,\n" +
                "    \"is_internal\": true,\n" +
                "    \"ext_entity\": null,\n" +
                "    \"ext_entity_access\": null,\n" +
                "    \"ext_system_info\": null,\n" +
                "    \"ext_gateway\": null,\n" +
                "    \"ext_vm\": null,\n" +
                "    \"ext_image_info\": null,\n" +
                "    \"ext_docker\": null,\n" +
                "    \"is_build_in\": true,\n" +
                "    \"is_hide\": false,\n" +
                "    \"default_launch_image_name\": null\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"uuid\": \"854f7c24-060f-4dbd-b771-68eab207e071\",\n" +
                "    \"updated_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted\": false,\n" +
                "    \"created_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted_at\": null,\n" +
                "    \"name\": \"路由器\",\n" +
                "    \"image\": \"Router\",\n" +
                "    \"description\": \"internal, do not delete\",\n" +
                "    \"logo\": \"/fdfs/group1/M00/00/09/CmQHAWM2rw-AIpEqAAAUL72TDJM231.png\",\n" +
                "    \"image_type\": \"simple\",\n" +
                "    \"maintype\": 0,\n" +
                "    \"role\": 21,\n" +
                "    \"category\": 3,\n" +
                "    \"second_category\": 51,\n" +
                "    \"third_category\": 0,\n" +
                "    \"is_import\": false,\n" +
                "    \"is_internal\": true,\n" +
                "    \"ext_entity\": null,\n" +
                "    \"ext_entity_access\": null,\n" +
                "    \"ext_system_info\": null,\n" +
                "    \"ext_gateway\": null,\n" +
                "    \"ext_vm\": null,\n" +
                "    \"ext_image_info\": null,\n" +
                "    \"ext_docker\": null,\n" +
                "    \"is_build_in\": true,\n" +
                "    \"is_hide\": false,\n" +
                "    \"default_launch_image_name\": null\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 3,\n" +
                "    \"uuid\": \"c12b7b53-e232-40f4-b7c3-d3dfac8e1b31\",\n" +
                "    \"updated_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted\": false,\n" +
                "    \"created_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted_at\": null,\n" +
                "    \"name\": \"防火墙\",\n" +
                "    \"image\": \"Firewall\",\n" +
                "    \"description\": \"internal, do not delete\",\n" +
                "    \"logo\": \"/fdfs/group1/M00/00/09/CmQHAWM2rw-ATmpdAAAJxoatQBo982.png\",\n" +
                "    \"image_type\": \"simple\",\n" +
                "    \"maintype\": 0,\n" +
                "    \"role\": 23,\n" +
                "    \"category\": 3,\n" +
                "    \"second_category\": 52,\n" +
                "    \"third_category\": 0,\n" +
                "    \"is_import\": false,\n" +
                "    \"is_internal\": true,\n" +
                "    \"ext_entity\": null,\n" +
                "    \"ext_entity_access\": null,\n" +
                "    \"ext_system_info\": null,\n" +
                "    \"ext_gateway\": null,\n" +
                "    \"ext_vm\": null,\n" +
                "    \"ext_image_info\": null,\n" +
                "    \"ext_docker\": null,\n" +
                "    \"is_build_in\": true,\n" +
                "    \"is_hide\": false,\n" +
                "    \"default_launch_image_name\": null\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 4,\n" +
                "    \"uuid\": \"5dfe83ac-4689-478d-8257-89bb118ef105\",\n" +
                "    \"updated_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted\": false,\n" +
                "    \"created_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted_at\": null,\n" +
                "    \"name\": \"外部网络\",\n" +
                "    \"image\": \"Internet\",\n" +
                "    \"description\": \"internal, do not delete\",\n" +
                "    \"logo\": \"/fdfs/group1/M00/00/09/CmQHAWM2rw-AL9ukAAAXAo3jjks237.png\",\n" +
                "    \"image_type\": \"simple\",\n" +
                "    \"maintype\": 0,\n" +
                "    \"role\": 3,\n" +
                "    \"category\": 3,\n" +
                "    \"second_category\": 53,\n" +
                "    \"third_category\": 0,\n" +
                "    \"is_import\": false,\n" +
                "    \"is_internal\": true,\n" +
                "    \"ext_entity\": null,\n" +
                "    \"ext_entity_access\": null,\n" +
                "    \"ext_system_info\": null,\n" +
                "    \"ext_gateway\": null,\n" +
                "    \"ext_vm\": null,\n" +
                "    \"ext_image_info\": null,\n" +
                "    \"ext_docker\": null,\n" +
                "    \"is_build_in\": true,\n" +
                "    \"is_hide\": false,\n" +
                "    \"default_launch_image_name\": null\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 5,\n" +
                "    \"uuid\": \"1e64d97a-f0b3-4704-be21-8121a9b3ef8f\",\n" +
                "    \"updated_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted\": false,\n" +
                "    \"created_at\": \"2022-09-29T01:17:01Z\",\n" +
                "    \"deleted_at\": null,\n" +
                "    \"name\": \"仿真节点\",\n" +
                "    \"image\": \"NPC\",\n" +
                "    \"description\": \"internal, do not delete\",\n" +
                "    \"logo\": \"/fdfs/group1/M00/00/09/CmQHAWM2rw-ALx1KAAAJHcVCK58331.png\",\n" +
                "    \"image_type\": \"simple\",\n" +
                "    \"maintype\": 0,\n" +
                "    \"role\": 7,\n" +
                "    \"category\": 3,\n" +
                "    \"second_category\": 54,\n" +
                "    \"third_category\": 0,\n" +
                "    \"is_import\": false,\n" +
                "    \"is_internal\": true,\n" +
                "    \"ext_entity\": null,\n" +
                "    \"ext_entity_access\": null,\n" +
                "    \"ext_system_info\": null,\n" +
                "    \"ext_gateway\": null,\n" +
                "    \"ext_vm\": null,\n" +
                "    \"ext_image_info\": null,\n" +
                "    \"ext_docker\": null,\n" +
                "    \"is_build_in\": true,\n" +
                "    \"is_hide\": false,\n" +
                "    \"default_launch_image_name\": null\n" +
                "  }\n" +
                "]";

        List<DeviceVO> deviceVOList = JSONObject.parseArray(arrStr, DeviceVO.class);
        return deviceVOList;
    }

    @ApiOperation("批量同步靶标数据")
    @GetMapping("/multi-async")
    public ServiceResult multiAsync(TargetMultiAsyncRequest request){
        int deleteNum = devopsTargetService.deleteAllTarget();
        log.info("删除靶标数量：{}",deleteNum);
        List<Device> deviceList = targetConverter.multiAsync(request.getOffset(), request.getLimit());
        MultiThreadUtils multiThreadUtils = MultiThreadUtils.newInstance(request.getThreadCount());

        ServiceResult serviceResult = multiThreadUtils.execute(deviceList, new HashMap<>(), asyncTargetTask);

        return serviceResult.asResult();
    }

    @ApiOperation("注册MP地址")
    @ApiImplicitParam(name = "url", value = "地址", dataTypeClass = String.class)
    @GetMapping("/register")
    public ServiceResult register(@RequestParam(required = false) String url){
        initRegisterService.register(url);
        return ResultCommon.success();
    }

    @ApiOperation("上传靶标图标")
    @PostMapping("/upload/icon")
    @ApiImplicitParam(name = "file", value = "文件", dataTypeClass = MultipartFile.class)
    @ResponseBody
    public ServiceResult<FileResponseData> uploadIcon(MultipartFile file){
        return targetIconUploadService.build(file).upload();
    }


    @ApiOperation("校验靶标是否存在")
    @PostMapping("/check")
    @ApiImplicitParam(name = "request", value = "请求体", dataTypeClass = TargetSceneRequest.class)
    @ResponseBody
    public ServiceResult<List<String>> check(@RequestBody TargetSceneRequest request){
        request.setPage(null);
        request.setPerPage(null);
        List<String> nameList = targetConverter.findByImagePageDTO(new ImagePageDTO(request));
        return new Result(nameList);
    }

}
