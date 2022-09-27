package cn.cyberpeace.mp.aggregation.sceneimport.parse.export;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.InitCommandParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.AccessModeParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.YmlParseResultV2;
import cn.cyberpeace.mp.aggregation.service.DefaultImgService;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @Description
 * @Author maojun
 * @Date 2022/8/15
 */
@Slf4j
public class SceneExporter {

    private static String EXPORT_DEVICE_YML = "/cpcloud/device/v1/image/export_device_yml";

    public ExportResult export(String sceneId, String envPath, String imagePath, String singleImagePath) {
        // 生成topo.yml
        SceneParseResourceCenterV1.Result resultV1 = new SceneParseResourceCenterV1().parseAndLoadInfo(sceneId);
        this.createSceneYml(resultV1.getYmlResult(), envPath, "topo");

        SceneParseResourceCenterV2.Result parseResult = new SceneParseResourceCenterV2().parseAndLoadInfo(sceneId);
        this.createSceneYml(parseResult.getYmlResult(), envPath, "topo2");

        // 下载附件
        this.downloadNodeFile(envPath, resultV1.getFile2Download());
        // 生成server.yml与导出靶标
        this.createImageAndServiceYml(imagePath, singleImagePath, resultV1.getDevices());
        ExportResult exportResult = new ExportResult();
        List<ExportResult.ConvertInfo> convertInfos = new ArrayList<>();
        resultV1.getIdConvert().forEach((k, v) -> {
            ExportResult.ConvertInfo convertInfo = new ExportResult.ConvertInfo();
            convertInfo.setId(v);
            convertInfo.setOriginId(k);
            convertInfos.add(convertInfo);
        });
        exportResult.setConvertInfos(convertInfos);
        return exportResult;
    }

    private void createImageAndServiceYml(String imagePath, String singleImagePath, List<SceneParseResourceCenterV2.Device> devices) {
        if (CollectionUtils.isNotEmpty(devices)) {
            for (var device : devices) {
                String currentImageDir = null;
                if (StringUtils.isNotEmpty(singleImagePath)) {
                    currentImageDir = singleImagePath;
                } else {
                    currentImageDir = imagePath + "/" + device.getImage();
                }
                File file = new File(currentImageDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
                // 生成镜像
                ExportDeviceYmlParams params = new ExportDeviceYmlParams();
                params.setImageName(device.getImage());
                params.setDestPath(currentImageDir);
                var result = new Remote().cloudGet(EXPORT_DEVICE_YML, params, new TypeReference<CloudResponse<Map>>() {
                }, true);
                var imageMap = result.getData();
                if (imageMap.containsKey("Cloud-init")) {
                    imageMap.put("cloudInit", imageMap.get("Cloud-init"));
                }
                var image = JsonCommon.mapToBean(imageMap, Image.class);

                // 下载图片
                if (device.getLogo().startsWith("/fdfs") || device.getLogo().startsWith("group/")) {
                    String logoUrl = StringUtils.replaceOnce(device.getLogo(), "/fdfs/", "");
                    byte[] content = FdfsCommon.downloadRawContent(logoUrl);
                    // 判断图片文件类型
                    if (this.isPng(content)) {
                        this.createFile(content, currentImageDir + "/logo.png");
                        image.setLOGO("logo.png");
                    } else {
                        this.createFile(content, currentImageDir + "/logo.jpg");
                        image.setLOGO("logo.jpg");
                    }
                } else if (device.getLogo().startsWith(AuthConstant.DEFAULT_IMAGE_PREFIX)) {
                    String relativePath = StringUtils.replaceOnce(device.getLogo(), AuthConstant.DEFAULT_IMAGE_PREFIX, "");
                    String filePath = BeanCommon.getBean(DefaultImgService.class).getDefaultImgFilePath(relativePath);
                    byte[] content = null;
                    try {
                        File logo = new File(filePath);
                        content = FileUtils.readFileToByteArray(logo);
                    } catch (Exception e) {
                        log.error("failed to export logo {} , {}", device.getLogo(), ExceptionUtils.getStackTrace(e));
                        throw new MpRuntimeException(BaseErrorCode.ERROR);
                    }
                    if (this.isPng(content)) {
                        this.createFile(content, currentImageDir + "/logo.png");
                        image.setLOGO("logo.png");
                    } else {
                        this.createFile(content, currentImageDir + "/logo.jpg");
                        image.setLOGO("logo.jpg");
                    }
                }
                // 生成service.yml
                if (CollectionUtils.isNotEmpty(device.getAccessModes())) {
                    List<AccessModeParseResult> normal = new ArrayList<>();
                    List<AccessModeParseResult> manage = new ArrayList<>();
                    for (var m : device.getAccessModes()) {
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
                content = StringUtils.replaceOnce(content, "mpYmlImageNameToChange", device.getImage());
                content = StringUtils.replaceOnce(content, "cloudInit", "Cloud-init");
                this.createFile(content, currentImageDir + "/servers.yml");

                if (StringUtils.isNotEmpty(singleImagePath)) {
                    break;
                }
            }
        }
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

    private void downloadNodeFile(String envPath, Map<String, String> file2Download) {
        for (var entry : file2Download.entrySet()) {
            // 下载附件
            byte[] content = FdfsCommon.downloadRawContent(entry.getValue());
            try {
                FileUtils.writeByteArrayToFile(new File(envPath + "/" + entry.getKey()), content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createSceneYml(YmlParseResultV2 ymlParseResult, String envPath, String name) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(new SceneRepresenter(), dumperOptions);
        String content = yaml.dumpAs(ymlParseResult, Tag.MAP, null);
        for (Map.Entry<String, String> e : specialYmlFields().entrySet()) {
            content = StringUtils.replace(content, e.getKey(), e.getValue());
        }
        try {
            FileUtils.write(new File(envPath + "/" + name + ".yml"), content, "utf8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> specialYmlFields() {
        Map<String, String> result = new HashMap<>();
        result.put("initCommands", "init_commands");
        result.put("dhcpPool", "dhcp-pool");
        result.put("staticRouters", "static-routers");
        result.put("staticRoutes", "static-routers");
        result.put("sourceIp", "source-iP");
        result.put("sourcePort", "source-port");
        result.put("targetIP", "target-iP");
        result.put("targetPort", "target-port");
        result.put("virtualPlatform", "virtual-platform");
        return result;
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
    private class ExportDeviceYmlParams implements CloudHttpParam {
        private String imageName;
        private String destPath;
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
}