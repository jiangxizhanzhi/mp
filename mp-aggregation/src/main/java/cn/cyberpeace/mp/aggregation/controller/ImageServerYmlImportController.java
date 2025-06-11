// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name SceneImportController.java.
// File size 5121.
// File create date Fri Jul 12 12:55:32 CST 2024.
// File update date Fri Jul 12 12:55:32 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.config.AggregationConfig;
import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.request.sceneimport.SceneImportRequest;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.FilePathYmlParser;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.AccessModeParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageInfoParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ImagesService;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devopscluster.converter.TargetConverter;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.ImageRequest;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneConfigElementMapper;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigElementMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName SceneImportController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Api(value = "靶标server.yml导入", tags = "靶标server.yml导入")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/images")
@AuthTarget(TargetEnum.ADMIN)
public class ImageServerYmlImportController {

    @Resource
    private AggregationConfig aggregationConfig;

    @Resource
    private ImagesConverter imagesConverter;

    @Resource
    private TargetConverter targetConverter ;

    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    @Resource
    private SceneImportController sceneImportController ;

    @Resource
    private SceneConfigElementMapper sceneConfigElementMapper;

    @Resource
    private CloudPluginSceneConfigElementMapper cloudPluginSceneConfigElementMapper;

    @ApiOperation("场景模板导入")
    @ApiImplicitParam(name = "request", value = "请求", dataTypeClass = SceneImportRequest.class)
    @PostMapping("/import_scene_templates")
    public ServiceResult<Boolean> importSceneTemplate(@RequestBody SceneImportRequest request) {
        log.info("导入场景模板开始 , 输入参数为: {}", JsonCommon.stringify(request));
        if (StringUtils.isEmpty(request.getImagePath())) {
            String detail = "导入场景参数错误,缺少path参数 , 输入参数为: " + JsonCommon.stringify(request);
            log.info(detail);
            return ResultCommon.fail(detail);
        }
        // 遍历目录 取所有的servers.yml的绝对地址
        List<String> topoYmlFiles = findTopoYmlFiles(Path.of(request.getPath()));
        List<String> serverYmlFiles = findServersYmlFiles(Path.of(request.getPath()));
        log.info("发现 topo.yml 文件共 {} 个", serverYmlFiles.size());
        log.info("发现 servers.yml 文件共 {} 个", serverYmlFiles.size());
        List<String> imagePathList = new ArrayList<>();
        for (String s : serverYmlFiles) {
            imagePathList.add(StringUtils.replace(s, "\\servers.yml", ""));
        }
        for (String topo : topoYmlFiles) {
            String envPath = topo.replace("\\topo.yml", "");
            SceneImportRequest sceneImportRequest = new SceneImportRequest();
            sceneImportRequest.setImagePathList(imagePathList);
            sceneImportRequest.setPath(envPath);
            sceneImportRequest.setTemplate(true);
            sceneImportController.importResourceCenterV2(sceneImportRequest);
        }
        return ResultCommon.success(true);
    }

    @ApiOperation("使用正常的模型cpu ram disk去补充拓扑节点缺失的cpu ram disk")
    @PostMapping("/fix_cpu_ram_disk")
    public ServiceResult<Boolean> fixCpuRamDisk() {
        HumanCondition maxIdCondition = new HumanCondition().reverseOrderBy(SceneConfigElementModel::getPk);
        maxIdCondition.setLimit(1);
        Integer maxPk = sceneConfigElementMapper.list(maxIdCondition).get(0).getPk();


        HumanCondition minIdCondition = new HumanCondition().orderBy(SceneConfigElementModel::getPk);
        minIdCondition.setLimit(1);
        Integer minPk = sceneConfigElementMapper.list(minIdCondition).get(0).getPk();

        int step = 500;

        for (int i = minPk; i < maxPk; i += step) {

            int up = i + step;
            if (maxPk <= (i + step)) {
                up = maxPk + 1;
            }

            List<SceneConfigElementModel> sceneConfigElementModels = sceneConfigElementMapper.list(new HumanCondition()
                    .filterGreaterThanOrEqual(SceneConfigElementModel::getPk, i)
                    .filterLessThan(SceneConfigElementModel::getPk, up));

            for (SceneConfigElementModel sceneConfigElementModel : sceneConfigElementModels) {
                boolean cpuAndRamDiskIsNotValid = sceneConfigElementModel.getCpu() == null || sceneConfigElementModel.getCpu() == 0
                        || sceneConfigElementModel.getRam() == null || sceneConfigElementModel.getRam() == 0
                        || sceneConfigElementModel.getDisk() == null || sceneConfigElementModel.getDisk() == 0;
                if (!cpuAndRamDiskIsNotValid) {
                    continue;
                }


                List<CloudPluginSceneConfigElementModel> cloudPluginSceneConfigElementModels = cloudPluginSceneConfigElementMapper.list(new HumanCondition()
                        .filterEqual(CloudPluginSceneConfigElementModel::getSceneElement, sceneConfigElementModel.getNodeId())
                        .filterEqual(CloudPluginSceneConfigElementModel::getScene, sceneConfigElementModel.getScene()));

                if (CollectionUtils.isEmpty(cloudPluginSceneConfigElementModels)) {
                    continue;
                } else {
                    CloudPluginSceneConfigElementModel cloudPluginSceneConfigElementModel = cloudPluginSceneConfigElementModels.get(0);
                    String imageUuid = cloudPluginSceneConfigElementModel.getImageUuid();
                    if (StringUtils.isNotBlank(imageUuid)) {
                        try {
                            Device device = targetConverter.findByUuid(imageUuid);
                            int cpu = StringUtils.isBlank(device.getCpu()) ||  Integer.parseInt(device.getCpu()) == 0 ? 1 : Integer.parseInt(device.getCpu());
                            int ram = StringUtils.isBlank(device.getMemory()) ||  Integer.parseInt(device.getMemory()) == 0 ? 2048 : Integer.parseInt(device.getMemory());
                            int disk = StringUtils.isBlank(device.getDisk()) ||  Integer.parseInt(device.getDisk()) == 0 ? 30 : Integer.parseInt(device.getDisk());
                            sceneConfigElementModel.setCpu(cpu);
                            sceneConfigElementModel.setRam(ram);
                            sceneConfigElementModel.setDisk(disk);

                            sceneConfigElementMapper.save(sceneConfigElementModel);
                        } catch (Exception e) {
                            log.error("通过imageUuid {}获取靶标失败", imageUuid);
                        }
                    }
                }
            }
        }
        return ResultCommon.success(true);
    }


    @ApiOperation("servers.yml 导入")
    @ApiImplicitParam(name = "request", value = "请求", dataTypeClass = SceneImportRequest.class)
    @PostMapping("/import_images")
    public ServiceResult<String> importImages(@RequestBody SceneImportRequest request) {
        log.info("导入servers.yml开始 , 输入参数为: {}", JsonCommon.stringify(request));
        if (StringUtils.isEmpty(request.getImagePath())) {
            String detail = "导入场景参数错误,缺少image path参数 , 输入参数为: " + JsonCommon.stringify(request);
            log.info(detail);
            return ResultCommon.fail(detail);
        }

        // 开启一个线程跑导入
        singleThreadExecutor.submit(() -> {
            // 遍历目录 取所有的servers.yml的绝对地址
            List<String> serverYmlFiles = findServersYmlFiles(Path.of(request.getImagePath()));
            log.info("发现 servers.yml 文件共 {} 个", serverYmlFiles.size());

            for (int i =0 ;i<serverYmlFiles.size();i++) {
                String serverYml = serverYmlFiles.get(i);
                log.info("======================================================");
                log.info("开始处理第 {} 个文件 ，{}", i + 1, serverYml);
                File file = new File(serverYml);
                if (file.exists()) {
                    ImageParseResult imageParseResult = new FilePathYmlParser<>(serverYml, ImageParseResult.class).parse();
                    Map<String, ImageInfoParseResult> servers = imageParseResult.getServers();
                    for (Map.Entry<String, ImageInfoParseResult> entry : servers.entrySet()) {
                        String key = entry.getKey();
                        ImageInfoParseResult value = entry.getValue();
                        log.info("解析到靶标名称:{} , 靶标类型:{}", key, value.getImageType());
                        // 如果靶标在mp库中存在，则跳过
                        var model = existsModel(key);
                        if (model != null) {
                            //更新systemTyp和parentSystemType
                            if (Objects.isNull(model.getSystemType())) {
                                model.setSystemType(value.getSystemType());
                                model.setParentSystemType(value.getParentSystemType());
                                DeviceTargetMapper deviceTargetMapper = BeanCommon.getBean(DeviceTargetMapper.class);
                                //更新mp
                                deviceTargetMapper.updateById(model);
                                //更新cloudmanager
                                DeviceService deviceService = BeanCommon.getBean(DeviceService.class);
                                List<Device> devices = deviceService.findByIds(List.of(Integer.parseInt(model.getId())));
                                if (CollectionUtils.isEmpty(devices)) {
                                    log.error("靶标{}存在于mp但是不存在于cloudmanager, 请确认，或者删除mp靶标管理处靶标重新导入", model.getName());
                                } else {
                                    Device device = devices.get(0);
                                    device.getExtSystemInfo().setParentSystemType(value.getParentSystemType());
                                    device.getExtSystemInfo().setSystemType(value.getSystemType());
                                    ImagesService imagesService = BeanCommon.getBean(ImagesService.class);
                                    imagesService.update(device.getUuid(), device);

                                }
                            }
                            log.info("靶标 {} 已存在，uuid 为 {} ，跳过导入", key, model.getUuid());
                        } else {
                            if (BaseCloudPluginSceneElementModel.ImageType.VM.equals(value.getImageType())) {
                                try {
                                    importVm(serverYml, key, value);
                                } catch (Exception e) {
                                    log.error("导入失败", e);
                                }
                            } else {
                                try {
                                    importContainer(serverYml, key, value);
                                } catch (Exception e) {
                                    log.error("导入失败", e);
                                }
                            }
                        }
                    }
                }
                log.info("{} 文件处理结束", serverYml);
                log.info("======================================================");
            }
        });

        return new ServiceResult<>("").asResult();
    }

    private DeviceTargetPO existsModel(String imageName) {
        DevopsTargetService a = BeanCommon.getBean(DevopsTargetService.class);
        ImagePageDTO dto = new ImagePageDTO();
        dto.setName(imageName);
        dto.setLikeName(false);
        var res = a.page(dto);
        if (CollectionUtils.isNotEmpty(res)) {
            return res.get(0);
        } else {
            return null;
        }
    }

    public Device addByImport(ImageRequest request){
        request.setLogo(FsCommon.getFileKey(request.getLogo()));
        if (CollectionUtils.isNotEmpty(request.getAccessProtocolList()) && BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(request.getImageType())) {
            // 容器不支持vnc、spice和com
            Set<String> unsupporteedProtocols = new HashSet<>(Arrays.asList("vnc", "spice", "com", "COM"));
            for (var a : request.getAccessProtocolList()) {
                String protocol = a.getProtocol();
                if (StringUtils.isNotEmpty(protocol) && unsupporteedProtocols.contains(protocol)) {
                    throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("容器不支持配置vnc、spice和com"));
                }
            }
        }
        validImageRequest(request);
        // role值处理
        this.convertRole(request);
        var device = imagesConverter.addByImport(request);
        return device;
    }

    private void convertRole(ImageRequest request){
        // 网络设备/路由器 -> role = 21
        // 网络设备/交换机 -> role = 22
        // 网络设备/防火墙 -> role = 23
        if (request.getCategory() != null && request.getSecondCategory() != null) {
            if (request.getCategory() == 3 && request.getSecondCategory() == 52) {
                request.setRole(23);
            } else if (request.getCategory() == 3 && request.getSecondCategory() == 50) {
                request.setRole(22);
            } else if (request.getCategory() == 3 && request.getSecondCategory() == 55) {
                request.setRole(22);
            } else if (request.getCategory() == 3 && request.getSecondCategory() == 56) {
                request.setRole(22);
            } else if (request.getCategory() == 3 && request.getSecondCategory() == 51) {
                request.setRole(21);
            }
        }
    }

    private void validImageRequest(ImageRequest request){
        if (StringUtils.isNotBlank(request.getName()) && request.getName().length() > 126){
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_NAME_EXCEED_MAX_LENGTH);
        }
        if (StringUtils.isNotBlank(request.getDescription()) && request.getDescription().length() > 1000){
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_DESCRIPTION_EXCEED_MAX_LENGTH);
        }
    }

    private void importVm(String serverImlPath, String imageName, ImageInfoParseResult parseResult) {
        VmTemplate vmTemplate = this.getVmTemplate(serverImlPath, imageName, parseResult);
        String templateId = vmTemplate.getTemplateId();
        String templateName = vmTemplate.getTemplateName();

        log.info("开始创建镜像管理数据");
        var imageRequest = createVmImageRequest(imageName, templateId, templateName, parseResult);

        String logoPath = getDeviceImage(serverImlPath);
        imageRequest.setLogo(logoPath);

        log.info("输入参数: {}", JsonCommon.stringify(imageRequest));
        var device = addByImport(imageRequest);
        log.info("镜像管理创建结束,得到 device id: {}", device.getUuid());
    }

    private String getDeviceImage(String serverImlPath) {
        // 处理下靶标图片
        var logoPath = StringUtils.replace(serverImlPath, "servers.yml", "logo.png");
        File file = new File(logoPath);
        if (file.exists()) {
            String fdfsPath = FsCommon.upload(logoPath);
            String fileName = file.getName();
            var fdfsSceneFilePath = String.format("%s?filename=%s", fdfsPath, fileName);
            return fdfsSceneFilePath;
        } else {
            logoPath = StringUtils.replace(serverImlPath, "servers.yml", "logo.jpg");
            file = new File(logoPath);
            if (file.exists()) {
                String fdfsPath = FsCommon.upload(logoPath);
                String fileName = file.getName();
                var fdfsSceneFilePath = String.format("%s?filename=%s", fdfsPath, fileName);
                return fdfsSceneFilePath;
            } else {
                return null;
            }
        }
    }

    private void importContainer(String serverImlPath, String imageName, ImageInfoParseResult parseResult) {
        ContainerTemplate containerTemplate = this.getContainerTemplate(serverImlPath, parseResult);
        String tempdirUuid = containerTemplate.getTempdirUuid();
        String userFileName = containerTemplate.getFileName();

        log.info("开始创建镜像管理数据");
        var imageRequest = createContainerImageRequest(imageName, tempdirUuid, userFileName, parseResult);

        String logoPath = getDeviceImage(serverImlPath);
        imageRequest.setLogo(logoPath);

        log.info("输入参数: {}", JsonCommon.stringify(imageRequest));
        var device = addByImport(imageRequest);
        log.info("镜像管理创建结束,得到 device id: {}", device.getUuid());
    }

    private VmTemplate getVmTemplate(String serverImlPath, String imageName, ImageInfoParseResult parseResult) {
        String prefixPath = aggregationConfig.getPrefixPath();
        String prefixLinyun = aggregationConfig.getPrefixLinyun();
        String importImageUrl = aggregationConfig.getImportImageUrl();

        log.info("开始将靶标导入到虚拟化平台,前缀 {} 替换 {} , 导入地址 {} , ........", prefixPath, prefixLinyun, importImageUrl);
        String templateId = "";
        String templateName = "";
        ImportImageBody imageBody = new ImportImageBody();
        if (SystemTypeCommandUtils.isWindows(String.valueOf(parseResult.getSystemType()))) {
            imageBody.setOs("windows");
        } else {
            imageBody.setOs("linux");
        }
        imageBody.setDiskFormat("qcow2");
        var fileName = StringUtils.replace(serverImlPath, prefixPath, prefixLinyun);
        fileName = StringUtils.replace(fileName, "servers.yml", "disk");
        imageBody.setFileName(fileName);
        imageBody.setName(imageName);
        try {
            String body = JsonCommon.stringify(imageBody);
            log.info("请求:{} , 参数:{}", importImageUrl, body);
            var result = HttpUtils.sendPost(importImageUrl, body);
            log.info("结果:{}", result);
            Response<ImportImageResult> response = JsonCommon.parse(result, new TypeReference<Response<ImportImageResult>>() {
            });
            if (response != null && response.getData() != null) {
                templateId = response.getData().getId();
                templateName = response.getData().getName();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("导入结束,得到 template id: {} , template name: {}", templateId, templateName);
        VmTemplate vmTemplate = new VmTemplate();
        vmTemplate.setTemplateId(templateId);
        vmTemplate.setTemplateName(templateName);
        return vmTemplate;
    }

    private ImageRequest createVmImageRequest(String imageName, String templateId, String templateName, ImageInfoParseResult parseResult) {
        ImageRequest imageRequest = new ImageRequest();
        imageRequest.setSupportQga(false);
        imageRequest.setBehaveSupport(false);
        imageRequest.setSupportCollect(false);
        imageRequest.setCanParticipantsUse(false);
        imageRequest.setAttackMockDeviceFlag(false);
        imageRequest.setOperateMachine(false);
        imageRequest.setName(imageName);
        imageRequest.setDescription(parseResult.get描述());
        imageRequest.setLogo("");// todo
        imageRequest.setRole(parseResult.getRole());
        imageRequest.setCategory(parseResult.getCategory());
        imageRequest.setSecondCategory(parseResult.getSecondCategory());
        imageRequest.setThirdCategory(parseResult.getThirdCategory());
        imageRequest.setImageType(parseResult.getImageType());

        imageRequest.setExtGateway(new ExtGateway());
        imageRequest.getExtGateway().setConsoleConfig(new AccessProtocol());
        imageRequest.getExtGateway().setPortConfigs(new ArrayList<>());

        imageRequest.setAccessProtocolList(new ArrayList<>());
        for (AccessModeParseResult accessMode : parseResult.getAccessModes()) {
            AccessProtocol accessProtocol = new AccessProtocol();
            accessProtocol.setProtocol(accessMode.getProtocol());
            if (StringUtils.isNotEmpty(accessMode.getPort())) {
                accessProtocol.setPort(Integer.parseInt(accessMode.getPort()));
            }
            accessProtocol.setUsername(accessMode.getUsername());
            accessProtocol.setPassword(accessMode.getPassword());
            accessProtocol.setPrivateKeyEnable(false);
            imageRequest.getAccessProtocolList().add(accessProtocol);
        }

        imageRequest.setExtSystemInfo(new ExtSystemInfo());
        imageRequest.getExtSystemInfo().setLanguage(parseResult.getLanguage());
        imageRequest.getExtSystemInfo().setSystemArchitecture(parseResult.getSystemArchitecture());
        imageRequest.getExtSystemInfo().setSystemType(parseResult.getSystemType());

        imageRequest.setExtImageInfo(new ExtImageInfo());
        imageRequest.getExtImageInfo().setImageSource(2);
        imageRequest.getExtImageInfo().setMetaData(new ImageMetaData());
        imageRequest.getExtImageInfo().getMetaData().setHwDiskBus("ide");

        imageRequest.setExtVm(new ExtVm());
        imageRequest.getExtVm().setCpu(parseResult.calFlavor().getCpu());
        imageRequest.getExtVm().setRam(parseResult.calFlavor().getRam());
        imageRequest.getExtVm().setDisk(parseResult.calFlavor().getDisk());
        imageRequest.getExtVm().setInitSupport(true);
        imageRequest.getExtVm().setQuickCreateSupport(false);
        imageRequest.getExtVm().setIsoAttach(false);
        imageRequest.getExtVm().setExtInfo(new HashMap<>());
        imageRequest.getExtVm().setTemplateId(templateId);
        imageRequest.getExtVm().setTemplateName(templateName);
        return imageRequest;
    }

    private ImageRequest createContainerImageRequest(String imageName, String tempdirUuid, String userFileName, ImageInfoParseResult parseResult) {
        ImageRequest imageRequest = new ImageRequest();
        imageRequest.setBehaveSupport(false);
        imageRequest.setSupportCollect(false);
        imageRequest.setAttackMockDeviceFlag(false);
        imageRequest.setOperateMachine(false);
        imageRequest.setName(imageName);
        imageRequest.setDescription(parseResult.get描述());
        imageRequest.setLogo("");// todo
        imageRequest.setRole(parseResult.getRole());
        imageRequest.setCategory(parseResult.getCategory());
        imageRequest.setSecondCategory(parseResult.getSecondCategory());
        imageRequest.setThirdCategory(parseResult.getThirdCategory());
        imageRequest.setImageType(parseResult.getImageType());

        imageRequest.setExtGateway(new ExtGateway());
        imageRequest.getExtGateway().setConsoleConfig(new AccessProtocol());
        imageRequest.getExtGateway().setPortConfigs(new ArrayList<>());

        imageRequest.setAccessProtocolList(new ArrayList<>());
        for (AccessModeParseResult accessMode : parseResult.getAccessModes()) {
            AccessProtocol accessProtocol = new AccessProtocol();
            accessProtocol.setProtocol(accessMode.getProtocol());
            if (StringUtils.isNotEmpty(accessMode.getPort())) {
                accessProtocol.setPort(Integer.parseInt(accessMode.getPort()));
            }
            accessProtocol.setUsername(accessMode.getUsername());
            accessProtocol.setPassword(accessMode.getPassword());
            accessProtocol.setPrivateKeyEnable(false);
            imageRequest.getAccessProtocolList().add(accessProtocol);
        }

        imageRequest.setExtSystemInfo(new ExtSystemInfo());
        imageRequest.getExtSystemInfo().setLanguage(parseResult.getLanguage());
        imageRequest.getExtSystemInfo().setSystemArchitecture(parseResult.getSystemArchitecture());
        imageRequest.getExtSystemInfo().setSystemType(parseResult.getSystemType());

        imageRequest.setExtImageInfo(new ExtImageInfo());
        imageRequest.getExtImageInfo().setImageSource(3);
        imageRequest.getExtImageInfo().setUploadType("http");
        imageRequest.getExtImageInfo().setDiskFormat("raw");
        imageRequest.getExtImageInfo().setTmpdirUuid(tempdirUuid);
        imageRequest.getExtImageInfo().setUserFilename(userFileName);
        imageRequest.getExtImageInfo().setMetaData(new ImageMetaData());
        imageRequest.getExtImageInfo().getMetaData().setInteractive(false);
        imageRequest.getExtImageInfo().getMetaData().setTty(false);
        imageRequest.getExtImageInfo().getMetaData().setEnvironmentStr("");

        imageRequest.setExtDocker(new ExtDocker());
        imageRequest.getExtDocker().setDisk(20);
        imageRequest.getExtDocker().setRam(512);
        return imageRequest;
    }

    private ContainerTemplate getContainerTemplate(String serverImlPath, ImageInfoParseResult parseResult) {
        String containerUploadUrl = aggregationConfig.getContainerUploadUrl();
        String filePath = StringUtils.replace(serverImlPath, "servers.yml", "disk");
        try {
            var result = HttpUtils.sendHttpPostWithFile(containerUploadUrl, new HashMap<>(), List.of(new File(filePath)));
            var response = JsonCommon.snakeParse(result, new TypeReference<Response<ContainerImport>>() {
            });
            ContainerImport containerImport = response.getData();
            ContainerTemplate template = new ContainerTemplate();
            template.setFileName(containerImport.getFileNames().get(0));
            template.setTempdirUuid(containerImport.getTempdirUuid());
            return template;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Getter
    @Setter
    public static class ImportImageBody {
        private String name;
        private String fileName;
        private String diskFormat;
        private String os;
    }

    @Getter
    @Setter
    public static class ContainerImport {
        private String tempdirUuid;
        private List<String> fileNames;
    }

    @Getter
    @Setter
    public static class Response<T> {
        private Boolean success;
        private Integer code;
        private String message;
        private String jobId;
        private T data;
    }

    @Getter
    @Setter
    public static class ImportImageResult {
        private String id;
        private String name;
    }

    public List<String> findServersYmlFiles(Path dir) {
        List<String> result = new ArrayList<>();
        Queue<Path> dirs = new LinkedList<>();
        dirs.add(dir);
        while (!dirs.isEmpty()) {
            var currentDir = dirs.poll();
            String checkServerYmlPath = currentDir + "\\servers.yml";
            if (new File(checkServerYmlPath).exists()) {
                result.add(checkServerYmlPath);
            }
            // 将当前目录下的子目录加入队列 等待遍历
            try {
                Files.list(currentDir).forEach(path -> {
                    if (Files.isDirectory(path)) {
                        dirs.add(path);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public List<String> findTopoYmlFiles(Path dir) {
        List<String> result = new ArrayList<>();
        Queue<Path> dirs = new LinkedList<>();
        dirs.add(dir);
        while (!dirs.isEmpty()) {
            var currentDir = dirs.poll();
            String checkServerYmlPath = currentDir + "\\topo.yml";
            if (new File(checkServerYmlPath).exists()) {
                result.add(checkServerYmlPath);
            }
            // 将当前目录下的子目录加入队列 等待遍历
            try {
                Files.list(currentDir).forEach(path -> {
                    if (Files.isDirectory(path)) {
                        dirs.add(path);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @Getter
    @Setter
    private static class VmTemplate {
        private String templateId = "";
        private String templateName = "";
    }

    @Getter
    @Setter
    private static class ContainerTemplate {
        private String tempdirUuid = "";
        private String fileName = "";
    }

}
