package cn.cyberpeace.mp.aggregation.sceneimport.parse.export;

import cn.cyberpeace.mp.aggregation.constant.AggregationSingleton;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.ExtNodeInfo;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.InitCommandParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.StaticRoutesResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.AccessModeExportResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.YmlParseResultV2;
import cn.cyberpeace.mp.aggregation.service.DefaultImgService;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigPieceBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
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
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.UnsupportedEncodingException;
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
            for (NodeElementParseResult n : resultV1.getYmlResult().getNodes()) {
                if (StringUtils.isNotEmpty(n.getImage())) {
                    n.setImage(sceneNumber + n.getImage());
                }
                if (StringUtils.isNotEmpty(n.getImages())) {
                    n.setImages(sceneNumber + n.getImages());
                }
            }
        }
        this.createSceneYml(resultV1.getYmlResult(), envPath, "topo");

        SceneParseResourceCenterV2.Result parseResult = new SceneParseResourceCenterV2().parseAndLoadInfo(sceneId);
        if (CollectionUtils.isNotEmpty(parseResult.getYmlResult().getNodes())) {
            for (NodeElementParseResult n : parseResult.getYmlResult().getNodes()) {
                if (StringUtils.isNotEmpty(n.getImage())) {
                    n.setImage(sceneNumber + n.getImage());
                }
                if (StringUtils.isNotEmpty(n.getImages())) {
                    n.setImages(sceneNumber + n.getImages());
                }
                if (CollectionUtils.isNotEmpty(n.getStaticRouters())) {
                    for (StaticRoutesResult sr : n.getStaticRouters()) {
                        if (StringUtils.isNotEmpty(sr.getNexthopNode())) {
                            sr.setNexthopNode(parseResult.getIdConvert().get(sr.getNexthopNode()));
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(n.getStaticRoutes())) {
                    for (StaticRoutesResult sr : n.getStaticRoutes()) {
                        if (StringUtils.isNotEmpty(sr.getNexthopNode())) {
                            sr.setNexthopNode(parseResult.getIdConvert().get(sr.getNexthopNode()));
                        }
                    }
                }
                if (n.getExtInfo() != null) {
                    ExtNodeInfo extNodeInfo = n.getExtInfo();
                    if (CollectionUtils.isNotEmpty(extNodeInfo.getPortProtocols())) {
                        for (ExtNodeInfo.PortProtocolExportInfo p : extNodeInfo.getPortProtocols()) {
                            p.setConnectNode(parseResult.getIdConvert().get(p.getConnectNode()));
                        }
                    }
                }
            }
        }
        this.createSceneYml(parseResult.getYmlResult(), envPath, "topo2");

        // 下载附件
        this.downloadNodeFile(envPath, resultV1.getFile2Download());
        // 生成server.yml与导出靶标
        if (!disableImageExport) {
            this.createImageAndServiceYml(imagePath, singleImagePath, resultV1.getDevices(), sceneNumber);
        }
        ExportResult exportResult = new ExportResult();
        List<ExportResult.ConvertInfo> convertInfos = new ArrayList<>();
        for (Map.Entry<String, String> entry : parseResult.getIdConvert().entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            ExportResult.ConvertInfo convertInfo = new ExportResult.ConvertInfo();
            convertInfo.setId(k);
            convertInfo.setOriginId(k);
            convertInfos.add(convertInfo);
        }
        exportResult.setConvertInfos(convertInfos);

        // 生成json格式的tuop导入资源
        var sceneConfigHandler = this.createTopoJson(sceneId, sceneNumber, envPath + "/topo.json");
        // 生成各个节点的图片和附件
        this.createElementFiles(sceneConfigHandler, envPath);

        return exportResult;
    }

    private SceneConfigHandler createTopoJson(String sceneId,String sceneNum, String filePath) {
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, null, null);
        this.removeEid(sceneConfigHandler);
        SceneConfigDataBO sceneConfigDataBO = sceneConfigHandler.toData();
        sceneConfigDataBO.getPlugins().remove("cpflow");
        sceneConfigDataBO.getPlugins().remove("collection");
        var cloudData = sceneConfigDataBO.getPlugins().get("cpcloud");
        for (Map<String, Object> cloudElement : cloudData.getElements().values()) {
            var imageType = cloudElement.get("image_type");
            if (StringUtils.isEmpty(sceneNum)) {
                continue;
            }
            boolean isContainer = BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(imageType);
            boolean isVm = BaseCloudPluginSceneElementModel.ImageType.VM.equals(imageType);
            if (isVm || isContainer) {
                Object image = cloudElement.get("image");
                if (image != null) {
                    cloudElement.put("image", sceneNum + image);
                }
            }
        }
        String content = JsonCommon.stringify(sceneConfigDataBO);
        try {
            this.createFile(content.getBytes("utf8"), filePath);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return sceneConfigHandler;
    }

    private void createElementFiles(SceneConfigHandler sceneConfigHandler, String envPath) {
        for (var e : sceneConfigHandler.getElementMap().values()) {
            String nodeId = e.getId();
            if (StringUtils.isNotEmpty(e.getIcon2())) {
                var p = new ParseFileKey(e.getIcon2(), "png");
                byte[] content = FsCommon.downloadRawContent(p.getFilePath());
                writeFile(content, envPath + "/" + nodeId + "/icon2." + p.getSuffix(), 3);
            }
            String icon3 = e.getIcon3();
            if (StringUtils.isNotEmpty(icon3)) {
                var p = new ParseFileKey(e.getIcon3(), "png");
                byte[] content = FsCommon.downloadRawContent(p.getFilePath());
                writeFile(content, envPath + "/" + nodeId + "/icon3." + p.getSuffix(), 3);
            }
            String file = e.getFile();
            if (StringUtils.isNotEmpty(file)) {
                var p = new ParseFileKey(e.getFile(), "zip");
                byte[] content = FsCommon.downloadRawContent(p.getFilePath());
                writeFile(content, envPath + "/" + nodeId + "/attachment." + p.getSuffix(), 3);
            }
        }
    }

    private String getFilePath(String input) {
        int queryIndex = input.indexOf('?');
        String pathPart = (queryIndex != -1) ? input.substring(0, queryIndex) : input;
        return pathPart;
    }

    @Getter
    private static class ParseFileKey {
        private String filePath;
        private String fileName;
        private String suffix;

        public ParseFileKey(String input, String defaultSuffix) {
            int queryIndex = input.indexOf('?');
            this.filePath = (queryIndex != -1) ? input.substring(0, queryIndex) : input;
            this.suffix = FileUtil.getSuffix(this.filePath);
            this.fileName = FileUtil.getName(this.filePath);
            if (StringUtils.isEmpty(this.suffix)) {
                this.suffix = defaultSuffix;
            }
        }
    }

    private void writeFile(byte[] content, String filePath, int tryTimes) {
        Exception throwE = null;
        for (int i = 0; i < tryTimes; i++) {
            try {
                FileUtils.writeByteArrayToFile(new File(filePath), content);
                return;
            } catch (Exception e) {
                throwE = e;
            }
        }
        if (throwE != null) {
            throw new MpRuntimeException(throwE.getMessage());
        }
    }

    private SceneConfigHandler removeEid(SceneConfigHandler sceneConfigHandler){
        sceneConfigHandler.getScene().setEid(null);
        for (Map.Entry<String, SceneConfigElementBO> entry : sceneConfigHandler.getElementMap().entrySet()) {
            SceneConfigElementBO value = entry.getValue();
            value.setEid(null);
            value.getPorts().forEach(p -> p.setEid(null));
        }
        for (Map.Entry<String, SceneConfigAreaBO> entry : sceneConfigHandler.getAreaMap().entrySet()) {
            SceneConfigAreaBO value = entry.getValue();
            value.setEid(null);
        }
        for (Map.Entry<String, SceneConfigPieceBO> entry : sceneConfigHandler.getPieceMap().entrySet()) {
            SceneConfigPieceBO value = entry.getValue();
            value.setEid(null);
        }
        for (Map.Entry<String, SceneConfigElementPortConnectionBO> entry : sceneConfigHandler.getElementPortConnectionMap()
                .entrySet()) {
            SceneConfigElementPortConnectionBO v = entry.getValue();
            v.setEid(null);
        }
        return sceneConfigHandler;
    }

    private void createImageAndServiceYml(String imagePath, String singleImagePath, List<SceneParseResourceCenterV2.Device> devices, String sceneNumber) {
        if (CollectionUtils.isNotEmpty(devices)) {
            for (SceneParseResourceCenterV2.Device device : devices) {
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
                CloudResponse<Map> result = null ;
                try {
                    result = new Remote().cloudGet(EXPORT_DEVICE_YML, params, new TypeReference<CloudResponse<Map>>() {
                    }, true, AggregationSingleton.aggregationConfig.getExportImageTimeout());
                } catch (MpRuntimeException e) {
                    String detail = "导出镜像接口报错 , 参数为: " + JsonCommon.stringify(params) + ",结果为: " + e.getMessage();
                    log.error(detail, e);
                    e.setDetail(detail);
                    throw e;
                }
                Map imageMap = result.getData();
                if (imageMap.containsKey("Cloud-init")) {
                    imageMap.put("cloudInit", imageMap.get("Cloud-init"));
                }
                Image image = JsonCommon.mapToBean(imageMap, Image.class);
                if (device.getCpu() != null && device.getRam() != null && device.getDisk() != null) {
                    String flavor = String.format("%sc-%sg-%sg", device.getCpu(), device.getRam() / 1024, device.getDisk());
                    image.set实例大小(formatFlavor(flavor));
                }

                // 下载图片
                if (device.getLogo().startsWith("/fdfs") || device.getLogo().startsWith("group/") || device.getLogo().startsWith("group1/")) {
                    String logoUrl = StringUtils.replaceOnce(device.getLogo(), "/fdfs/", "");
                    byte[] content = null;
                    try {
                        content = FsCommon.downloadRawContent(logoUrl);
                    } catch (Exception e) {
                        String detail = "导出场景出错 , 下载图片失败 ,文件地址:" + logoUrl;
                        log.error(detail, e);
                        throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
                    }
                    // 判断图片文件类型
                    if (this.isPng(content)) {
                        this.createFile(content, currentImageDir + "/logo.png");
                        image.setLOGO("logo.png");
                    } else {
                        this.createFile(content, currentImageDir + "/logo.jpg");
                        image.setLOGO("logo.jpg");
                    }
                } else if (device.getLogo().startsWith(AuthConstant.DEFAULT_IMAGE_PREFIX) && !device.getLogo().startsWith("/api/mp/file")) {
                    String relativePath = StringUtils.replaceOnce(device.getLogo(), AuthConstant.DEFAULT_IMAGE_PREFIX, "");
                    String filePath = BeanCommon.getBean(DefaultImgService.class).getDefaultImgFilePath(relativePath);
                    byte[] content = null;
                    try {
                        File logo = new File(filePath);
                        content = FileUtils.readFileToByteArray(logo);
                    } catch (Exception e) {
                        String detail = "导出场景出错 , 下载图片失败 ,文件地址:" + device.getLogo();
                        log.error(detail, e);
                        throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
                    }
                    if (this.isPng(content)) {
                        this.createFile(content, currentImageDir + "/logo.png");
                        image.setLOGO("logo.png");
                    } else {
                        this.createFile(content, currentImageDir + "/logo.jpg");
                        image.setLOGO("logo.jpg");
                    }
                } else {
                    byte[] content = null;
                    try {
                        content = FsCommon.downloadRawContent(device.getLogo());
                    } catch (Exception e) {
                        String detail = "导出场景出错 , 下载图片失败 ,文件地址:" + device.getLogo();
                        log.error(detail, e);
                        throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
                    }
                    // 判断图片文件类型
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
                    for (AccessMode m : device.getAccessModes()) {
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
                if (StringUtils.isEmpty(image.get系统语言())) {
                    image.set系统语言("hasNoSystemServer");
                }

                ServerYml serverYml = new ServerYml();
                Server server = new Server();
                server.setMpYmlImageNameToChange(image);
                serverYml.setServers(server);

                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(new SceneRepresenter(), dumperOptions);
                String content = yaml.dumpAs(serverYml, Tag.MAP, null);
                content = StringUtils.replaceOnce(content, "mpYmlImageNameToChange", sceneNumber + device.getImage());
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
        int[] pngHeaders = new int[]{0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
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
            String detail = "导出场景出错 , 创建文件失败,文件地址: " + filePath;
            log.error(detail, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
        }
    }

    private void createFile(String content, String filePath) {
        try {
            FileUtils.write(new File(filePath), content, "utf8");
        } catch (Exception e) {
            String detail = "导出场景出错 , 创建server.yml文件失败";
            log.error("导出场景出错 , 创建server.yml文件失败 , 内容为: " + content, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
        }
    }

    private void downloadNodeFile(String envPath, Map<String, String> file2Download) {
        for (Map.Entry<String, String> entry : file2Download.entrySet()) {
            // 下载附件
            String fdfsFilePath = entry.getValue();
            if (StringUtils.contains(entry.getValue(), "?")) {
                fdfsFilePath = StringUtils.split(entry.getValue(), "?")[0];
            }
            try {
                byte[] content = FsCommon.downloadRawContent(fdfsFilePath);
                FileUtils.writeByteArrayToFile(new File(envPath + "/" + entry.getKey()), content);
            } catch (Exception e) {
                String detail = "导出场景出错 , 下载附件失败 , 附件地址为" + fdfsFilePath;
                log.error(detail, e);
                throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
            }
        }
    }

    private void downloadPrivateKeyFile(String fdfsFilePath, String imagePath, String fileName) {
        if (StringUtils.isNotEmpty(fdfsFilePath) && StringUtils.isNotEmpty(imagePath)) {
            byte[] content = FsCommon.downloadRawContent(fdfsFilePath);
            try {
                FileUtils.writeByteArrayToFile(new File(imagePath + "/" + fileName), content);
            } catch (Exception e) {
                String detail = "导出场景出错 , 下载秘钥文件失败 , 文件地址为" + fdfsFilePath;
                log.error(detail, e);
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
        } catch (Exception e) {
            String detail = "[" + envPath + "/" + name + ".yml" + "]-文件创建失败";
            log.error(detail, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
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
        result.put("extInfo", "ext-info");
        result.put("displayContext", "display-context");
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
                } else if (property.getType().isAssignableFrom(ExtNodeInfo.class)) {
                    if (memberValue != null) {
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