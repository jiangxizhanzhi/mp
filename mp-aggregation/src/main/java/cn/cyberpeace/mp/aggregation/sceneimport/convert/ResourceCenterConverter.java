package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
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

    public CsceneBO convert(YmlParseResult parseResult) {
        // 构建转换的责任链路
        List<IConverter> converters = new ArrayList<>();
        // 解析scene -> 解析node,创建端口 -> 解析network,创建端口,创建连线 -> 加载其余靶标信息
        converters.add(new SceneElementConverter(parseResult.getScene()));
        converters.add(new NodeElementConverter(parseResult.getNodes()));
        converters.add(new NetworkElementConverter(parseResult.getNetworks()));
        converters.add(new DeviceInfoConverter(this.targetConverter));

        // 依次执行链路
        ConvertDataContainer container = new ConvertDataContainer();
        converters.forEach(converter -> converter.convert2BO(container));

        return container.toCsceneBO();
    }
}
