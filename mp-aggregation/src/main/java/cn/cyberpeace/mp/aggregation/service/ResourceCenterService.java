package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.sceneimport.convert.ResourceCenterConverter;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.ResourceCenterYmlParser;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/11
 */
@Service
public class ResourceCenterService {

    @Resource
    private CsceneService csceneService;

    @Resource
    private ResourceCenterConverter cSceneBoConverter;

    public ServiceResult<CsceneBO> importByYmlConfig(String ymlFilePath, String imageFilePath) {
        // 解析 topo.yml
        YmlParseResult ymlParseResult = ResourceCenterYmlParser.parseTopoYml(ymlFilePath);
        // 解析 image/services.yml
        List<ImageParseResult> imageInfos = ResourceCenterYmlParser.parseImageServicesYml(ymlFilePath, imageFilePath);
        // 转换bo
        CsceneBO csceneBO = cSceneBoConverter.convert(ymlParseResult, imageInfos);
        // 保存
        return csceneService.syncWithoutCheckLimit(csceneBO, new CsceneContextBO());
    }
}
