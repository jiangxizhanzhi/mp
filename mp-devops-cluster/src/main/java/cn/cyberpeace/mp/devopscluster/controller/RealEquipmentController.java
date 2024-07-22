 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RealEquipmentController.java.
 // File size 30618.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.RealEquipmentService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterConstant;
import cn.cyberpeace.mp.devopscluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devopscluster.converter.TypeConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.RealEquipmentBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeIdStringConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.model.*;
import cn.cyberpeace.mp.devopscluster.model.common.Hypervisor;
import cn.cyberpeace.mp.devopscluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.request.ImageRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentNetworkCardPageRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devopscluster.vo.RealEquipmentByIdVO;
import cn.cyberpeace.mp.devopscluster.vo.RealEquipmentDevopsDetailVO;
import cn.cyberpeace.mp.devopscluster.vo.RealEquipmentNetworkCardPageVO;
import cn.cyberpeace.mp.devopscluster.vo.RealEquipmentPageVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.cyberpeace.mp.base.constant.TargetEnum.REAL_EQUIPMENT_MANAGEMENT;
import static cn.cyberpeace.mp.base.constant.TargetEnum.REAL_MANAGE;

/**
 * @ClassName RealEquipmentController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/real-equipment")
@Api(tags = "靶标管理-实装管理")
@RequiredArgsConstructor
@AuthTarget({REAL_EQUIPMENT_MANAGEMENT, REAL_MANAGE})
public class RealEquipmentController {

    private final RealEquipmentConverter realEquipmentConverter;

    private final RealEquipmentService realEquipmentService;

    private final TypeConverter typeConverter;

    @Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    @AuthTarget({TargetEnum.DEVOPS_EQUIPMENT_PAGE_GROUP, REAL_EQUIPMENT_MANAGEMENT, REAL_MANAGE})
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<RealEquipmentPageVO> page(RealEquipmentPageRequest request){
        BasePageBO<RealEquipmentBO> model = realEquipmentConverter.page(new ImagePageDTO(request));
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();
        TypeAllConvertModel accessType = typeConverter.findAccessType();

        List<RealEquipmentPageVO> list = new ArrayList<>();
        for (RealEquipmentBO v : model.getList()) {
            RealEquipmentPageVO realEquipmentPageVO = getRealEquipmentPageVO(l1AndL2AndL3Type, language, systemArchitecure, accessType, v);
            list.add(realEquipmentPageVO);
        }
        return new PageResult<>(list,model.getTotal());
    }

    /**
     * @param l1AndL2AndL3Type
     * @param language
     * @param systemArchitecure
     * @param accessType
     * @param v
     * @return
     */
    private RealEquipmentPageVO getRealEquipmentPageVO(TypeAllConvertModel l1AndL2AndL3Type, TypeAllConvertModel language, TypeAllConvertModel systemArchitecure, TypeAllConvertModel accessType, RealEquipmentBO v) {
        ExtEntity extEntity = v.getExtEntity();
        CloudPlatformPageResult<TargetConnection> connection = realEquipmentService.connection(v.getUuid());
        Boolean loginEnable = false;
        if (connection != null && CollectionUtils.isNotEmpty(connection.getList())) {
            loginEnable = true;
        }
        RealEquipmentPageVO response = RealEquipmentPageVO.builder().id(v.getUuid()).name(v.getName())
                .category(v.getMainCategory()).subCategory(v.getSubCategory()).thirdCategory(v.getThirdCategory())
                .description(v.getDescription()).useStatus(v.getUseStatus()).referenceStatus(v.getReferenceStatus())
                .inPosition(v.getInPosition()).extEntityUsb(v.getExtEntityUsb()).loginEnable(loginEnable).build();
        if (Objects.nonNull(l1AndL2AndL3Type)){
           List<String> firstTypeList;
            if (CollectionUtils.isEmpty(l1AndL2AndL3Type.getFirstTypeList())) {
                firstTypeList = null;
            } else {
                List<String> result = new ArrayList<>();
                for (TypeConvertModel typeConvertModel : l1AndL2AndL3Type.getFirstTypeList()) {
                    if (typeConvertModel.getId().equals(v.getMainCategory())) {
                        String name = typeConvertModel.getName();
                        result.add(name);
                    }
                }
                firstTypeList = result;
            }
            response.setCategoryName(CollectionUtils.isEmpty(firstTypeList) ? null : firstTypeList.get(0));

            List<String> secondTypeList;
            if (CollectionUtils.isEmpty(l1AndL2AndL3Type.getSecondTypeList())) {
                secondTypeList = null;
            } else {
                List<String> result = new ArrayList<>();
                for (TypeConvertModel typeConvertModel : l1AndL2AndL3Type.getSecondTypeList()) {
                    if (typeConvertModel.getId().equals(v.getSubCategory())) {
                        String name = typeConvertModel.getName();
                        result.add(name);
                    }
                }
                secondTypeList = result;
            }
            response.setSubCategoryName(CollectionUtils.isEmpty(secondTypeList) ? null : secondTypeList.get(0));

            List<String> thirdTypeList;
            if (CollectionUtils.isEmpty(l1AndL2AndL3Type.getThirdTypeList())) {
                thirdTypeList = null;
            } else {
                List<String> result = new ArrayList<>();
                for (TypeConvertModel typeConvertModel : l1AndL2AndL3Type.getThirdTypeList()) {
                    if (typeConvertModel.getId().equals(v.getThirdCategory())) {
                        String name = typeConvertModel.getName();
                        result.add(name);
                    }
                }
                thirdTypeList = result;
            }
            response.setThirdCategoryName(CollectionUtils.isEmpty(thirdTypeList) ? null : thirdTypeList.get(0));
        }
        ExtSystemInfo extSystemInfo = v.getExtSystemInfo();
        if (Objects.nonNull(extSystemInfo)){
            if (Objects.nonNull(l1AndL2AndL3Type) && CollectionUtils.isNotEmpty(l1AndL2AndL3Type.getThirdTypeList())) {
                List<String> systemTypeList = new ArrayList<>();
                for (TypeConvertModel typeConvertModel : l1AndL2AndL3Type.getThirdTypeList()) {
                    if (typeConvertModel.getId().equals(extSystemInfo.getSystemType())) {
                        String name = typeConvertModel.getName();
                        systemTypeList.add(name);
                    }
                }
                extSystemInfo.setSystemTypeName(CollectionUtils.isEmpty(systemTypeList) ? null : systemTypeList.get(0));
            }
            if (Objects.nonNull(language) && CollectionUtils.isNotEmpty(language.getIdStringModelList())){
                List<String> languageTypeList = new ArrayList<>();
                for (TypeIdStringConvertModel typeConvertModel : language.getIdStringModelList()) {
                    if (typeConvertModel.getId().equals(extSystemInfo.getLanguage())) {
                        String name = typeConvertModel.getName();
                        languageTypeList.add(name);
                    }
                }
                extSystemInfo.setLanguageName(CollectionUtils.isEmpty(languageTypeList) ? null : languageTypeList.get(0));
            }

            if (Objects.nonNull(systemArchitecure) && CollectionUtils.isNotEmpty(systemArchitecure.getIdStringModelList())){
                List<String> systemArchitecureTypeList = new ArrayList<>();
                for (TypeIdStringConvertModel typeConvertModel : systemArchitecure.getIdStringModelList()) {
                    if (typeConvertModel.getId().equals(extSystemInfo.getSystemArchitecture())) {
                        String name = typeConvertModel.getName();
                        systemArchitecureTypeList.add(name);
                    }
                }
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
                List<String> accessTypeeTypeList = new ArrayList<>();
                for (TypeIdStringConvertModel typeConvertModel : accessType.getIdStringModelList()) {
                    if (typeConvertModel.getId().equals(accessType1)) {
                        String name = typeConvertModel.getName();
                        accessTypeeTypeList.add(name);
                    }
                }
                response.setEntityTypeName(CollectionUtils.isEmpty(accessTypeeTypeList) ? null : accessTypeeTypeList.get(0));
            }
            response.setEntityType(accessType1);

            response.setEntityDetail(JSONArray.toJSONString(extEntityAccess, SerializerFeature.WriteNullStringAsEmpty));
        }
        return response;
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询")
    @GetMapping("/{id}")
    public Result<RealEquipmentByIdVO> findByUuid(@PathVariable String id){
        RealEquipmentBO realEquipmentBO = realEquipmentConverter.findByUuid(id);
        List<ExtEntityAccess> extEntityAccess = realEquipmentBO.getExtEntityAccess();
        if (CollectionUtils.isNotEmpty(extEntityAccess)) {
            List<ExtEntityAccess> list = new ArrayList<>();
            for (ExtEntityAccess v : extEntityAccess) {
                ExtEntityAccess entityAccess = getExtEntityAccess(v);
                list.add(entityAccess);
            }
            extEntityAccess = list;
        }
        DeviceTargetPO targetPO = realEquipmentService.getTargetBo(id);
        RealEquipmentByIdVO response = RealEquipmentByIdVO.builder().uuid(realEquipmentBO.getUuid())
                .name(realEquipmentBO.getName()).logo(realEquipmentBO.getLogo()).role(realEquipmentBO.getRole())
                .mainCategory(realEquipmentBO.getMainCategory()).subCategory(realEquipmentBO.getSubCategory()).thirdCategory(realEquipmentBO.getThirdCategory())
                .systemInfo(realEquipmentBO.getExtSystemInfo()).extEntity(realEquipmentBO.getExtEntity())
                .description(realEquipmentBO.getDescription()).extEntityAccess(extEntityAccess).extEntityUsb(realEquipmentBO.getExtEntityUsb())
                .openSyslog(targetPO.getOpenSyslog()).openKafka(targetPO.getOpenKafka()).openRestapi(targetPO.getOpenRestapi())
                .openSoap(targetPO.getOpenSoap()).openSnmp(targetPO.getOpenSnmp()).build();
        return new Result(response);
    }

    /**
     * @param v
     * @return 扩展实体访问信息
     */
    private static ExtEntityAccess getExtEntityAccess(ExtEntityAccess v) {
        List<AccessProtocol> accessProtocols = v.getAccessProtocols();
        if (CollectionUtils.isNotEmpty(accessProtocols)) {
            List<AccessProtocol> list = new ArrayList<>();
            for (AccessProtocol accessProtocol : accessProtocols) {
                if (!"console".equals(accessProtocol.getProtocol())) {
                    list.add(accessProtocol);
                }
            }
            accessProtocols = list;
        }
        v.setAccessProtocols(accessProtocols);
        return v;
    }

    /**
     * 新增
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("新增")
    @ApiImplicitParam(name = "request", value = "新增实装设备请求体", dataTypeClass = RealEquipmentRequest.class)
    @PostMapping("/add")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.CREATE, title = "新增实装", target = OpTargetEnums.REALEQUIPMENT)
    public ServiceResult add(@RequestBody RealEquipmentRequest request){
        this.convertRole(request);
        realEquipmentConverter.add(request);
        return ResultCommon.success();
    }

    /**
     * 更新
     *
     * @param request
     * @return 统一服务返回结果
     */
    @ApiOperation("更新")
    @ApiImplicitParam(name = "request", value = "更新数据请求体", dataTypeClass = RealEquipmentRequest.class)
    @PutMapping("/update")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.UPDATE, title = "修改实装", target = OpTargetEnums.REALEQUIPMENT)
    public ServiceResult update(@RequestBody RealEquipmentRequest request){
        if (StringUtils.isBlank(request.getUuid())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        this.convertRole(request);
        realEquipmentConverter.update(request);
        return ResultCommon.success();
    }

    /**
     * 删除
     *
     * @param request
     * @return
     */
    @ApiOperation("删除")
    @ApiImplicitParam(name = "request", value = "删除数据请求体", dataTypeClass = RealEquipmentDeleteRequest.class)
    @DeleteMapping("/delete")
    @OperateLog(module = OpModuleEnums.TARGET, type = OpTypeEnums.DELETE, title = "删除实装", target = OpTargetEnums.REALEQUIPMENT)
    public Result<Boolean> delete(@RequestBody RealEquipmentDeleteRequest request){
        BaseCloudPlatformResult result = realEquipmentConverter.delete(request);
        return new Result<>(result.isSuccess());
    }

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    @ApiOperation("查询详情")
    @ApiImplicitParam(name = "uuid", value = "请求参数", dataTypeClass = String.class)
    @GetMapping("/get-devops-details")
    public Result<RealEquipmentDevopsDetailVO> findDevopsDetails(@RequestParam("uuid") String id){
        RealEquipmentBO realEquipmentBO = realEquipmentConverter.findByUuid(id);
        if (Objects.isNull(realEquipmentBO)){
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
        DeviceTargetPO targetPO = realEquipmentService.getTargetBo(id);
        RealEquipmentDevopsDetailVO response = RealEquipmentDevopsDetailVO.builder()
                .name(realEquipmentBO.getName()).logo(realEquipmentBO.getLogo())
                .extSystemInfo(realEquipmentBO.getExtSystemInfo()).extEntity(realEquipmentBO.getExtEntity()).extEntityUsb(realEquipmentBO.getExtEntityUsb())
                .openSyslog(targetPO.getOpenSyslog()).openKafka(targetPO.getOpenKafka()).openRestapi(targetPO.getOpenRestapi())
                .openSoap(targetPO.getOpenSoap()).openSnmp(targetPO.getOpenSnmp()).build();
        if (CollectionUtils.isNotEmpty(realEquipmentBO.getExtEntityAccess())) {
            List<String> list = new ArrayList<>();
            for (ExtEntityAccess extEntityAccess : realEquipmentBO.getExtEntityAccess()) {
                String ip = extEntityAccess.getIp();
                list.add(ip);
            }
            response.setAddress(list);
        }
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();

        ExtSystemInfo extSystemInfo = realEquipmentBO.getExtSystemInfo();
        if (Objects.nonNull(extSystemInfo)){
            if (Objects.nonNull(l1AndL2AndL3Type) && CollectionUtils.isNotEmpty(l1AndL2AndL3Type.getThirdTypeList())) {
                List<String> systemTypeList = new ArrayList<>();
                for (TypeConvertModel typeConvertModel : l1AndL2AndL3Type.getThirdTypeList()) {
                    if (typeConvertModel.getId().equals(extSystemInfo.getSystemType())) {
                        String name = typeConvertModel.getName();
                        systemTypeList.add(name);
                    }
                }
                extSystemInfo.setSystemTypeName(CollectionUtils.isEmpty(systemTypeList) ? null : systemTypeList.get(0));
            }
            if (Objects.nonNull(language) && CollectionUtils.isNotEmpty(language.getIdStringModelList())){
                List<String> languageTypeList = new ArrayList<>();
                for (TypeIdStringConvertModel typeConvertModel : language.getIdStringModelList()) {
                    if (typeConvertModel.getId().equals(extSystemInfo.getLanguage())) {
                        String name = typeConvertModel.getName();
                        languageTypeList.add(name);
                    }
                }
                extSystemInfo.setLanguageName(CollectionUtils.isEmpty(languageTypeList) ? null : languageTypeList.get(0));
            }

            if (Objects.nonNull(systemArchitecure) && CollectionUtils.isNotEmpty(systemArchitecure.getIdStringModelList())){
                List<String> systemArchitecureTypeList = new ArrayList<>();
                for (TypeIdStringConvertModel typeConvertModel : systemArchitecure.getIdStringModelList()) {
                    if (typeConvertModel.getId().equals(extSystemInfo.getSystemArchitecture())) {
                        String name = typeConvertModel.getName();
                        systemArchitecureTypeList.add(name);
                    }
                }
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
                SnmpCloudPlatform disk = realEquipmentService.snmpForDisk(snmpParams);
                if (Objects.nonNull(disk) &&  Objects.nonNull(disk.getStatus()) && disk.getStatus()){
                    response.setDiskPercent(disk.getResult().get(extEntity.getSnmpDiskOid()));
                }
            }
        }
        CloudPlatformPageResult<TargetConnection> connection = realEquipmentService.connection(id);
        if (Objects.nonNull(connection)){
            response.setConnections(connection.getList());
        }

        if (response.getExtSystemInfo() != null && "USB".equals(response.getExtSystemInfo().getSystemTypeName())) {
            response.getExtSystemInfo().setSystemArchitecture(null);
            response.getExtSystemInfo().setSystemArchitectureName(null);
            response.getExtSystemInfo().setLanguage(null);
            response.getExtSystemInfo().setLanguageName(null);
        }

        return new Result<>(response);
    }


    /**
     * 接口资源
     *
     * @param request
     * @return
     */
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
        List<RealEquipmentNetworkCardPageVO> list = new ArrayList<>();
        for (SnmpInterfaceResourceCloudPlatform.Interfaces v : interfaces) {
            RealEquipmentNetworkCardPageVO build = RealEquipmentNetworkCardPageVO.builder().name(v.getInterfaceName()).inflow(v.getInTraffic()).outflow(v.getOutTraffic()).build();
            list.add(build);
        }
        return new PageResult<>(list,model.getResult().size());
    }

    /**
     * 根据设备ID获取登录链接信息
     *
     * @param uuid
     * @return
     */
    @ApiOperation("根据设备ID获取登录链接信息")
    @ApiImplicitParam(name = "uuid", value = "设备id", dataTypeClass = String.class)
    @GetMapping("/connection")
    public Result<List<TargetConnection>> connection(@RequestParam String uuid){
        CloudPlatformPageResult<TargetConnection> connection = realEquipmentService.connection(uuid);
        if (Objects.isNull(connection)){
            return new Result<>();
        }
        return new Result<>(connection.getList());
    }

    /**
     * 根据交换机名称查询实体装备
     *
     * @param switchName
     * @return
     */
    @ApiOperation("根据交换机名称查询实体装备")
    @ApiImplicitParam(name = "switchName", value = "交换机名称", dataTypeClass = String.class)
    @GetMapping("/by-switch-name")
    public Result<List<RealEquipmentByParams>> findBySwitchName(@RequestParam("switchName") String switchName){
        CloudPlatformPageResult<RealEquipmentByParams> bySwitchName = realEquipmentService.findBySwitchName(switchName);
        if (Objects.isNull(bySwitchName)){
            return new Result<>();
        }
        return new Result<>(bySwitchName.getList());
    }

    /**
     * 根据串口名称查询实体装备
     *
     * @param serialPort
     * @return
     */
    @ApiOperation("根据串口名称查询实体装备")
    @ApiImplicitParam(name = "serialPort", value = "端口名称", dataTypeClass = String.class)
    @GetMapping("/by-serial-port")
    public Result<List<RealEquipmentByParams>> findBySerialPort(@RequestParam("serialPort") String serialPort){
        CloudPlatformPageResult<RealEquipmentByParams> bySerialPort = realEquipmentService.findBySerialPort(serialPort);
        if (Objects.isNull(bySerialPort)){
            return new Result<>();
        }
        return new Result<>(bySerialPort.getList());
    }

    /**
     * 查询实装设备的在位状态
     *
     * @return
     */
    @GetMapping("/position")
    @ApiOperation(value = "查询实装设备的在位状态")
    public ServiceResult<List<OptionBO>> getPositionStatus() {
        return optionService.getIntOptions(DevopsClusterConstant.MP_COMMON_OPTION_TYPE_DEVOPS_CLUSTER_POSITION);
    }

    /**
     * 实装设备的使用状态
     *
     * @return
     */
    @GetMapping("/use")
    @ApiOperation(value = "实装设备的使用状态")
    public ServiceResult<List<OptionBO>> getUseStatus() {
        return optionService.getIntOptions(DevopsClusterConstant.MP_COMMON_OPTION_TYPE_DEVOPS_CLUSTER_USE);
    }

    /**
     * 获取服务器列表
     *
     * @return
     */
    @ApiOperation("获取服务器列表")
    @GetMapping("/hypervisor_list")
    public ServiceResult<List<Hypervisor>> hypervisorList() {
        return new ServiceResult<>(realEquipmentService.hypervisorList()).asResult();
    }

    /**
     * 根据hostName查询usb列表
     *
     * @param hostName
     * @return
     */
    @ApiOperation("根据hostName查询usb列表")
    @ApiImplicitParam(name = "hostName", value = "服务器主机名", dataTypeClass = String.class)
    @GetMapping("/usb_list")
    public ListObjServiceResult<EntityUsb> usbList(@RequestParam("hostName") String hostName) {
        ListObjServiceResult<EntityUsb> result = realEquipmentService.physicalUsbListByHost(hostName);
        return result;
    }

    private void convertRole(RealEquipmentRequest request) {
        // 网络设备/路由器 -> role = 21
        // 网络设备/交换机 -> role = 22
        // 网络设备/防火墙 -> role = 23
        if (request.getMainCategory() != null && request.getSubCategory() != null) {
            if (request.getMainCategory() == 3 && request.getSubCategory() == 52) {
                request.setRole(23);
            } else if (request.getMainCategory() == 3 && request.getSubCategory() == 50) {
                request.setRole(22);
            } else if (request.getMainCategory() == 3 && request.getSubCategory() == 55) {
                request.setRole(22);
            } else if (request.getMainCategory() == 3 && request.getSubCategory() == 56) {
                request.setRole(22);
            } else if (request.getMainCategory() == 3 && request.getSubCategory() == 51) {
                request.setRole(21);
            }
        }
    }
}
