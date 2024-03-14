package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;

public interface IAzureResourceManager {
    AzureResourceManager getManager();

    Region getRegin();

    static IAzureResourceManager getInstance(int type) {
        if (BaseSceneModel.CloudType.PUBLIC == type) {
            return BeanCommon.getBean(PublicAzureResourceManager.class);
        } else if (BaseSceneModel.CloudType.PRIVATE == type) {
            return BeanCommon.getBean(PrivateAzureResourceManager.class);
        } else if (BaseSceneModel.CloudType.MIX == type) {
            return BeanCommon.getBean(MixAzureResourceManager.class);
        } else {
            return BeanCommon.getBean(MixAzureResourceManager.class);
        }
    }
}
