package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import org.springframework.stereotype.Component;

@Component
public class MixAzureResourceManager implements IAzureResourceManager {
    @Override
    public AzureResourceManager getManager() {
        //优先使用配置的环境变量
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
                .build();

        //根据传来的云type配置不同的认证信息,目前三套皆用同一套认证信息
//                    ClientSecretCredential credential = new ClientSecretCredentialBuilder()
//                            .clientId("<client-id>")
//                            .clientSecret("<client-secret>")
//                            .tenantId("<tenant-id>")
//                            .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
//                            .build();

        // If you don't set the tenant ID and subscription ID via environment variables,
        // change to create the Azure profile with tenantId, subscriptionId, and Azure environment.
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);

        AzureResourceManager azureResourceManager = AzureResourceManager.configure()
                .withLogLevel(HttpLogDetailLevel.BASIC)
                .authenticate(credential, profile)
                .withDefaultSubscription();
        return azureResourceManager;
    }

    @Override
    public Region getRegin() {
        return Region.CHINA_EAST;
    }
}
