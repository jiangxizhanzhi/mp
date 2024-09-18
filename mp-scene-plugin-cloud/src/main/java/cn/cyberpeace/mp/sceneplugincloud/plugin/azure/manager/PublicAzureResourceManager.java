package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.service.impl.MicrosoftAzureLoginServiceImpl;
import com.alibaba.fastjson.JSONObject;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.loganalytics.LogAnalyticsManager;
import com.azure.resourcemanager.monitor.MonitorManager;
import com.azure.resourcemanager.resourcegraph.ResourceGraphManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublicAzureResourceManager implements IAzureResourceManager {

    // 订阅(默认,当获取user时,覆盖)
    private static String subscription = "c87ba926-f7c2-4d53-8b8e-89245dc92e26";

    //当前云环境资源管理器
    private static AzureResourceManager AZURE_RESOURCE_MANAGER;
    //当前云环境监控管理器
    private static MonitorManager AZURE_MONITOR_MANAGER;

    private static ResourceGraphManager AZURE_RESOURCE_GRAPH_MANAGER;

    private static LogAnalyticsManager AZURE_LOG_ANALYTICS_MANAGER;

    //当前云环境账号
    private static MicrosoftAzureUserModel user;

    //当前云环境令牌凭据
    private static TokenCredential tokenCredential;

    @Autowired
    private MicrosoftAzureLoginServiceImpl microsoftAzureLoginService;


    @Override
    public MicrosoftAzureUserModel getUser() {
        if (user == null) {
            synchronized (this) {
                if (user == null) {
                    JSONObject jsonObject = new JSONObject();
                    //云类型(1: 微软公有云 2:微软私有云 3:微软混合云)
                    jsonObject.put("cloudType", 1);
                    MicrosoftAzureUserModel localAzurer = microsoftAzureLoginService.getLocalAzurer(jsonObject);
                    this.user = localAzurer;
                    if(null != localAzurer && null != localAzurer.getAzureSubscriptionId()){
                        this.subscription = localAzurer.getAzureSubscriptionId();
                    }
                }
            }
        }
        return user;
    }

    @Override
    public TokenCredential getTokenCredential() {
        if (tokenCredential == null) {
            synchronized (this) {
                if (tokenCredential == null) {
                    MicrosoftAzureUserModel localAzurer = null;
                    if (user == null) {
                        localAzurer = getUser();
                    }else{
                        localAzurer = user;
                    }
                    //优先使用配置的环境变量
//                    TokenCredential credential = new DefaultAzureCredentialBuilder()
//                            .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
//                            .build();

                    //根据传来的云type配置不同的认证信息,目前三套皆用同一套认证信息
                    ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                            .clientId(localAzurer.getAzureClientId())
                            .clientSecret(localAzurer.getAzureClientSecret())
                            .tenantId(localAzurer.getAzureTenantId())
                            .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
                            .build();
                    this.tokenCredential = credential;
                }
            }
        }
        return tokenCredential;
    }

    @Override
    public AzureResourceManager getManager() {
        if (AZURE_RESOURCE_MANAGER == null) {
            synchronized (this) {
                if (AZURE_RESOURCE_MANAGER == null) {
                    TokenCredential credential = getTokenCredential();
                    // If you don't set the tenant ID and subscription ID via environment variables,
                    // change to create the Azure profile with tenantId, subscriptionId, and Azure environment.
                    AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
                    AZURE_RESOURCE_MANAGER = AzureResourceManager.configure()
                            .withLogLevel(HttpLogDetailLevel.BASIC)
                            .authenticate(credential, profile)
                            .withSubscription(subscription);
                }
            }
        }
        return AZURE_RESOURCE_MANAGER;
    }

    @Override
    public MonitorManager getMonitorManager() {
        if (AZURE_MONITOR_MANAGER == null) {
            synchronized (this) {
                if (AZURE_MONITOR_MANAGER == null) {
                    TokenCredential credential = getTokenCredential();
                    MicrosoftAzureUserModel localAzurer = null;
                    if (user == null) {
                        localAzurer = getUser();
                    }else{
                        localAzurer = user;
                    }
                    AzureProfile profile = new AzureProfile(localAzurer.getAzureTenantId(),
                            subscription,
                            AzureEnvironment.AZURE);
                    AZURE_MONITOR_MANAGER = MonitorManager.configure()
                            .withLogLevel(HttpLogDetailLevel.BASIC)
                            .authenticate(credential, profile);
                }
            }
        }
        return AZURE_MONITOR_MANAGER;
    }

    @Override
    public ResourceGraphManager getResourceGraphManager() {
        if (AZURE_RESOURCE_GRAPH_MANAGER == null) {
            synchronized (this) {
                if (AZURE_RESOURCE_GRAPH_MANAGER == null) {
                    TokenCredential credential = getTokenCredential();
                    MicrosoftAzureUserModel localAzurer = null;
                    if (user == null) {
                        localAzurer = getUser();
                    }else{
                        localAzurer = user;
                    }
                    AzureProfile profile = new AzureProfile(localAzurer.getAzureTenantId(),
                            subscription,
                            AzureEnvironment.AZURE);
                    AZURE_RESOURCE_GRAPH_MANAGER = ResourceGraphManager.configure()
                            .authenticate(credential, profile);
                }
            }
        }
        return AZURE_RESOURCE_GRAPH_MANAGER;
    }

    @Override
    public LogAnalyticsManager getLogAnalyticsManager() {
        if (AZURE_LOG_ANALYTICS_MANAGER == null) {
            synchronized (this) {
                if (AZURE_LOG_ANALYTICS_MANAGER == null) {
                    TokenCredential credential = getTokenCredential();
                    MicrosoftAzureUserModel localAzurer = null;
                    if (user == null) {
                        localAzurer = getUser();
                    }else{
                        localAzurer = user;
                    }
                    AzureProfile profile = new AzureProfile(localAzurer.getAzureTenantId(),
                            subscription,
                            AzureEnvironment.AZURE);
                    AZURE_LOG_ANALYTICS_MANAGER = LogAnalyticsManager.configure()
                            .authenticate(credential, profile);
                }
            }
        }
        return AZURE_LOG_ANALYTICS_MANAGER;
    }

    @Override
    public void init() {
        JSONObject jsonObject = new JSONObject();
        //云类型(1: 微软公有云 2:微软私有云 3:微软混合云)
        jsonObject.put("cloudType", 1);
        MicrosoftAzureUserModel localAzurer = microsoftAzureLoginService.getLocalAzurer(jsonObject);
        this.user = localAzurer;
        if(null != localAzurer && null != localAzurer.getAzureSubscriptionId()){
            this.subscription = localAzurer.getAzureSubscriptionId();
        }
        //优先使用配置的环境变量
//        TokenCredential credential = new DefaultAzureCredentialBuilder()
//                .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
//                .build();

        //根据传来的云type配置不同的认证信息,目前三套皆用同一套认证信息
        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .clientId(localAzurer.getAzureClientId())
                .clientSecret(localAzurer.getAzureClientSecret())
                .tenantId(localAzurer.getAzureTenantId())
                .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
                .build();
        this.tokenCredential = credential;

        // If you don't set the tenant ID and subscription ID via environment variables,
        // change to create the Azure profile with tenantId, subscriptionId, and Azure environment.
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        AZURE_RESOURCE_MANAGER = AzureResourceManager.configure()
                .withLogLevel(HttpLogDetailLevel.BASIC)
                .authenticate(credential, profile)
                .withSubscription(subscription);
    }

    @Override
    public Region getRegin() {
        return Region.ASIA_EAST;
    }
}
