package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.config.AggregationConfig;
import cn.cyberpeace.mp.aggregation.constant.AggregationSingleton;
import cn.cyberpeace.mp.aggregation.sceneimport.convert.ResourceCenterConverter;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.ResourceCenterYmlParser;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.ExportResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.SceneExporter;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.SceneParseResourceCenterV1;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.export.SceneParseResourceCenterV2;
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
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/11
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

    private static String DEVICE_QUERY_URL = "/v1/volumes/snapshots";

    @Value("${mp.scene.scene-export-limit-linux-size:8}")
    private Integer EXPORT_LINUX_LIMIT_SIZE;

    @Value("${mp.scene.scene-export-limit-win-size:20}")
    private Integer EXPORT_WIN_LIMIT_SIZE;

    @Resource
    private CsceneService csceneService;

    @Resource
    private ResourceCenterConverter cSceneBoConverter;

    @Autowired
    private AggregationConfig aggregationConfig;

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
        return csceneService.syncWithoutCheckLimit(csceneBO, new CsceneContextBO());
    }

    public ServiceResult<ExportResult> exportScene(String csceneId, String envPath, String imagePath, String singleImagePath, String sceneNumber, boolean disableImageExport) {
        var cscene = csceneService.get(csceneId);
        if (cscene == null || cscene.getSource() == null || cscene.getSource().getObj() == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        //获取场景id，根据场景id解析信息
        String sceneId = cscene.getSource().getObj().getSceneConfig();
        SceneParseResourceCenterV1.Result resultV1 = new SceneParseResourceCenterV1().parseAndLoadInfo(sceneId);
        SceneParseResourceCenterV2.Result resultV2 = new SceneParseResourceCenterV2().parseAndLoadInfo(sceneId);
        //场景导出限制开关，默认为开
        if (aggregationConfig.getExportSceneImageLimitEnable()) {
            //遍历每个设备进行大小检查
            for (var device : resultV1.getDevices()) {
                var params = new DeviceQueryParams();
                params.setName(device.getImage());
                CloudResponse<Map> result;
                try {
                    //调用接口查询镜像信息
                    result = new Remote().cloudGet(DEVICE_QUERY_URL, params, new TypeReference<CloudResponse<Map>>() {
                    }, true, AggregationSingleton.aggregationConfig.getExportImageTimeout());
                } catch (MpRuntimeException exception) {
                    exception.setServiceCode(CsceneErrorCode.SCENE_EXPORT_ERROR);
                    exception.setDetail(String.format("Failed to query information about device %s",params.getName()));
                    throw exception;
                }

                var data = result.getData();
                if (data.containsKey("data")) {
                    LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) data.get("data");
                    if (!linkedHashMap.containsKey("list")) {
                        throw new MpRuntimeException(CsceneErrorCode.SCENE_EXPORT_ERROR, "key 'list' is not exist.");
                    }
                    ArrayList<Object> arrayList = (ArrayList<Object>) linkedHashMap.get("list");

                    for (Object obj : arrayList) {
                        if (!(obj instanceof Map)) {
                            throw new MpRuntimeException(CsceneErrorCode.SCENE_EXPORT_ERROR, "The elements in ArrayList are not of Map type.");
                        }
                        LinkedHashMap<String, Object> item = (LinkedHashMap<String, Object>) obj;
                        if (item.containsKey("name") && item.get("name") != null &&
                                device.getImage().equals(item.get("name").toString()))
                            if (item.containsKey("size") && item.get("size") != null) {
                                Integer size = Integer.valueOf(item.get("size").toString());
                                Integer maxSize = 0;
                                //根据系统类型获取最大限制
                                if (SystemTypeCommandUtils.isLinux(device.getSystemType())) {
                                    maxSize = EXPORT_LINUX_LIMIT_SIZE;
                                } else {
                                    maxSize = EXPORT_WIN_LIMIT_SIZE;
                                }
                                if (size > maxSize) {
                                    throw new MpRuntimeException(CsceneErrorCode.SCENE_EXPORT_ERROR,
                                            String.format("scene image %s is too big, " +
                                                            "The export maximum size is %dGB, but its size is %dGB.",
                                                    device.getImage(), maxSize, size));
                                }
                            }
                    }
                } else {
                    //所有查不到的直接默认导出不判断大小
                }
            }
        }


        String logo = cscene.getSource().getObj().getLogo();
        //存在logo图片则导出
        if (StringUtils.isNotEmpty(logo)) {
            String logoPath = StringUtils.replaceOnce(logo, LOGO_FILE_SUFFIX, "");
            byte[] content = FsCommon.downloadRawContent(logoPath);
            try {
                FileUtils.writeByteArrayToFile(new File(envPath + "/" + LOGO_NAME), content);
            } catch (IOException e) {
                log.error("failed to write file ", ExceptionUtils.getStackTrace(e));
                throw new MpRuntimeException(BaseErrorCode.ERROR);
            }
        }

        var result = new SceneExporter().export(resultV1, resultV2,
                envPath, imagePath, singleImagePath, sceneNumber, disableImageExport);
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

    @Setter
    @Getter
    private class DeviceQueryParams implements CloudHttpParam {
        private String name;
    }
}
