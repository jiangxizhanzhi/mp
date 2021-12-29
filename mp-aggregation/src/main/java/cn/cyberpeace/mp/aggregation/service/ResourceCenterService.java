package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.sceneimport.convert.ResourceCenterConverter;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.ResourceCenterYmlParser;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;
import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    private static String FDFS_SCENE_FILEPATH_FORMAT = "%s?filename=%s";

    @Resource
    private CsceneService csceneService;

    @Resource
    private ResourceCenterConverter cSceneBoConverter;

    public ServiceResult<CsceneBO> importByYmlConfig(String ymlFilePath, String imageFilePath, String sceneFilePath) {
        // 解析 topo.yml
        YmlParseResult ymlParseResult = ResourceCenterYmlParser.parseTopoYml(ymlFilePath);
        // 解析 image/services.yml
        List<ImageParseResult> imageInfos = ResourceCenterYmlParser.parseImageServicesYml(ymlFilePath, imageFilePath);
        // 处理附件
        String fdfsSceneFilePath = null;
        if (StringUtils.isNotEmpty(sceneFilePath)) {
            if (FileUtil.exist(sceneFilePath)) {
                String fdfsPath = FdfsCommon.upload(sceneFilePath);
                String fileName = FileUtil.getName(sceneFilePath);
                fdfsSceneFilePath = String.format(FDFS_SCENE_FILEPATH_FORMAT, fdfsPath, fileName);
            }
        }
        // 转换bo
        CsceneBO csceneBO = cSceneBoConverter.convert(ymlParseResult, imageInfos, fdfsSceneFilePath);
        if (CollectionUtils.isNotEmpty(csceneBO.getSceneConfig().getTopology().getElements())) {
            csceneBO.getSceneConfig().getTopology().getElements().forEach(e -> {
                if (CollectionUtils.isNotEmpty(e.getPorts())) {
                    for (int i = 0; i < e.getPorts().size(); i++) {
                        e.getPorts().get(i).setName("port" + i);
                    }
                }
            });
        }
        // 保存
        return csceneService.syncWithoutCheckLimit(csceneBO, new CsceneContextBO());
    }
}
