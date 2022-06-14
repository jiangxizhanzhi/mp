package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @Description 经过多层解析的处理仍没有值的属性, 在这里做默认的处理
 * @Author maojun
 * @Date 2021/10/12
 */
class DefaultValueConverter implements IConverter {
    @Override
    public void convert2BO(ConvertDataContainer container) {
        var cloudElementMap = container.getCloudPluginElements();
        if (cloudElementMap != null) {
            cloudElementMap.forEach((id, e) -> {
                if (e.getImageType() == null && id != null) {
                    // 处理下 imageType
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
            });
        }
    }
}
