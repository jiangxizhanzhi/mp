package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.sceneimport.convert.ResourceCenterConverter;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.ResourceCenterYmlParser;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class ResourceCenterService {
    private static String FDFS_SCENE_FILEPATH_FORMAT = "%s?filename=%s";
    private static String MOUNT_CMD = "mount -t nfs -o soft -o nolock %s:%s %s";
    private static String UNMOUNT_CMD = "umount %s";

    @Resource
    private CsceneService csceneService;

    @Resource
    private ResourceCenterConverter cSceneBoConverter;

    public ServiceResult<CsceneBO> importByYmlConfig(String ymlFilePath, String imageFilePath, List<String> imageFilePathList, String sceneFilePath) {
        // 解析 topo.yml
        YmlParseResult ymlParseResult = ResourceCenterYmlParser.parseTopoYml(ymlFilePath);
        // 解析 image/services.yml
        List<ImageParseResult> imageInfos = null;
        if (StringUtils.isNotEmpty(imageFilePath)) {
            imageInfos = ResourceCenterYmlParser.parseImageServicesYml(ymlFilePath, imageFilePath);
        } else {
            imageInfos = ResourceCenterYmlParser.parseImageServicesYml(ymlFilePath, imageFilePathList);
        }
        // 处理附件
        String fdfsSceneFilePath = null;
        if (StringUtils.isNotEmpty(sceneFilePath)) {
            if (FileUtil.exist(sceneFilePath)) {
                String fdfsPath = FsCommon.upload(sceneFilePath);
                String fileName = FileUtil.getName(sceneFilePath);
                fdfsSceneFilePath = String.format(FDFS_SCENE_FILEPATH_FORMAT, fdfsPath, fileName);
            } else {
                log.info("import scene , no scene file for path {}", sceneFilePath);
            }
        }
        // 转换bo
        CsceneBO csceneBO = cSceneBoConverter.convert(ymlParseResult, imageInfos, fdfsSceneFilePath);
        if (CollectionUtils.isNotEmpty(csceneBO.getSceneConfig().getTopology().getElements())) {
            csceneBO.getSceneConfig().getTopology().getElements().forEach(e -> {
                if (CollectionUtils.isNotEmpty(e.getPorts())) {
                    String prefix = (e.getType() != null && e.getType() == BaseSceneElementModel.Type.TERMINAL) ? "eth0" : "E0";
                    for (int i = 0; i < e.getPorts().size(); i++) {
                        e.getPorts().get(i).setName(prefix + "/" + (i + 1));
                    }
                }
            });
        }
        // 保存
        return csceneService.syncWithoutCheckLimit(csceneBO, new CsceneContextBO());
    }

    public ServiceResult<Boolean> mount(String host, String path) {
        this.execShell(String.format(MOUNT_CMD, host, path, path));
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> unmount(String path) {
        this.execShell(String.format(UNMOUNT_CMD, path));
        return new ServiceResult<>(true);
    }

    private void execShell(String shell) {
        log.info("Start exec shell! command=[{}]", shell);
        try {
            Process process = Runtime.getRuntime().exec(shell);
        } catch (Exception e) {
            log.error("Exec shell error! command=[{}], catch error:", shell, e);
        }
        log.info("End exec shell!");
    }
}
