package cn.cyberpeace.mp.aggregation.sceneimport.parse.export;

import cn.cyberpeace.mp.aggregation.constant.AggregationSingleton;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.InitCommandParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.AccessModeExportResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.YmlParseResultV2;
import cn.cyberpeace.mp.aggregation.service.DefaultImgService;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
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
import java.time.LocalDate;
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

    public ExportResult export(String sceneId, String envPath, String imagePath, String singleImagePath, String sceneNumber, boolean disableImageExport) {
        // 生成topo.yml
        SceneParseResourceCenterV1.Result resultV1 = new SceneParseResourceCenterV1().parseAndLoadInfo(sceneId);
        if (CollectionUtils.isNotEmpty(resultV1.getYmlResult().getNodes())) {
            resultV1.getYmlResult().getNodes().forEach(n -> {
                if (StringUtils.isNotEmpty(n.getImage())) {
                    n.setImage(sceneNumber + n.getImage());
                }
                if (StringUtils.isNotEmpty(n.getImages())) {
                    n.setImages(sceneNumber + n.getImages());
                }
            });
        }
        this.createSceneYml(resultV1.getYmlResult(), envPath, "topo");

        SceneParseResourceCenterV2.Result parseResult = new SceneParseResourceCenterV2().parseAndLoadInfo(sceneId);
        if (CollectionUtils.isNotEmpty(parseResult.getYmlResult().getNodes())) {
            parseResult.getYmlResult().getNodes().forEach(n -> {
                if (StringUtils.isNotEmpty(n.getImage())) {
                    n.setImage(sceneNumber + n.getImage());
                }
                if (StringUtils.isNotEmpty(n.getImages())) {
                    n.setImages(sceneNumber + n.getImages());
                }
            });
        }
        this.createSceneYml(parseResult.getYmlResult(), envPath, "topo2");

        // 下载附件
        this.downloadNodeFile(envPath, resultV1.getFile2Download());
        // 生成server.yml与导出靶标
        if (!disableImageExport) {
            this.createImageAndServiceYml(imagePath, singleImagePath, resultV1.getDevices(),sceneNumber);
        }
        ExportResult exportResult = new ExportResult();
        List<ExportResult.ConvertInfo> convertInfos = new ArrayList<>();
        parseResult.getIdConvert().forEach((k, v) -> {
            ExportResult.ConvertInfo convertInfo = new ExportResult.ConvertInfo();
            convertInfo.setId(v);
            convertInfo.setOriginId(k);
            convertInfos.add(convertInfo);
        });
        exportResult.setConvertInfos(convertInfos);
        return exportResult;
    }

    private void createImageAndServiceYml(String imagePath, String singleImagePath, List<SceneParseResourceCenterV2.Device> devices,String sceneNumber) {
        if (CollectionUtils.isNotEmpty(devices)) {
            for (var device : devices) {
                String currentImageDir = null;
                if (StringUtils.isNotEmpty(singleImagePath)) {
                    currentImageDir = singleImagePath;
                } else {
                    currentImageDir = imagePath + "/" + (sceneNumber + device.getImage());
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
                }, true, AggregationSingleton.aggregationConfig.getExportImageTimeout());
                var imageMap = result.getData();
                if (imageMap.containsKey("Cloud-init")) {
                    imageMap.put("cloudInit", imageMap.get("Cloud-init"));
                }
                var image = JsonCommon.mapToBean(imageMap, Image.class);
                if (device.getCpu() != null && device.getRam() != null && device.getDisk() != null) {
                    String flavor = String.format("%sc-%sg-%sg", device.getCpu(), device.getRam() / 1024, device.getDisk());
                    image.set实例大小(formatFlavor(flavor));
                }

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
                    List<AccessModeExportResult> normal = new ArrayList<>();
                    List<AccessModeExportResult> manage = new ArrayList<>();
                    for (var m : device.getAccessModes()) {
                        AccessModeExportResult accessModeParseResult = new AccessModeExportResult();
                        accessModeParseResult.setDesc(m.getDesc());
                        if (StringUtils.isNotEmpty(m.getPath())) {
                            accessModeParseResult.setPath(m.getPath());
                        }
                        accessModeParseResult.setProtocol(m.getProtocol());
                        accessModeParseResult.setPrivateKey(m.getPrivateKeyFileName());
                        //下载密钥文件
                        downloadPrivateKeyFile(m.getPrivateKeyPath(), currentImageDir, m.getPrivateKeyFileName());
                        if (m.getPort() != null) {
                            accessModeParseResult.setPort(m.getPort());
                        }
                        if (Boolean.TRUE.equals(m.getProxy())) {
                            accessModeParseResult.setIsproxy(true);
                        } else {
                            accessModeParseResult.setIsproxy(false);
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

                // 设置发布时间
                if (StringUtils.isEmpty(image.get发布时间())) {
                    String time = TimeUtils.localDateToString(LocalDate.now());
                    time = StringUtils.replace(time, "-", "timeFormatSplit");
                    image.set发布时间(time);
                }
                if (StringUtils.isEmpty(image.get系统服务())) {
                    image.set系统服务(Boolean.TRUE.equals(image.getCloudInit()) ? "CloudInit" : "hasNoSystemServer");
                }
                if (StringUtils.isEmpty(image.get安装软件())) {
                    image.set安装软件("");
                }
                if (StringUtils.isEmpty(image.get授权类型())) {
                    image.set授权类型("无需授权");
                }

                ServerYml serverYml = new ServerYml();
                Server server = new Server();
                server.setMpYmlImageNameToChange(image);
                serverYml.setServers(server);

                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(new SceneRepresenter(), dumperOptions);
                String content = yaml.dumpAs(serverYml, Tag.MAP, null);
                content = StringUtils.replaceOnce(content, "mpYmlImageNameToChange", sceneNumber+device.getImage());
                content = StringUtils.replaceOnce(content, "cloudInit", "Cloud-init");
                content = StringUtils.replace(content, "privateKey", "private-key");
                content = StringUtils.replace(content, "hasNoSystemServer", "");
                content = StringUtils.replace(content, "timeFormatSplit", "-");
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
            log.error("failed to write file:{} ", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
    }

    private void createFile(String content, String filePath) {
        try {
            FileUtils.write(new File(filePath), content, "utf8");
        } catch (Exception e) {
            log.error("failed to write yml file , yml content is {}, {}", content, ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
    }

    private void downloadNodeFile(String envPath, Map<String, String> file2Download) {
        for (var entry : file2Download.entrySet()) {
            // 下载附件
            String fdfsFilePath = entry.getValue();
            if (StringUtils.contains(entry.getValue(), "?")) {
                fdfsFilePath = StringUtils.split(entry.getValue(), "?")[0];
            }
            byte[] content = FdfsCommon.downloadRawContent(fdfsFilePath);
            try {
                FileUtils.writeByteArrayToFile(new File(envPath + "/" + entry.getKey()), content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadPrivateKeyFile(String fdfsFilePath, String imagePath, String fileName) {
        if (StringUtils.isNotEmpty(fdfsFilePath) && StringUtils.isNotEmpty(imagePath)) {
            byte[] content = FdfsCommon.downloadRawContent(fdfsFilePath);
            try {
                FileUtils.writeByteArrayToFile(new File(imagePath + "/" + fileName), content);
            } catch (IOException e) {
                log.error("Download the private key file error:{}", e.getMessage());
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
        result.put("staticRoutes", "static-routes");
        result.put("sourceIp", "source-iP");
        result.put("sourcePort", "source-port");
        result.put("targetIP", "target-iP");
        result.put("targetPort", "target-port");
        result.put("virtualPlatform", "virtual-platform");
        result.put(BaseSceneParseResourceCenter.SPECIAL_STR_OFF, "off");
        result.put(BaseSceneParseResourceCenter.SPECIAL_STR_ON, "on");
        result.put(BaseSceneParseResourceCenter.SPECIAL_STR_EMPTY, "");
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
        private List<AccessModeExportResult> 接入方式;
        private List<AccessModeExportResult> 管理接入;
    }

    private static String formatFlavor(String flavor) {
        String[] formatStrs = new String[]{
                "m2.1c-1g-10g", "m2.2c-2g-10g", "m3.1c-2g-20g", "m3.2c-4g-20g",
                "m3.4c-4g-20g", "m4.2c-2g-40g", "m4.2c-4g-40g", "m4.4c-4g-40g",
                "m5.4c-4g-80g", "m6.4c-4g-120g", "m4.1c-1g-40g", "m4.2c-2g-60g",
                "m4.4c-8g-40g", "m3.2c-4g-50g", "m5.4c-8g-60g", "m3.1c-2g-50g",
                "m5.4c-8g-80g", "m3.1c-1g-20g", "m4.2c-4g-60g", "m3.4c-4g-50g"
        };
        for (String f : formatStrs) {
            if (f.endsWith(flavor)) {
                return f;
            }
        }
        return "mx." + flavor;
    }
}