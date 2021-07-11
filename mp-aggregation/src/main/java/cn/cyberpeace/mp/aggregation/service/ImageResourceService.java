package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.InitCommandParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.AccessModeParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.YmlParseResultV2;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


@Slf4j
@Service
public class ImageResourceService {
    @Resource
    private DeviceTargetMapper deviceTargetMapper;

    private static String EXPORT_DEVICE_YML = "/cpcloud/device/v1/image/export_device_yml";

    private static final String PNG = "logo.png";
    private static final String JPG = "logo.jpg";

    public Boolean exportImageByUuid(String uuid, String imagePath) {
        if (StringUtils.isNotEmpty(uuid)) {
            var imageInfo = deviceTargetMapper.findByUuid(uuid);
            if (ObjectUtil.isNull(imageInfo)) {
                log.error("failed to get image information by uuid:{}", uuid);
                throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
            }
            String currentImageDir = imagePath + "/" + imageInfo.getName();
            exportImage(imageInfo.getName(), imageInfo.getLogo(), currentImageDir, new ArrayList<AccessMode>());
            return true;
        } else {
            log.error("failed to get image information，uuid is {}", uuid);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
    }

    public void exportImage(String imageName, String logoPath, String imagePath, List<AccessMode> deviceAccessModes) {

        File file = new File(imagePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        
        var image = exportImage(imageName, imagePath);

        
        image.setLOGO(exportLogo(logoPath, imagePath));

        
        createServiceYml(imageName, imagePath, image, deviceAccessModes);
    }

    private Image exportImage(String imageName, String imagePath) {
        ExportImageParams params = new ExportImageParams();
        params.setImageName(imageName);
        params.setDestPath(imagePath);
        var result = new Remote().cloudGet(EXPORT_DEVICE_YML, params, new TypeReference<CloudResponse<Map>>() {
        }, true);
        var imageMap = result.getData();
        if (imageMap.containsKey("Cloud-init")) {
            imageMap.put("cloudInit", imageMap.get("Cloud-init"));
        }
        return JsonCommon.mapToBean(imageMap, Image.class);
    }

    private String exportLogo(String logoPath, String imagePath) {
        if (FsCommon.isFileServerOuterUrl(logoPath)) {
            String logoUrl = FsCommon.getFileKey(logoPath);
            byte[] content = FsCommon.downloadRawContent(logoUrl);
            
            if (this.isPng(content)) {
                this.createFile(content, imagePath + "/logo.png");
                return PNG;
            } else {
                this.createFile(content, imagePath + "/logo.jpg");
                return JPG;
            }
        } else if (logoPath.startsWith(AuthConstant.DEFAULT_IMAGE_PREFIX)) {
            String relativePath = StringUtils.replaceOnce(logoPath, AuthConstant.DEFAULT_IMAGE_PREFIX, "");
            String filePath = BeanCommon.getBean(DefaultImgService.class).getDefaultImgFilePath(relativePath);
            byte[] content = null;
            try {
                File logo = new File(filePath);
                content = FileUtils.readFileToByteArray(logo);
            } catch (Exception e) {
                log.error("failed to export logo {} , {}", logoPath, ExceptionUtils.getStackTrace(e));
                throw new MpRuntimeException(BaseErrorCode.ERROR);
            }
            if (this.isPng(content)) {
                this.createFile(content, imagePath + "/logo.png");
                return PNG;
            } else {
                this.createFile(content, imagePath + "/logo.jpg");
                return JPG;
            }
        } else if (StringUtils.isNotEmpty(logoPath)) {
            byte[] content = FsCommon.downloadRawContent(logoPath);
            
            if (this.isPng(content)) {
                this.createFile(content, imagePath + "/logo.png");
                return PNG;
            } else {
                this.createFile(content, imagePath + "/logo.jpg");
                return JPG;
            }
        } else {
            log.error("failed to export logo {}", logoPath);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
    }

    private void createServiceYml(String imageName, String imagePath, Image image, List<AccessMode> deviceAccessModes) {
        if (CollectionUtils.isNotEmpty(deviceAccessModes)) {
            List<AccessModeParseResult> normal = new ArrayList<>();
            List<AccessModeParseResult> manage = new ArrayList<>();
            for (var m : deviceAccessModes) {
                AccessModeParseResult accessModeParseResult = new AccessModeParseResult();
                accessModeParseResult.setDesc(m.getDesc());
                accessModeParseResult.setProtocol(m.getProtocol());
                if (m.getPort() != null) {
                    accessModeParseResult.setPort(String.valueOf(m.getPort()));
                }
                if (Boolean.TRUE.equals(m.getProxy())) {
                    accessModeParseResult.setIsproxy("true");
                }
                accessModeParseResult.setPassword(m.getPassword());
                accessModeParseResult.setUsername(m.getUsername());
                if (Boolean.TRUE.equals(m.getManageMode())) {
                    manage.add(accessModeParseResult);
                } else {
                    normal.add(accessModeParseResult);
                }
            }
            if (CollectionUtils.isNotEmpty(normal)) {
                image.set接入方式(normal);
            }
            if (CollectionUtils.isNotEmpty(manage)) {
                image.set管理接入(manage);
            }
        }
        ServerYml serverYml = new ServerYml();
        Server server = new Server();
        server.setMpYmlImageNameToChange(image);
        serverYml.setServers(server);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(new SceneRepresenter(), dumperOptions);
        String content = yaml.dumpAs(serverYml, Tag.MAP, null);
        content = StringUtils.replaceOnce(content, "mpYmlImageNameToChange", imageName);
        content = StringUtils.replaceOnce(content, "cloudInit", "Cloud-init");
        this.createFile(content, imagePath + "/servers.yml");
    }

    private static class SceneRepresenter extends Representer {

        private static Map<String, Integer> FIELD_ORDER = new HashMap<>();

        static {
            FIELD_ORDER.put("scene", 1);
            FIELD_ORDER.put("networks", 2);
            FIELD_ORDER.put("nodes", 3);
            FIELD_ORDER.put("shape", 4);
        }

        @Override
        protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
            Set<Property> filterProperties = new TreeSet<>();
            if (javaBean instanceof YmlParseResultV2) {
                filterProperties = new TreeSet<>((o1, o2) -> {
                    Integer x1 = FIELD_ORDER.get(o1.getName());
                    Integer x2 = FIELD_ORDER.get(o2.getName());
                    return x1 - x2;
                });
            }
            for (Property property : properties) {
                Object memberValue = property.get(javaBean);
                if (property.getType() == String.class) {
                    if (memberValue != null && !"null".equals(memberValue)) {
                        if (StringUtils.isNotEmpty(memberValue.toString())) {
                            filterProperties.add(property);
                        } else if (javaBean instanceof InitCommandParseResult) {
                            if ("".equals(memberValue)) {
                                filterProperties.add(property);
                            }
                        }
                    }
                } else if (property.getType().isAssignableFrom(List.class)) {
                    if (memberValue != null && CollectionUtils.isNotEmpty((List) memberValue)) {
                        filterProperties.add(property);
                    }
                } else {
                    filterProperties.add(property);
                }
            }
            return super.representJavaBean(filterProperties, javaBean);
        }
    }

    @Setter
    @Getter
    private static class ServerYml {
        private Server servers;
    }

    @Setter
    @Getter
    private static class Server {
        Image MpYmlImageNameToChange;
    }

    @Setter
    @Getter
    private class ExportImageParams implements CloudHttpParam {
        private String imageName;
        private String destPath;
    }

    @Getter
    @Setter
    private static class Image {
        private String 描述;
        private String 系统类型;
        private String 系统版本;
        private String 镜像分类;
        private String 系统语言;
        private String 系统架构;
        private String 实例大小;
        private String 镜像格式;
        private String 虚拟机文件;
        private String LOGO;
        private String 发布时间;
        private String 镜像来源;
        private String 系统服务;
        private String 安装软件;
        private String 授权类型;
        private String 备注;
        private String NAT到外网;
        private String 自定义命令;
        private String 角色;
        private String 虚拟类型;
        private Boolean cloudInit;
        private List<AccessModeParseResult> 接入方式;
        private List<AccessModeParseResult> 管理接入;
    }

    private boolean isPng(byte[] content) {
        if (content == null || content.length < 8) {
            return false;
        }
        int[] pngHeaders = new int[]{ 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };
        for (int i = 0; i < pngHeaders.length; i++) {
            if (Byte.toUnsignedInt(content[i]) != pngHeaders[i]) {
                return false;
            }
        }
        return true;
    }

    private void createFile(byte[] content, String filePath) {
        try {
            FileUtils.writeByteArrayToFile(new File(filePath), content);
        } catch (Exception e) {
            log.error("failed to write file ", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
    }

    private void createFile(String content, String filePath) {
        try {
            FileUtils.write(new File(filePath), content, "utf8");
        } catch (Exception e) {
            log.error("failed to write yml file , yml content is {}", content, ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
    }

}
