package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.RealEquipmentService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.converter.TypeConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.RealEquipmentBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageTargetConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeIdStringConvertModel;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentNetworkCardPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devops.cluster.service.DeviceLogAsyncService;
import cn.cyberpeace.mp.devops.cluster.vo.*;
import cn.cyberpeace.mp.devops.cluster.vo.common.PageResult;
import cn.cyberpeace.mp.devops.cluster.vo.common.Result;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.bo.TargetLogBO;
import cn.cyberpeace.mp.devops.log.dto.LogOperateDTO;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/real-equipment")
@Api(tags = "靶标管理-实装管理")
@RequiredArgsConstructor
public class RealEquipmentController {

    private final RealEquipmentConverter realEquipmentConverter;

    private final RealEquipmentService realEquipmentService;

    private final TypeConverter typeConverter;

    private final DeviceLogAsyncService deviceLogAsyncService;

    private final ImagesConverter imagesConverter;

    private final DeviceService deviceService;

    /**
     * 分页查询
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<RealEquipmentPageVO> page(RealEquipmentPageRequest request){
        BasePageBO<RealEquipmentBO> model = realEquipmentConverter.page(new ImagePageDTO(request));
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            return new PageResult<>(Lists.newArrayList(), model.getTotal());
        }
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();
        TypeAllConvertModel accessType = typeConverter.findAccessType();

        List<RealEquipmentPageVO> list = model.getList().stream().map(v -> {
            ExtEntity extEntity = v.getExtEntity();
            RealEquipmentPageVO response = RealEquipmentPageVO.builder().id(v.getUuid()).name(v.getName())
                    .category(v.getMainCategory()).subCategory(v.getSubCategory()).thirdCategory(v.getThirdCategory())
                    .description(v.getDescription()).useStatus(v.getUseStatus()).referenceStatus(v.getReferenceStatus())
                    .inPosition(v.getInPosition()).build();
            if (Objects.nonNull(l1AndL2AndL3Type)){
               List<String> firstTypeList = CollectionUtils.isEmpty(l1AndL2AndL3Type.getFirstTypeList()) ?
                            null : l1AndL2AndL3Type.getFirstTypeList().stream()
                        .filter(typeConvertModel -> typeConvertModel.getId().equals(v.getMainCategory()))
                       .map(TypeConvertModel::getName).collect(Collectors.toList());
               response.setCategoryName(CollectionUtils.isEmpty(firstTypeList) ? null : firstTypeList.get(0));

                List<String> secondTypeList = CollectionUtils.isEmpty(l1AndL2AndL3Type.getSecondTypeList()) ? null : l1AndL2AndL3Type.getSecondTypeList().stream()
                        .filter(typeConvertModel -> typeConvertModel.getId().equals(v.getSubCategory()))
                        .map(TypeConvertModel::getName).collect(Collectors.toList());
                response.setSubCategoryName(CollectionUtils.isEmpty(secondTypeList) ? null : secondTypeList.get(0));

                List<String> thirdTypeList = CollectionUtils.isEmpty(l1AndL2AndL3Type.getThirdTypeList()) ? null : l1AndL2AndL3Type.getThirdTypeList().stream()
                        .filter(typeConvertModel -> typeConvertModel.getId().equals(v.getThirdCategory()))
                        .map(TypeConvertModel::getName).collect(Collectors.toList());
                response.setThirdCategoryName(CollectionUtils.isEmpty(thirdTypeList) ? null : thirdTypeList.get(0));
            }
            ExtSystemInfo extSystemInfo = v.getExtSystemInfo();
            if (Objects.nonNull(extSystemInfo)){
                if (Objects.nonNull(l1AndL2AndL3Type) && CollectionUtils.isNotEmpty(l1AndL2AndL3Type.getThirdTypeList())) {
                    List<String> systemTypeList = l1AndL2AndL3Type.getThirdTypeList().stream()
                            .filter(typeConvertModel -> typeConvertModel.getId().equals(extSystemInfo.getSystemType()))
                            .map(TypeConvertModel::getName).collect(Collectors.toList());
                    extSystemInfo.setSystemTypeName(CollectionUtils.isEmpty(systemTypeList) ? null : systemTypeList.get(0));
                }
                if (Objects.nonNull(language) && CollectionUtils.isNotEmpty(language.getIdStringModelList())){
                    List<String> languageTypeList = language.getIdStringModelList().stream()
                            .filter(typeConvertModel -> typeConvertModel.getId().equals(extSystemInfo.getLanguage()))
                            .map(TypeIdStringConvertModel::getName).collect(Collectors.toList());
                    extSystemInfo.setLanguageName(CollectionUtils.isEmpty(languageTypeList) ? null : languageTypeList.get(0));
                }

                if (Objects.nonNull(systemArchitecure) && CollectionUtils.isNotEmpty(systemArchitecure.getIdStringModelList())){
                    List<String> systemArchitecureTypeList = systemArchitecure.getIdStringModelList().stream()
                            .filter(typeConvertModel -> typeConvertModel.getId().equals(extSystemInfo.getSystemArchitecture()))
                            .map(TypeIdStringConvertModel::getName).collect(Collectors.toList());
                    extSystemInfo.setSystemArchitectureName(CollectionUtils.isEmpty(systemArchitecureTypeList) ? null : systemArchitecureTypeList.get(0));
                }
                response.setExtSystemInfo(extSystemInfo);
            }

            if (Objects.nonNull(extEntity)){
                response.setModel(extEntity.getStyle());
                response.setParams(extEntity.getParams());
            }

            List<ExtEntityAccess> extEntityAccess = v.getExtEntityAccess();
            if (CollectionUtils.isNotEmpty(extEntityAccess)){
                String accessType1 = Objects.isNull(extEntityAccess.get(0)) ? "" : extEntityAccess.get(0).getAccessType();
                if (Objects.nonNull(accessType) && CollectionUtils.isNotEmpty(accessType.getIdStringModelList())){
                    List<String> accessTypeeTypeList = accessType.getIdStringModelList().stream()
                            .filter(typeConvertModel -> typeConvertModel.getId().equals(accessType1))
                            .map(TypeIdStringConvertModel::getName).collect(Collectors.toList());
                    response.setEntityTypeName(CollectionUtils.isEmpty(accessTypeeTypeList) ? null : accessTypeeTypeList.get(0));
                }
                response.setEntityType(accessType1);

                response.setEntityDetail(JSONArray.toJSONString(extEntityAccess));
            }
            return response;
        }).collect(Collectors.toList());

        return new PageResult<>(list,model.getTotal());
    }

    @ApiOperation("根据ID查询")
    @GetMapping("/{id}")
    public Result<RealEquipmentByIdVO> findByUuid(@PathVariable String id){
        RealEquipmentBO realEquipmentBO = realEquipmentConverter.findByUuid(id);
        List<ExtEntityAccess> extEntityAccess = realEquipmentBO.getExtEntityAccess();
        if (CollectionUtils.isNotEmpty(extEntityAccess)) {
            extEntityAccess = extEntityAccess.stream().map(v -> {
                List<AccessProtocol> accessProtocols = v.getAccessProtocols();
                if (CollectionUtils.isNotEmpty(accessProtocols)) {
                    accessProtocols = accessProtocols.stream().filter(accessProtocol -> !"console".equals(accessProtocol.getProtocol())).collect(Collectors.toList());
                }
                v.setAccessProtocols(accessProtocols);
                return v;
            }).collect(Collectors.toList());
        }

        RealEquipmentByIdVO response = RealEquipmentByIdVO.builder().uuid(realEquipmentBO.getUuid())
                .name(realEquipmentBO.getName()).logo(realEquipmentBO.getLogo()).role(realEquipmentBO.getRole())
                .mainCategory(realEquipmentBO.getMainCategory()).subCategory(realEquipmentBO.getSubCategory()).thirdCategory(realEquipmentBO.getThirdCategory())
                .systemInfo(realEquipmentBO.getExtSystemInfo()).extEntity(realEquipmentBO.getExtEntity())
                .description(realEquipmentBO.getDescription()).extEntityAccess(extEntityAccess).build();
        return new Result(response);
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "新增实装", target = OpTargetEnums.REALEQUIPMENT)
    public ServiceResult add(@RequestBody RealEquipmentRequest request){
        Device device = realEquipmentConverter.add(request);
        final TargetLogBO bo = TargetLogBO.builder().uuid(device.getUuid()).name(device.getName()).equType(Objects.nonNull(device.getCategory()) ? device.getCategory().toString() : "").build();
        final ExtEntity extEntity = device.getExtEntity();
        if (Objects.nonNull(extEntity)) {
            bo.setEquIp(Objects.nonNull(extEntity.getExtInfo()) ? extEntity.getExtInfo().getIpAddress() : "");
        }
        LogOperateDTO dto = LogOperateDTO.builder().module(OpModuleEnums.TARGET).type(OpTypeEnums.CREATE)
                .target(OpTargetEnums.REALEQUIPMENT).locale(LocaleContextHolder.getLocale()).t(bo).build();
        deviceLogAsyncService.log(dto, ServletUtils.getRequest());
        return new Result(TargetLogVO.builder().uuid(device.getUuid()).name(device.getName()).build());
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PutMapping("/update")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.UPDATE, title = "修改实装", target = OpTargetEnums.REALEQUIPMENT)
    public ServiceResult update(@RequestBody RealEquipmentRequest request){
        if (StringUtils.isBlank(request.getUuid())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        Device device = realEquipmentConverter.update(request);
        final TargetLogBO bo = TargetLogBO.builder().uuid(device.getUuid()).name(device.getName()).equType(Objects.nonNull(device.getCategory()) ? device.getCategory().toString() : "").build();
        final ExtEntity extEntity = device.getExtEntity();
        if (Objects.nonNull(extEntity)) {
            bo.setEquIp(Objects.nonNull(extEntity.getExtInfo()) ? extEntity.getExtInfo().getIpAddress() : "");
        }
        LogOperateDTO dto = LogOperateDTO.builder().module(OpModuleEnums.TARGET).type(OpTypeEnums.UPDATE)
                .target(OpTargetEnums.REALEQUIPMENT).locale(LocaleContextHolder.getLocale()).t(bo).build();
        deviceLogAsyncService.log(dto, ServletUtils.getRequest());
        return new Result(TargetLogVO.builder().uuid(device.getUuid()).name(device.getName()).build());
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @DeleteMapping("/delete")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "删除实装", target = OpTargetEnums.REALEQUIPMENT)
    public Result<Boolean> delete(@RequestBody RealEquipmentDeleteRequest request){
        List<ImageTargetConvertModel> models = imagesConverter.findByUuids(request.getUuids());
        List<Integer> deviceIdList = models.stream().map(ImageTargetConvertModel::getId).map(Integer::valueOf).collect(Collectors.toList());
        final CloudPlatformPageResult<Device> cloudPlatformPageResult = deviceService.findByIds(deviceIdList);
        List<Device> deviceList = cloudPlatformPageResult.getList();
        BaseCloudPlatformResult result = realEquipmentConverter.delete(request);
        for (Device device : deviceList) {
            final TargetLogBO bo = TargetLogBO.builder().uuid(device.getUuid()).name(device.getName()).equType(Objects.nonNull(device.getCategory()) ? device.getCategory().toString() : "").build();
            final ExtEntity extEntity = device.getExtEntity();
            if (Objects.nonNull(extEntity)) {
                bo.setEquIp(Objects.nonNull(extEntity.getExtInfo()) ? extEntity.getExtInfo().getIpAddress() : "");
            }
            LogOperateDTO dto = LogOperateDTO.builder().module(OpModuleEnums.TARGET).type(OpTypeEnums.DELETE)
                    .target(OpTargetEnums.REALEQUIPMENT).locale(LocaleContextHolder.getLocale()).t(bo).build();
            deviceLogAsyncService.log(dto, ServletUtils.getRequest());
        }
        return new Result<>(result.isSuccess());
    }

    /**
     * 登入
     */
    @ApiOperation("登入")
    @GetMapping("/login/{uuid}")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.LOGIN, title = "实装登录", target = OpTargetEnums.REALEQUIPMENT)
    public ServiceResult login(@PathVariable String uuid){
        RealEquipmentBO model = realEquipmentConverter.findByUuid(uuid);
        final TargetLogBO bo = TargetLogBO.builder().uuid(model.getUuid()).name(model.getName()).equType(Objects.nonNull(model.getMainCategory()) ? model.getMainCategory().toString() : "").build();
        final ExtEntity extEntity = model.getExtEntity();
        if (Objects.nonNull(extEntity)) {
            bo.setEquIp(Objects.nonNull(extEntity.getExtInfo()) ? extEntity.getExtInfo().getIpAddress() : "");
        }
        LogOperateDTO dto = LogOperateDTO.builder().module(OpModuleEnums.TARGET).type(OpTypeEnums.LOGIN)
                .target(OpTargetEnums.REALEQUIPMENT).locale(LocaleContextHolder.getLocale()).t(bo).build();
        deviceLogAsyncService.log(dto, ServletUtils.getRequest());
        return ServiceResult.successful();
    }

    @ApiOperation("查询详情")
    @GetMapping("/get-devops-details")
    public Result<RealEquipmentDevopsDetailVO> findDevopsDetails(@RequestParam("uuid") String id){
        RealEquipmentBO realEquipmentBO = realEquipmentConverter.findByUuid(id);
        if (Objects.isNull(realEquipmentBO)){
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
        RealEquipmentDevopsDetailVO response = RealEquipmentDevopsDetailVO.builder()
                .name(realEquipmentBO.getName()).logo(realEquipmentBO.getLogo())
                .extSystemInfo(realEquipmentBO.getExtSystemInfo()).extEntity(realEquipmentBO.getExtEntity()).build();
        if (CollectionUtils.isNotEmpty(realEquipmentBO.getExtEntityAccess())) {
            response.setAddress(realEquipmentBO.getExtEntityAccess().stream().map(ExtEntityAccess::getIp).collect(Collectors.toList()));
        }
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();

        ExtSystemInfo extSystemInfo = realEquipmentBO.getExtSystemInfo();
        if (Objects.nonNull(extSystemInfo)){
            if (Objects.nonNull(l1AndL2AndL3Type) && CollectionUtils.isNotEmpty(l1AndL2AndL3Type.getThirdTypeList())) {
                List<String> systemTypeList = l1AndL2AndL3Type.getThirdTypeList().stream()
                        .filter(typeConvertModel -> typeConvertModel.getId().equals(extSystemInfo.getSystemType()))
                        .map(TypeConvertModel::getName).collect(Collectors.toList());
                extSystemInfo.setSystemTypeName(CollectionUtils.isEmpty(systemTypeList) ? null : systemTypeList.get(0));
            }
            if (Objects.nonNull(language) && CollectionUtils.isNotEmpty(language.getIdStringModelList())){
                List<String> languageTypeList = language.getIdStringModelList().stream()
                        .filter(typeConvertModel -> typeConvertModel.getId().equals(extSystemInfo.getLanguage()))
                        .map(TypeIdStringConvertModel::getName).collect(Collectors.toList());
                extSystemInfo.setLanguageName(CollectionUtils.isEmpty(languageTypeList) ? null : languageTypeList.get(0));
            }

            if (Objects.nonNull(systemArchitecure) && CollectionUtils.isNotEmpty(systemArchitecure.getIdStringModelList())){
                List<String> systemArchitecureTypeList = systemArchitecure.getIdStringModelList().stream()
                        .filter(typeConvertModel -> typeConvertModel.getId().equals(extSystemInfo.getSystemArchitecture()))
                        .map(TypeIdStringConvertModel::getName).collect(Collectors.toList());
                extSystemInfo.setSystemArchitectureName(CollectionUtils.isEmpty(systemArchitecureTypeList) ? null : systemArchitecureTypeList.get(0));
            }
            response.setExtSystemInfo(extSystemInfo);
        }

        if (Objects.nonNull(realEquipmentBO.getExtEntity())){
            ExtEntity extEntity = realEquipmentBO.getExtEntity();
            response.setModel(extEntity.getStyle());
            response.setParams(extEntity.getParams());
            if (Objects.nonNull(extEntity.getSnmpConfig()) && StringUtils.isNotBlank(extEntity.getSnmpVersion())) {
                SnmpParams snmpParams = new SnmpParams();
                SnmpConfig snmpConfigs = extEntity.getSnmpConfig();
                if (extEntity.getSnmpVersion().equals("V1") || extEntity.getSnmpVersion().equals("V2")) {
                    snmpParams.setVersion(2);
                    if (Objects.nonNull(snmpConfigs)) {
                        SnmpBaseAuth snmpAuth = new SnmpBaseAuth();
                        snmpAuth.setCommunityName(snmpConfigs.getCommunityName());
                        snmpParams.setAuth(snmpAuth);
                    }
                } else if (extEntity.getSnmpVersion().equals("V3")) {
                    snmpParams.setVersion(3);
                    if (Objects.nonNull(snmpConfigs)) {
                        SnmpAuth snmpAuth = new SnmpAuth();
                        snmpAuth.setAuthKey(snmpConfigs.getAuthKey());
                        snmpAuth.setUsername(snmpConfigs.getUsername());
                        snmpAuth.setPrivKey(snmpConfigs.getPrivKey());
                        snmpParams.setAuth(snmpAuth);
                    }
                }
                snmpParams.setIp(extEntity.getSnmpConfig().getIp());
                snmpParams.setOid(extEntity.getSnmpCpuOid());
                SnmpCloudPlatform cpu = realEquipmentService.snmpForCpu(snmpParams);
                if (Objects.nonNull(cpu) && Objects.nonNull(cpu.getStatus()) && cpu.getStatus()){
                    response.setCpuPercent(cpu.getResult().get(extEntity.getSnmpCpuOid()));
                }
                snmpParams.setOid(extEntity.getSnmpMemOid());
                SnmpCloudPlatform mem = realEquipmentService.snmpForMem(snmpParams);
                if (Objects.nonNull(mem) &&  Objects.nonNull(mem.getStatus()) && mem.getStatus()){
                    response.setRamPercent(mem.getResult().get(extEntity.getSnmpMemOid()));
                }
                snmpParams.setOid(extEntity.getSnmpDiskOid());
                var disk = realEquipmentService.snmpForDisk(snmpParams);
                if (Objects.nonNull(disk) &&  Objects.nonNull(disk.getStatus()) && disk.getStatus()){
                    response.setDiskPercent(disk.getResult().get(extEntity.getSnmpDiskOid()));
                }
            }
        }
        CloudPlatformPageResult<TargetConnection> connection = realEquipmentService.connection(id);
        if (Objects.nonNull(connection)){
            response.setConnections(connection.getList());
        }
        return new Result<>(response);
    }


    @ApiOperation("接口资源")
    @GetMapping("/network-card")
    public PageResult<RealEquipmentNetworkCardPageVO> pageNetworkCard(RealEquipmentNetworkCardPageRequest request){
        RealEquipmentBO realEquipmentBO = realEquipmentConverter.findByUuid(request.getUuid());
        if (Objects.isNull(realEquipmentBO)){
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
        ExtEntity extEntity = realEquipmentBO.getExtEntity();
        if (Objects.isNull(extEntity) || StringUtils.isBlank(extEntity.getSnmpVersion()) || Objects.isNull(extEntity.getSnmpConfig())){
            return new PageResult<>(Lists.newArrayList(),0);
        }
        SnmpParams snmpParams = new SnmpParams();
        SnmpConfig snmpConfig = extEntity.getSnmpConfig();
        if (extEntity.getSnmpVersion().equals("V1") || extEntity.getSnmpVersion().equals("V2")) {
            snmpParams.setVersion(2);
            if (Objects.nonNull(snmpConfig)) {
                SnmpBaseAuth snmpAuth = new SnmpBaseAuth();
                snmpAuth.setCommunityName(snmpConfig.getCommunityName());
                snmpParams.setAuth(snmpAuth);
            }
        } else if (extEntity.getSnmpVersion().equals("V3")) {
            snmpParams.setVersion(3);
            if (Objects.nonNull(snmpConfig)) {
                SnmpAuth snmpAuth = new SnmpAuth();
                snmpAuth.setAuthKey(snmpConfig.getAuthKey());
                snmpAuth.setUsername(snmpConfig.getUsername());
                snmpAuth.setPrivKey(snmpConfig.getPrivKey());
                snmpParams.setAuth(snmpAuth);
            }
        }
        snmpParams.setIp(extEntity.getSnmpConfig().getIp());
        snmpParams.setOid(extEntity.getSnmpCpuOid());
        SnmpInterfaceResourceCloudPlatform model = realEquipmentService.interfacesResource(snmpParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getResult())){
            return new PageResult<>(Lists.newArrayList(),0);
        }

        Integer page = request.getPage();
        Integer perPage = request.getPerPage();
        Integer to = Math.min((page * perPage), model.getResult().size());
        List<SnmpInterfaceResourceCloudPlatform.Interfaces> interfaces = model.getResult().subList((page - 1) * perPage, to);
        List<RealEquipmentNetworkCardPageVO> list = interfaces.stream().map(v ->
                RealEquipmentNetworkCardPageVO.builder().name(v.getInterfaceName()).inflow(v.getInTraffic()).outflow(v.getOutTraffic()).build())
                .collect(Collectors.toList());
        return new PageResult<>(list,model.getResult().size());
    }

    @ApiOperation("根据设备ID获取登录链接信息")
    @GetMapping("/connection")
    public Result<List<TargetConnection>> connection(@RequestParam String uuid){
        CloudPlatformPageResult<TargetConnection> connection = realEquipmentService.connection(uuid);
        if (Objects.isNull(connection)){
            return new Result<>();
        }
        return new Result<>(connection.getList());
    }

    @ApiOperation("根据交换机名称查询实体装备")
    @GetMapping("/by-switch-name")
    public Result<List<RealEquipmentByParams>> findBySwitchName(@RequestParam("switchName") String switchName){
        CloudPlatformPageResult<RealEquipmentByParams> bySwitchName = realEquipmentService.findBySwitchName(switchName);
        if (Objects.isNull(bySwitchName)){
            return new Result<>();
        }
        return new Result<>(bySwitchName.getList());
    }

    @ApiOperation("根据串口名称查询实体装备")
    @GetMapping("/by-serial-port")
    public Result<List<RealEquipmentByParams>> findBySerialPort(@RequestParam("serialPort") String serialPort){
        CloudPlatformPageResult<RealEquipmentByParams> bySerialPort = realEquipmentService.findBySerialPort(serialPort);
        if (Objects.isNull(bySerialPort)){
            return new Result<>();
        }
        return new Result<>(bySerialPort.getList());
    }
}
