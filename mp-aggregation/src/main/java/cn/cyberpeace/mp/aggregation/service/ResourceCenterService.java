package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.sceneimport.convert.ResourceCenterConverter;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.ResourceCenterYmlParser;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.ExportResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.SceneExporter;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.converter.TargetConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
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

/**
 * @ClassName ResourceCenterService
 * @Description
 * @Author maojun
 * @Date 2021/10/11
 * @Version 1.0.0
 */
@Slf4j
@Service
public class ResourceCenterService {
    private static String FDFS_SCENE_FILEPATH_FORMAT = "%s?filename=%s";

    private static String LOGO_FILEPATH_FORMAT = "/api/mp/file/%s";

    private static String LOGO_FILE_SUFFIX = "/api/mp/file/";

    private static String LOGO_NAME = "Topology.png";


    private static String MOUNT_CMD = "mount -t nfs -o soft -o nolock -o nfsvers=3  %s:%s %s";
    private static String UMOUNT_CMD = "umount %s";

    @Resource
    private CsceneService csceneService;

    @Resource
    private ResourceCenterConverter cSceneBoConverter;

    @Resource
    private TargetConverter targetConverter;

    /**
     * importByConfig
     *
     * @param ymlFilePath
     * @param imageFilePath
     * @param imageFilePathList
     * @param sceneFilePath
     * @return
     */
    public ServiceResult<CsceneBO> importByConfig(String ymlFilePath, String imageFilePath, List<String> imageFilePathList, String sceneFilePath) {
        String jsonFilePath = ymlFilePath + "/topo.json";
        File file = new File(jsonFilePath);
        if (file.exists()) {
            return this.importByJsonConfig(ymlFilePath, imageFilePath, imageFilePathList, sceneFilePath);
        } else {
            return this.importByYmlConfig(ymlFilePath, imageFilePath, imageFilePathList, sceneFilePath);
        }
    }

    /**
     * importByJsonConfig
     *
     * @param ymlFilePath
     * @param imageFilePath
     * @param imageFilePathList
     * @param sceneFilePath
     * @return
     */
    public ServiceResult<CsceneBO> importByJsonConfig(String ymlFilePath, String imageFilePath, List<String> imageFilePathList, String sceneFilePath) {
        // 处理附件
        String fdfsSceneFilePath = null;
        if (StringUtils.isNotEmpty(sceneFilePath)) {
            File file = new File(sceneFilePath);
            if (file.exists()) {
                String fdfsPath = FsCommon.upload(sceneFilePath);
                String fileName = file.getName();
                fdfsSceneFilePath = String.format(FDFS_SCENE_FILEPATH_FORMAT, fdfsPath, fileName);
            } else {
                log.info("import scene , no scene file for path {}", sceneFilePath);
            }
        }
        String logoPath = ymlFilePath + "/" + LOGO_NAME;
        // 处理logo
        String fdfsLogoFilePath = null;
        File logo = new File(logoPath);
        if (logo.exists()) {
            String fdfsPath = FsCommon.upload(logoPath);
            fdfsLogoFilePath = String.format(LOGO_FILEPATH_FORMAT, fdfsPath);
        } else {
            log.info("import logo , no logo file for path {}", sceneFilePath);
        }
        // 转换bo
        File file = new File(ymlFilePath + "/topo.json");
        String content = null;
        try {
            content = FileUtils.readFileToString(file,"utf8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SceneConfigDataBO sceneConfigDataBO = JsonCommon.parse(content, SceneConfigDataBO.class);
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneConfigDataBO, null);
        CloudSceneConfigPluginHandler pluginHandler = sceneConfigHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        for (String nodeId : sceneConfigHandler.getElementMap().keySet()) {
            CloudPluginSceneConfigElementBO pluginElement = pluginHandler.getElement(nodeId);
            String imageName = pluginElement.getImage();
            Device device = this.getByImageName(imageName);
            if (device != null) {
                pluginElement.setImageUuid(device.getUuid());
            }
        }
        CsceneBO csceneBO = new CsceneBO();
        csceneBO.setSceneConfig(sceneConfigDataBO);
        csceneBO.setLogo(fdfsLogoFilePath);

        // 保存
        return csceneService.syncForImport(csceneBO, new CsceneContextBO());
    }

    /**
     * getByImageName
     *
     * @param imageName
     * @return 靶标信息
     */
    private Device getByImageName(String imageName){
        TargetSceneRequest request = new TargetSceneRequest();
        request.setName(imageName);
        request.setIgnoreUser(true);
        // 精确搜索
        request.setLikeName(false);
        //不过滤隐藏靶标
        request.setIsHide(Boolean.TRUE);
        BasePageBO<Device> targetSceneConvertModel = null;
        ImagePageDTO imagePageDTOParams = new ImagePageDTO(request);
        try {
            targetSceneConvertModel = this.targetConverter.allTarget(imagePageDTOParams);
        } catch (Exception e) {
            String detail = "导入场景失败,调用云平台所有靶标接口报错,输入参数:" + JsonCommon.stringify(imagePageDTOParams) + ",响应:" + e.getMessage();
            log.error(detail, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
        }

        if (targetSceneConvertModel != null && CollectionUtils.isNotEmpty(targetSceneConvertModel.getList())) {
            return targetSceneConvertModel.getList().get(0);
        } else {
            List<Device> devices = null;
            try {
                devices = this.targetConverter.getCommonTargets(request.getName());
            } catch (Exception e) {
                String detail = "导入场景失败,调用云平台常用靶标接口报错,输入参数:" + request.getName() + ",响应:" + e.getMessage();
                log.error(detail, e);
                throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
            }
            if (CollectionUtils.isNotEmpty(devices)) {
                return devices.get(0);
            } else {
                return null;
            }
        }
    }

    /**
     * importByYmlConfig
     *
     * @param ymlFilePath
     * @param imageFilePath
     * @param imageFilePathList
     * @param sceneFilePath
     * @return
     */
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
            File file = new File(sceneFilePath);
            if (file.exists()) {
                String fdfsPath = FsCommon.upload(sceneFilePath);
                String fileName = file.getName();
                fdfsSceneFilePath = String.format(FDFS_SCENE_FILEPATH_FORMAT, fdfsPath, fileName);
            } else {
                log.info("import scene , no scene file for path {}", sceneFilePath);
            }
        }
        String logoPath = ymlFilePath + "/" + LOGO_NAME;
        // 处理logo
        String fdfsLogoFilePath = null;
        File logo = new File(logoPath);
        if (logo.exists()) {
            String fdfsPath = FsCommon.upload(logoPath);
            fdfsLogoFilePath = String.format(LOGO_FILEPATH_FORMAT, fdfsPath);
        } else {
            log.info("import logo , no logo file for path {}", sceneFilePath);
        }
        // 转换bo
        CsceneBO csceneBO = cSceneBoConverter.convert(ymlParseResult, imageInfos, fdfsSceneFilePath);
        csceneBO.setLogo(fdfsLogoFilePath);
        // 保存
        return csceneService.syncForImport(csceneBO, new CsceneContextBO());
    }

    /**
     * exportScene
     *
     * @param csceneId
     * @param envPath
     * @param imagePath
     * @param singleImagePath
     * @param sceneNumber
     * @param disableImageExport
     * @return
     */
    public ServiceResult<ExportResult> exportScene(String csceneId, String envPath, String imagePath, String singleImagePath, String sceneNumber, boolean disableImageExport) {
        ObjServiceResult<CsceneModel> cscene = csceneService.get(csceneId);
        if (cscene == null || cscene.getSource() == null || cscene.getSource().getObj() == null) {
            String detail = "[" + csceneId + "]-场景不存在";
            log.error(detail);
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST, detail);
        }
        String logo = cscene.getSource().getObj().getLogo();
        //存在logo图片则导出
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

        ExportResult result = new SceneExporter().export(cscene.getSource().getObj().getSceneConfig(), envPath, imagePath, singleImagePath, sceneNumber, disableImageExport);
        return new ServiceResult<>(result);
    }

    /**
     * mount
     *
     * @param host
     * @param path
     * @return
     */
    public ServiceResult<Boolean> mount(String host, String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.execShell(String.format(MOUNT_CMD, host, path, path));
        return new ServiceResult<>(true);
    }

    /**
     * umount
     *
     * @param path
     * @return
     */
    public ServiceResult<Boolean> umount(String path) {
        this.execShell(String.format(UMOUNT_CMD, path));
        return new ServiceResult<>(true);
    }

    /**
     * execShell
     *
     * @param shell
     */
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
