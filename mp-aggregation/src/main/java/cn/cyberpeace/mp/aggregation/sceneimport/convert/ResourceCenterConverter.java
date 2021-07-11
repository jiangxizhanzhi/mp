package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.devopscluster.converter.TargetConverter;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Component
public class ResourceCenterConverter {
    @Resource
    private TargetConverter targetConverter;

    public CsceneBO convert(YmlParseResult parseResult, List<ImageParseResult> imageInfos, String sceneFilePath) {
        
        ConvertDataContainer container = new ConvertDataContainer();
        container.setSceneFilePath(sceneFilePath);
        if (CollectionUtils.isNotEmpty(imageInfos)) {
            imageInfos.stream().forEach(imageInfo -> {
                if (imageInfo.getServers() != null) {
                    imageInfo.getServers().forEach((id, server) -> {
                        container.putImageInfo(id, server);
                    });
                }
            });
        }

        
        List<IConverter> converters = new ArrayList<>();
        
        converters.add(new PrepareConverter(parseResult));
        converters.add(new SceneElementConverter(parseResult.getScene()));
        converters.add(new NodeElementConverter(parseResult.getNodes()));
        converters.add(new NetworkElementConverter(parseResult.getNetworks()));
        converters.add(new Node2NodeConnectConverter(parseResult.getNodes()));
        converters.add(new DeviceInfoConverter(this.targetConverter));
        converters.add(new ImageInfoConverter());
        converters.add(new SceneFilePathConverter());
        converters.add(new PortIdAndNameConvert());
        converters.add(new DefaultValueConverter());
        converters.add(new SceneElementShapeConverter(parseResult.getShape()));
        converters.add(new NetConfigElementConvert());
        converters.add(new ConnectionDisplayElementConvert());
        
        converters.forEach(converter -> converter.convert2BO(container));

        var csceneBO = container.toCsceneBO();
        if (CollectionUtils.isNotEmpty(csceneBO.getSceneConfig().getTopology().getElements())) {
            csceneBO.getSceneConfig().getTopology().getElements().forEach(e -> {
                if (CollectionUtils.isNotEmpty(e.getPorts())) {
                    String prefix = (e.getType() != null && e.getType() == BaseSceneElementModel.Type.TERMINAL) ? "eth0" : "E0";
                    for (int i = 0; i < e.getPorts().size(); i++) {
                        if (container.getEntityPortId2Name().containsKey(e.getPorts().get(i).getId())) {
                            continue;
                        }
                        e.getPorts().get(i).setName(prefix + "/" + (i + 1));
                    }
                }
            });
        }

        return csceneBO;
    }
}
