package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.util.BaseResponse;
import cn.cyberpeace.mp.auth.util.BaseStatusEnum;
import cn.cyberpeace.mp.auth.util.ParentCodeEnum;
import cn.cyberpeace.mp.auth.util.ResponseUtil;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.MicrosoftAzureMapper;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureContainer;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.AzureCreateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import com.alibaba.fastjson.JSONObject;
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

    @Value("${microsoft.azure.azure_tenant_id}")
    public String azureTenantId;

    @Value("${microsoft.azure.azure_subscription_id}")
    public String azureSubscriptionId;

    @Value("${microsoft.azure.azure_client_id}")
    public String azureClientId;

    @Value("${microsoft.azure.azure_client_secret}")
    public String azureClientSecret;

    @Autowired
    private MicrosoftAzureMapper microsoftAzureMapper;

    public BaseResponse getCloudTargetList(JSONObject requestData) {
        Integer pageNum = requestData.get("pageNum") != null ? requestData.getInteger("pageNum") : 1;
        Integer pageSize = requestData.get("pageSize") != null ? requestData.getInteger("pageSize") : 10;
        PageHelper.startPage(pageNum,pageSize);
        List<DeviceCloudTargetPO> list = microsoftAzureMapper.getCloudTargetList(requestData);
        PageInfo<DeviceCloudTargetPO> pageInfo = new PageInfo<>(list);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 云镜像管理同步
     */
    public BaseResponse synchronousCloudData(JSONObject requestData) throws Exception {

        // 获取资源管理器
        var az = IAzureResourceManager.getInstance(1).getManager();


        HttpClient httpClient = HttpClient.newHttpClient();
        // 替换为真实的API端点和请求参数
        String endpoint = "https://example.api.com/api/v1/resource";
        String apiKey = "your-api-key"; // 如果需要API密钥
        String requestBodyJson = "{\"param1\":\"value1\",\"param2\":\"value2\"}";

        HttpRequest request;
        if (apiKey != null) {
            // 如果API需要密钥，将其添加到请求头中
            request = HttpRequest.newBuilder()
                    .uri(new URI(endpoint))
                    .header("Authorization", "Bearer " + apiKey) // 假设API使用Bearer令牌
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();
        } else {
            // 如果不需要密钥，直接发出GET请求或POST请求（无认证）
            request = HttpRequest.newBuilder()
                    .uri(new URI(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();
        }

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

        //处理返回结果

        return ResponseUtil.success();
    }

    /**
     * 云镜像删除
     */
    public BaseResponse deleteCloudTarget(JSONObject requestData) {
        if(requestData.get("pk") != null){
            Integer pk = microsoftAzureMapper.deleteByPk(requestData.get("pk").toString());
            if(pk>0){
                return ResponseUtil.success();
            }else if(pk == 0){
                //影响行数为0
                return ResponseUtil.error(BaseStatusEnum.RECORD_NOT_FOUND.getCode(),BaseStatusEnum.RECORD_NOT_FOUND.getMessage());
            }else{
                return ResponseUtil.error(BaseStatusEnum.DB_OPERATE_ERROR.getCode(),BaseStatusEnum.DB_OPERATE_ERROR.getMessage());
            }
        }else{
            return ResponseUtil.error(BaseStatusEnum.PARAMETER_IS_NULL.getCode(),BaseStatusEnum.PARAMETER_IS_NULL.getMessage());
        }
    }

    public BaseResponse updateCloudTarget(JSONObject requestData) {
        if(requestData.get("pk") != null){
            Integer pk = microsoftAzureMapper.updateCloudTarget(requestData.get("pk").toString(),requestData);
            if(pk>0){
                return ResponseUtil.success();
            }else if(pk == 0){
                //影响行数为0
                return ResponseUtil.error(BaseStatusEnum.RECORD_NOT_FOUND.getCode(),BaseStatusEnum.RECORD_NOT_FOUND.getMessage());
            }else{
                return ResponseUtil.error(BaseStatusEnum.DB_OPERATE_ERROR.getCode(),BaseStatusEnum.DB_OPERATE_ERROR.getMessage());
            }
        }else{
            return ResponseUtil.error(BaseStatusEnum.PARAMETER_IS_NULL.getCode(),BaseStatusEnum.PARAMETER_IS_NULL.getMessage());
        }
    }

    public BaseResponse getOS(JSONObject requestData) {
        List<JSONObject> oslist = microsoftAzureMapper.getOS(ParentCodeEnum.PARENT_SYSTEM_TYPE.getCode());
        return ResponseUtil.success(oslist);
    }
}
