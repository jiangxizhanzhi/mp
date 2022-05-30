package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/12
 */
@Component
public class ResourceCenterConverter {
    @Resource
    private TargetConverter targetConverter;

    public CsceneBO convert(YmlParseResult parseResult, List<ImageParseResult> imageInfos, String sceneFilePath) {
        // 构建用于存储数据的容器
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

        // 构建转换的责任链路
        List<IConverter> converters = new ArrayList<>();
        // 解析scene -> 解析node,创建端口 -> 解析network,创建端口,创建连线 -> 加载其余靶标信息 -> 加载来yml解析出的镜像信息
        converters.add(new SceneElementConverter(parseResult.getScene()));
        converters.add(new NodeElementConverter(parseResult.getNodes()));
        converters.add(new NetworkElementConverter(parseResult.getNetworks()));
        converters.add(new DeviceInfoConverter(this.targetConverter));
        converters.add(new ImageInfoConverter());
        converters.add(new SceneFilePathConverter());
        converters.add(new PortIdAndNameConvert());
        converters.add(new DefaultValueConverter());
        // 依次执行链路
        converters.forEach(converter -> converter.convert2BO(container));

        return container.toCsceneBO();
    }
}
