package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.util.BaseResponse;
import cn.cyberpeace.mp.auth.util.BaseStatusEnum;
import cn.cyberpeace.mp.auth.util.ParentCodeEnum;
import cn.cyberpeace.mp.auth.util.ResponseUtil;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.MicrosoftAzureMapper;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureContainer;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.AzureCreateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import com.alibaba.fastjson.JSONObject;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MicrosoftAzureConverter implements BaseConverter{

    public final String azureTenantId = "e1039762-f748-4410-a19d-eb16ba072acd";

    public final String azureSubscriptionId = "c87ba926-f7c2-4d53-8b8e-89245dc92e26";

    public final String azureClientId = "a8982e4d-099d-4ab0-b6c8-178f50d16fb0";

    public final String azureClientSecret = "26V8Q~gGDhjyHIJLopU-.mws2agepHucAD~hqb7_";

    @Autowired
    private MicrosoftAzureMapper microsoftAzureMapper;

    /**
     * 云镜像管理页面分页查询
     */
    public ListObjServiceResult getCloudTargetList(JSONObject requestData) {
        Integer pageNum = requestData.get("pageNum") != null ? requestData.getInteger("pageNum") : 1;
        Integer pageSize = requestData.get("pageSize") != null ? requestData.getInteger("pageSize") : 10;
        requestData.put("offset", (pageNum - 1) * pageSize);
        List<DeviceCloudTargetPO> list = microsoftAzureMapper.getCloudTargetList(requestData);
        return new ListObjServiceResult<>(new ListObjResult<>(list, list.size()));
    }

    /**
     * 操作系统下拉框
     */
    public ListObjServiceResult getOS() {
        List<JSONObject> oslist = microsoftAzureMapper.getOS(ParentCodeEnum.PARENT_SYSTEM_TYPE.getCode());
        return new ListObjServiceResult<>(new ListObjResult<>(oslist, oslist.size()));
    }

    /**
     * 云镜像管理同步
     */
    public ListObjServiceResult synchronousCloudData(JSONObject requestData) throws Exception {

        // 获取资源管理器
        var azureResourceManager = IAzureResourceManager.getInstance(1).getManager();

        // todo 看云镜像到时是放在云服务本身提供的服务里还是自己部署服务

//        HttpClient httpClient = HttpClient.newHttpClient();
//        // 替换为真实的API端点和请求参数
//        String endpoint = "https://example.api.com/api/v1/resource";
//        String apiKey = "your-api-key"; // 如果需要API密钥
//        String requestBodyJson = "{\"param1\":\"value1\",\"param2\":\"value2\"}";
//
//        HttpRequest request;
//        if (apiKey != null) {
//            // 如果API需要密钥，将其添加到请求头中
//            request = HttpRequest.newBuilder()
//                    .uri(new URI(endpoint))
//                    .header("Authorization", "Bearer " + apiKey) // 假设API使用Bearer令牌
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
//                    .build();
//        } else {
//            // 如果不需要密钥，直接发出GET请求或POST请求（无认证）
//            request = HttpRequest.newBuilder()
//                    .uri(new URI(endpoint))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
//                    .build();
//        }
//
//        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//        System.out.println("Status Code: " + response.statusCode());
//        System.out.println("Response Body: " + response.body());
//
//        //处理返回结果
        return new ListObjServiceResult<>(null, BaseErrorCode.SUCCESS, BaseErrorCode.SUCCESS.getMessage());
    }

    /**
     * 云镜像删除
     */
    public ListObjServiceResult deleteCloudTarget(JSONObject requestData) {
        if(requestData != null && requestData.get("pk") != null){
            Integer pk = microsoftAzureMapper.deleteByPk(requestData.get("pk").toString());
            if(pk>0){
                return new ListObjServiceResult<>();
            }else{
                //影响行数为0
                return new ListObjServiceResult<>(null, BaseErrorCode.DATA_NOT_EXIST, BaseErrorCode.DATA_NOT_EXIST.getMessage());
            }
        }else{
            return new ListObjServiceResult<>(null, BaseErrorCode.PARAMS_INVALID, BaseErrorCode.PARAMS_INVALID.getMessage());
        }
    }

    public ListObjServiceResult updateCloudTarget(JSONObject requestData) {
        if(requestData != null && requestData.get("pk") != null){
            Integer pk = microsoftAzureMapper.updateCloudTarget(requestData.get("pk").toString(),requestData);
            if(pk>0){
                return new ListObjServiceResult<>();
            }else{
                //影响行数为0
                return new ListObjServiceResult<>(null, BaseErrorCode.DATA_NOT_EXIST, BaseErrorCode.DATA_NOT_EXIST.getMessage());
            }
        }else{
            return new ListObjServiceResult<>(null, BaseErrorCode.PARAMS_INVALID, BaseErrorCode.PARAMS_INVALID.getMessage());
        }
    }

    public ListObjServiceResult getCloudTargetByPk(JSONObject requestData) {
        if(requestData != null && requestData.get("pk") != null){
            List<DeviceCloudTargetPO> data = microsoftAzureMapper.getCloudTargetByPk(requestData.get("pk").toString());
            if(data != null && data.size()>0){
                return new ListObjServiceResult<>(new ListObjResult<>(data, data.size()));
            }else{
                //影响行数为0
                return new ListObjServiceResult<>(null, BaseErrorCode.DATA_NOT_EXIST, BaseErrorCode.DATA_NOT_EXIST.getMessage());
            }
        }else{
            return new ListObjServiceResult<>(null, BaseErrorCode.PARAMS_INVALID, BaseErrorCode.PARAMS_INVALID.getMessage());
        }
    }
}
