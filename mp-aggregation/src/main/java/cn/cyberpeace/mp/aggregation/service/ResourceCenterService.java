package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.sceneimport.convert.ResourceCenterConverter;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.ResourceCenterYmlParser;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.ExportResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.SceneExporter;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;


@Slf4j
@Service
public class ResourceCenterService {
    private static String FDFS_SCENE_FILEPATH_FORMAT = "%s?filename=%s";
    private static String MOUNT_CMD = "mount -t nfs -o soft -o nolock %s:%s %s";
    private static String UMOUNT_CMD = "umount %s";

    private static String LOGO_FILEPATH_FORMAT = "/api/mp/file/%s";

    private static String LOGO_FILE_SUFFIX = "/api/mp/file/";

    private static String LOGO_NAME = "Topology.png";

    @Resource
    private CsceneService csceneService;

    @Resource
    private ResourceCenterConverter cSceneBoConverter;

    public ServiceResult<CsceneBO> importByYmlConfig(String ymlFilePath, String imageFilePath, List<String> imageFilePathList, String sceneFilePath) {
        
        YmlParseResult ymlParseResult = ResourceCenterYmlParser.parseTopoYml(ymlFilePath);
        
        List<ImageParseResult> imageInfos = null;
        if (StringUtils.isNotEmpty(imageFilePath)) {
            imageInfos = ResourceCenterYmlParser.parseImageServicesYml(ymlFilePath, imageFilePath);
        } else {
            imageInfos = ResourceCenterYmlParser.parseImageServicesYml(ymlFilePath, imageFilePathList);
        }
        
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
        String logoPath = ymlFilePath + "/" + LOGO_NAME;
        
        String fdfsLogoFilePath = null;
        File logo = new File(logoPath);
        if (logo.exists()) {
            String fdfsPath = FsCommon.upload(logoPath);
            fdfsLogoFilePath = String.format(LOGO_FILEPATH_FORMAT, fdfsPath);
        } else {
            log.info("import logo , no logo file for path {}", sceneFilePath);
        }
        
        CsceneBO csceneBO = cSceneBoConverter.convert(ymlParseResult, imageInfos, fdfsSceneFilePath);
        csceneBO.setLogo(fdfsLogoFilePath);
        
        return csceneService.syncWithoutCheckLimit(csceneBO, new CsceneContextBO());
    }

    public ServiceResult<ExportResult> exportScene(String csceneId, String envPath, String imagePath, String singleImagePath, String sceneNumber, boolean disableImageExport) {
        var cscene = csceneService.get(csceneId);
        if (cscene == null || cscene.getSource() == null || cscene.getSource().getObj() == null) {
            String detail = "[" + csceneId + "]-场景不存在";
            log.error(detail);
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST, detail);
        }
        String logo = cscene.getSource().getObj().getLogo();
        
        if (StringUtils.isNotEmpty(logo)) {
            String logoPath = StringUtils.replaceOnce(logo, LOGO_FILE_SUFFIX, "");
            try {
                byte[] content = FsCommon.downloadRawContent(logoPath);
                FileUtils.writeByteArrayToFile(new File(envPath + "/" + LOGO_NAME), content);
            } catch (Exception e) {
                String detail = "[" + logoPath + "]-场景logo导出失败," + e.getMessage();
                log.error(detail, e);
                throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
            }
        }
        var result = new SceneExporter().export(cscene.getSource().getObj().getSceneConfig(), envPath, imagePath, singleImagePath,sceneNumber,disableImageExport);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> mount(String host, String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.execShell(String.format(MOUNT_CMD, host, path, path));
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> umount(String path) {
        this.execShell(String.format(UMOUNT_CMD, path));
        return new ServiceResult<>(true);
    }

    private void execShell(String shell) {
        log.info("Start exec shell! command=[{}]", shell);
        try {
            Process process = Runtime.getRuntime().exec(shell);
        } catch (Exception e) {
            log.error("Exec shell error! command=[{}], catch error:{}", shell, ExceptionUtils.getStackTrace(e));
        }
        log.info("End exec shell!");
    }
}
