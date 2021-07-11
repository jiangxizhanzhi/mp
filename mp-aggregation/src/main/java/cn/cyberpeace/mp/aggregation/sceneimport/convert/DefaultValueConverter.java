package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import org.apache.commons.lang3.StringUtils;


class DefaultValueConverter implements IConverter {
    @Override
    public void convert2BO(ConvertDataContainer container) {
        var cloudElementMap = container.getCloudPluginElements();
        if (cloudElementMap != null) {
            cloudElementMap.forEach((id, e) -> {
                if (e.getImageType() == null && id != null) {
                    
                    if (id.toLowerCase().startsWith("router")) {
                        e.setImageType(BaseCloudPluginSceneElementModel.ImageType.SIMPLE);
                    } else if (id.toLowerCase().startsWith("firewall")) {
                        e.setImageType(BaseCloudPluginSceneElementModel.ImageType.SIMPLE);
                    } else if (id.toLowerCase().startsWith("server")) {
                        e.setImageType(BaseCloudPluginSceneElementModel.ImageType.VM);
                    } else {
                        e.setImageType(BaseCloudPluginSceneElementModel.ImageType.SIMPLE);
                    }
                }
                if (e.getCategory() == null && StringUtils.isNotEmpty(id)) {
                    if (id.toLowerCase().startsWith("router")) {
                        e.setCategory(3);
                        e.setCategory2(51);
                    } else if (id.toLowerCase().startsWith("firewall")) {
                        e.setCategory(3);
                        e.setCategory2(52);
                    } else if (id.toLowerCase().startsWith("network")) {
                        e.setCategory(3);
                        e.setCategory2(50);
                    } else if (id.toLowerCase().startsWith("server")) {
                        if (e.getRole() != null && e.getRole() == BaseCloudPluginSceneElementModel.Role.NPC) {
                            e.setCategory(3);
                            e.setCategory2(54);
                        } else {
                            e.setCategory(2);
                        }
                    }
                }
            });
        }
    }
}
