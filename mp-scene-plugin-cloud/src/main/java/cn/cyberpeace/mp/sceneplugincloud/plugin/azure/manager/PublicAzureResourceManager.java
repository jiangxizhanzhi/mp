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
public class PublicAzureResourceManager implements IAzureResourceManager {

    private static AzureResourceManager AZURE_RESOURCE_MANAGER;

    @Override
    public AzureResourceManager getManager() {
        if (AZURE_RESOURCE_MANAGER == null) {
            synchronized (this) {
                if (AZURE_RESOURCE_MANAGER == null) {
                    TokenCredential credential = new DefaultAzureCredentialBuilder()
                            .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
                            .build();

                    // If you don't set the tenant ID and subscription ID via environment variables,
                    // change to create the Azure profile with tenantId, subscriptionId, and Azure environment.
                    AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);

                    AZURE_RESOURCE_MANAGER = AzureResourceManager.configure()
                            .withLogLevel(HttpLogDetailLevel.BASIC)
                            .authenticate(credential, profile)
                            .withSubscription("c87ba926-f7c2-4d53-8b8e-89245dc92e26");
                }
            }
        }
        return AZURE_RESOURCE_MANAGER;
    }

    @Override
    public Region getRegin() {
        return Region.ASIA_EAST;
    }
}
