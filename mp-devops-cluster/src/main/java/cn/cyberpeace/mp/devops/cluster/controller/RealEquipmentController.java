package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.converter.SerialPortConverter;
import cn.cyberpeace.mp.devops.cluster.converter.TypeConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.*;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.model.common.*;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.response.*;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import cn.cyberpeace.mp.devops.cluster.service.RealEquipmentService;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/real-equipment")
@Api(tags = "靶标管理-实装管理")
public class RealEquipmentController {

    private RealEquipmentConverter realEquipmentConverter;

    private RealEquipmentService realEquipmentService;

    private SerialPortConverter serialPortConverter;

    private TypeConverter typeConverter;

    @Autowired
    public RealEquipmentController(RealEquipmentConverter realEquipmentConverter,RealEquipmentService realEquipmentService,
                                   SerialPortConverter serialPortConverter,TypeConverter typeConverter) {
        this.realEquipmentConverter = realEquipmentConverter;
        this.realEquipmentService = realEquipmentService;
        this.serialPortConverter = serialPortConverter;
        this.typeConverter = typeConverter;
    }

    /**
     * 分页查询
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<RealEquipmentPageResponse> page(RealEquipmentPageRequest request){
        RealEquipmentConvertModel model = realEquipmentConverter.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();
        TypeAllConvertModel accessType = typeConverter.findAccessType();

        List<RealEquipmentPageResponse> list = model.getModelList().stream().map(v -> {
            ExtEntity extEntity = v.getExtEntity();
            RealEquipmentPageResponse response = RealEquipmentPageResponse.builder().id(v.getUuid()).name(v.getName())
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
    public Result<RealEquipmentByIdResponse> getById(@PathVariable String id){
        RealEquipment realEquipment = realEquipmentConverter.findById(id);
        RealEquipmentByIdResponse response = RealEquipmentByIdResponse.builder().uuid(realEquipment.getUuid())
                .name(realEquipment.getName()).logo(realEquipment.getLogo()).role(realEquipment.getRole())
                .mainCategory(realEquipment.getMainCategory()).subCategory(realEquipment.getSubCategory())
                .extSystemInfo(realEquipment.getExtSystemInfo()).extEntity(realEquipment.getExtEntity())
                .description(realEquipment.getDescription()).build();
        return new Result(response);
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    public ServiceResult add(RealEquipmentRequest request){
        RealEquipment realEquipment = realEquipmentConverter.add(request);
        return realEquipment.isSuccess() ? ServiceResult.successful() :ServiceResult.fail("");
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PutMapping("/update")
    public ServiceResult update(RealEquipmentRequest request){
        if (StringUtils.isBlank(request.getUuid())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        RealEquipment realEquipment = realEquipmentConverter.update(request);
        return realEquipment.isSuccess() ? ServiceResult.successful() :ServiceResult.fail("");
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @DeleteMapping("/delete")
    public Result<Boolean> delete(@RequestBody RealEquipmentDeleteRequest request){
        Boolean result = realEquipmentConverter.delete(request);
        return new Result<>(result);
    }

    /**
     * 登录
     */
    @ApiOperation("登录")
    @PostMapping("/sign-in")
    public Result<Boolean> signIn(@RequestBody RealEquipmentSignInRequest request){
//        Boolean result = realEquipmentConverter.delete(request);
        return new Result<>();
    }

    /**
     * 释放
     */
    @ApiOperation("释放")
    @PostMapping("/freed")
    public Result<Boolean> freed(@RequestBody RealEquipmentFreedRequest request){
        return new Result<>();
    }

    @ApiOperation("查询详情")
    @GetMapping("/get-devops-details")
    public Result<RealEquipmentDevopsDetailResponse> findDevopsDetails(@RequestParam("uuid") String id){
        RealEquipment realEquipment = realEquipmentConverter.findById(id);
        RealEquipmentDevopsDetailResponse response = RealEquipmentDevopsDetailResponse.builder()
                .name(realEquipment.getName()).logo(realEquipment.getLogo())
                .extSystemInfo(realEquipment.getExtSystemInfo()).extEntity(realEquipment.getExtEntity()).build();
        response.setAddress(realEquipment.getExtEntityAccess().stream().map(ExtEntityAccess::getIp).collect(Collectors.toList()));
        if (Objects.nonNull(realEquipment) && Objects.nonNull(realEquipment.getExtEntity())){
            ExtEntity extEntity = realEquipment.getExtEntity();
            response.setModel(extEntity.getStyle());
            response.setParams(extEntity.getParams());
            if (Objects.nonNull(extEntity.getSnmpConfig())) {
                SnmpParams snmpParams = new SnmpParams();
                SnmpConfig snmpConfigs = extEntity.getSnmpConfig();
                if (extEntity.getSnmpVersion().equals("V1")) {
                    snmpParams.setVersion(1);
                    if (Objects.nonNull(snmpConfigs)) {
                        SnmpBaseAuth snmpAuth = new SnmpBaseAuth();
                        snmpAuth.setCommunityName(snmpConfigs.getCommunityName());
                        snmpParams.setAuth(snmpAuth);
                    }
                } else if (extEntity.getSnmpVersion().equals("V2C")) {
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
                snmpParams.setOid(extEntity.getSnmpMemOid());
                SnmpCloudPlatform mem = realEquipmentService.snmpForMem(snmpParams);
                snmpParams.setOid(extEntity.getSnmpDiskOid());
                var disk = realEquipmentService.snmpForDisk(snmpParams);
            }
        }
        RealEquipmentConnectionCloudPlatform connection = realEquipmentService.connection(id);
        if (Objects.nonNull(connection)){
            response.setConnections(connection.getItems());
        }
        return new Result<>(response);
    }


    @ApiOperation("接口资源")
    @GetMapping("/network-card")
    public PageResult<SnmpInterfaceResourceCloudPlatform.Interfaces> pageNetworkCard(RealEquipmentNetworkCardPageRequest request){
        SnmpParams snmpParams = new SnmpParams();
        snmpParams.setVersion(request.getVersion());
        SnmpConfig snmpConfig = request.getSnmpConfig();
        if (Objects.nonNull(snmpConfig)){
            snmpParams.setIp(snmpConfig.getIp());
            if (StringUtils.isNotBlank(snmpConfig.getCommunityName())){
                SnmpBaseAuth snmpAuth = new SnmpBaseAuth();
                snmpAuth.setCommunityName(snmpConfig.getCommunityName());
                snmpParams.setAuth(snmpAuth);
            }else{
                SnmpAuth snmpAuth = new SnmpAuth();
                snmpAuth.setAuthKey(snmpConfig.getAuthKey());
                snmpAuth.setUsername(snmpConfig.getUsername());
                snmpAuth.setPrivKey(snmpConfig.getPrivKey());
                snmpParams.setAuth(snmpAuth);
            }
        }
        SnmpInterfaceResourceCloudPlatform model = realEquipmentService.interfacesResource(snmpParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getResult())){
            return new PageResult<>(Lists.newArrayList(),0);
        }
        Integer page = request.getPage();
        Integer perPage = request.getPerPage();
        List<SnmpInterfaceResourceCloudPlatform.Interfaces> interfaces = model.getResult().subList((page - 1) * perPage, (page - 1) * perPage + perPage);
        return new PageResult<>(interfaces,100);
    }

    @ApiOperation("场景引用")
    @GetMapping("/scenes")
    public PageResult<RealEquipmentScenesPageResponse> pageScenes(RealEquipmentScenesPageRequest request){
        return new PageResult<>(Lists.newArrayList(),100);
    }

    @ApiOperation("根据设备ID获取登录链接信息")
    @GetMapping("/connection")
    public Result<List<RealEquipmentConnection>> connection(@RequestParam String uuid){
        RealEquipmentConnectionCloudPlatform connection = realEquipmentService.connection(uuid);
        if (Objects.isNull(connection)){
            return new Result<>();
        }
        return new Result<>(connection.getItems());
    }

    @ApiOperation("根据交换机名称查询实体装备")
    @GetMapping("/by-switch-name")
    public Result<List<RealEquipmentByParams>> findBySwitchName(@RequestParam("switchName") String switchName){
        RealEquipmentBySwitchNameCloudPlatform bySwitchName = realEquipmentService.findBySwitchName(switchName);
        if (Objects.isNull(bySwitchName)){
            return new Result<>();
        }
        return new Result<>(bySwitchName.getItems());
    }

    @ApiOperation("根据串口名称查询实体装备")
    @GetMapping("/by-serial-port")
    public Result<List<RealEquipmentByParams>> findBySerialPort(@RequestParam("serialPort") String serialPort){
        RealEquipmentBySerialPortCloudPlatform bySerialPort = realEquipmentService.findBySerialPort(serialPort);
        if (Objects.isNull(bySerialPort)){
            return new Result<>();
        }
        return new Result<>(bySerialPort.getItems());
    }
}
