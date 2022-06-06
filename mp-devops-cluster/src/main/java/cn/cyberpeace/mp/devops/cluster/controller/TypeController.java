package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Switches;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.TypeService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.SerialPortConverter;
import cn.cyberpeace.mp.devops.cluster.converter.SwitchConverter;
import cn.cyberpeace.mp.devops.cluster.converter.TypeConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.SerialPortConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.SwitchConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.common.TargetType;
import cn.cyberpeace.mp.devops.cluster.vo.SerialPortVO;
import cn.cyberpeace.mp.devops.cluster.vo.TypeAllVO;
import cn.cyberpeace.mp.devops.cluster.vo.TypeL2AllVO;
import cn.cyberpeace.mp.devops.cluster.vo.common.ListResult;
import cn.cyberpeace.mp.devops.cluster.vo.common.Result;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * 类型管理类
 */
@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/types")
@Api(tags = "靶标管理-分类管理")
@RequiredArgsConstructor
public class TypeController {

    private static final String CCM_1 = "CCM-1";

    private final TypeConverter typeConverter;

    private final TypeService typeService;

    private final SerialPortConverter serialPortConverter;

    private final SwitchConverter switchConverter;

    /**
     * 查询所有分类
     * @return
     */
    @ApiOperation("查询所有靶标分类")
    @GetMapping("/target-categories")
    public Result<TypeAllVO> targetCategories(){
        TypeAllConvertModel roles = typeConverter.findRoles();
        TypeAllConvertModel entityRoles = typeConverter.findEntityRoles();
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();
        TypeAllConvertModel snmpVersion = typeConverter.findSnmpVersion();
        TypeAllConvertModel imageType = typeConverter.findImageType();
        TypeAllVO allResponse = new TypeAllVO();
        List<SerialPortConvertModel> serialPortConvertModels = serialPortConverter.list();
        allResponse.setSerialPortRespons(Lists.newArrayList(new SerialPortVO(CCM_1,CCM_1,serialPortConvertModels)));
        allResponse.setSwitchConvertModels(switchConverter.treeList());
        allResponse.setAccessProtocolsPorts(typeService.findAccessProtocolsPort().getList());
        allResponse.setSnmpVersions(Objects.nonNull(snmpVersion) ? snmpVersion.getIdStringModelList() : null);
        allResponse.setRole(Objects.nonNull(roles) ? roles.getIdIntegerModelList() : null);
        allResponse.setEntityRole(Objects.nonNull(entityRoles) ? entityRoles.getIdIntegerModelList() : null);
        allResponse.setFirstTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getFirstTypeList() : null);
        allResponse.setSecondTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getSecondTypeList() : null);
        allResponse.setThirdTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getThirdTypeList() : null);
        allResponse.setLanguage(Objects.nonNull(language) ? language.getIdStringModelList() : null);
        allResponse.setSystemArchitecure(Objects.nonNull(systemArchitecure) ?
                systemArchitecure.getIdStringModelList() : null);
        allResponse.setImageType(Objects.nonNull(imageType) ? imageType.getIdStringModelList() : null);
        return new Result<>(allResponse);
    }

    /**
     * 查询所有分类
     * @return
     */
    @ApiOperation("查询所有镜像管理分类")
    @GetMapping("/image-mg-categories")
    public Result<TypeAllVO> imageCategories(Boolean isAll){
        if (Objects.isNull(isAll)){
            isAll = Boolean.FALSE;
        }
        TypeAllConvertModel roles = typeConverter.findRoles(isAll);
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type(isAll);
        TypeAllConvertModel imageType = typeConverter.findImageType(isAll);
        TypeAllConvertModel diskFormat = typeConverter.findDiskFormat();
        TypeAllVO allResponse = new TypeAllVO();
        allResponse.setRole(Objects.nonNull(roles) ? roles.getIdIntegerModelList() : null);
        allResponse.setFirstTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getFirstTypeList() : null);
        allResponse.setSecondTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getSecondTypeList() : null);
        allResponse.setThirdTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getThirdTypeList() : null);
        allResponse.setImageType(Objects.nonNull(imageType) ? imageType.getIdStringModelList() : null);
        allResponse.setHwDiskBusList(typeConverter.findHwDiskBus());
        allResponse.setHwVifModelList(typeConverter.findHwVifModel());
        allResponse.setHwVideoModelList(typeConverter.findHwVideoModel());
        allResponse.setDiskFormat(Objects.nonNull(diskFormat) ? diskFormat.getIdStringModelList() : null);
        return new Result<>(allResponse);
    }

    /**
     * 查询所有分类
     * @return
     */
    @ApiOperation("查询所有分类")
    @GetMapping("/categories")
    public Result<TypeAllVO> list(){
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        if (Objects.isNull(l1AndL2AndL3Type)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO(l1AndL2AndL3Type.getFirstTypeList(),
                l1AndL2AndL3Type.getSecondTypeList(),l1AndL2AndL3Type.getThirdTypeList());
        return new Result<>(response);
    }

    /**
     * 查询所有分类
     * @return
     */
    @ApiOperation("查询所有二级分类")
    @GetMapping("/l2-categories")
    public Result<TypeL2AllVO> l2List(){
        ListResult<TargetType> l1AndL2AndL3Type = typeConverter.findL1AndL2Type();
        if (Objects.isNull(l1AndL2AndL3Type)){
            return new Result<>();
        }
        return new Result<>(new TypeL2AllVO(l1AndL2AndL3Type.getList()));
    }


    /**
     * 查询所有角色
     * @return
     */
    @ApiOperation("查询所有角色")
    @GetMapping("/roles")
    public Result<TypeAllVO>  roles(){
        TypeAllConvertModel model = typeConverter.findRoles();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setRole(model.getIdIntegerModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有部署类型
     * @return
     */
    @ApiOperation("查询所有部署类型")
    @GetMapping("/deploy_type")
    public Result<TypeAllVO>  deployType(){
        TypeAllConvertModel model = typeConverter.findDeployType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setDeployType(model.getIdIntegerModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有虚拟类型
     * @return
     */
    @ApiOperation("查询所有虚拟类型")
    @GetMapping("/image-types")
    public Result<TypeAllVO> imageType(){
        TypeAllConvertModel model = typeConverter.findImageType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setImageType(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有语言
     * @return
     */
    @ApiOperation("查询所有语言")
    @GetMapping("/language")
    public Result<TypeAllVO> language(){
        TypeAllConvertModel model = typeConverter.findLanguage();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setLanguage(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有系统架构
     * @return
     */
    @ApiOperation("查询所有系统架构")
    @GetMapping("/system-architecure")
    public Result<TypeAllVO> systemArchitecure(){
        TypeAllConvertModel model = typeConverter.findSystemArchitecure();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setSystemArchitecure(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询支持的磁盘格式
     * @return
     */
    @ApiOperation("查询支持的磁盘格式")
    @GetMapping("/disk-format")
    public Result<TypeAllVO> diskFormat(){
        TypeAllConvertModel model = typeConverter.findDiskFormat();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setDiskFormat(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有访问类型
     * @return
     */
    @ApiOperation("查询所有访问类型")
    @GetMapping("/access-type")
    public Result<TypeAllVO> accessType(){
        TypeAllConvertModel model = typeConverter.findAccessType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setAccessType(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有服务分类
     * @return
     */
    @ApiOperation("查询所有服务管理分类")
    @GetMapping("/service-mg-categories")
    public Result<TypeAllVO> serviceMgCategories(){
        TypeAllConvertModel serviceCategories = typeConverter.findServiceCategories();
        TypeAllConvertModel deployType = typeConverter.findDeployType();
        TypeAllConvertModel installerPlatforms = typeConverter.findInstallerPlatforms();

        TypeAllVO response = new TypeAllVO();
        response.setDeployType(Objects.nonNull(deployType) ?  deployType.getIdIntegerModelList() : null);
        response.setServiceCategories(Objects.nonNull(serviceCategories) ?
                serviceCategories.getIdStringModelList() : null);
        response.setInstallerPlatforms(Objects.nonNull(installerPlatforms) ? installerPlatforms.getIdStringModelList() : null);
        return new Result<>(response);
    }

    /**
     * 查询所有实装管理分类
     * @return
     */
    @ApiOperation("查询所有实装管理分类")
    @GetMapping("/entity-mg-categories")
    public Result<TypeAllVO> entityMgCategories(){
        TypeAllConvertModel entityRoles = typeConverter.findEntityRoles();
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();
        TypeAllConvertModel snmpVersion = typeConverter.findSnmpVersion();
        TypeAllVO allResponse = new TypeAllVO();
        List<SerialPortConvertModel> serialPortConvertModels = serialPortConverter.list();
        allResponse.setSerialPortRespons(Lists.newArrayList(new SerialPortVO(CCM_1,CCM_1,serialPortConvertModels)));
        allResponse.setSwitchConvertModels(switchConverter.treeList());
        allResponse.setAccessProtocolsPorts(typeService.findAccessProtocolsPort().getList());
        allResponse.setSnmpVersions(Objects.nonNull(snmpVersion) ? snmpVersion.getIdStringModelList() : null);
        allResponse.setEntityRole(Objects.nonNull(entityRoles) ? entityRoles.getIdIntegerModelList() : null);
        allResponse.setFirstTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getFirstTypeList() : null);
        allResponse.setSecondTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getSecondTypeList() : null);
        allResponse.setThirdTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getThirdTypeList() : null);
        allResponse.setLanguage(Objects.nonNull(language) ? language.getIdStringModelList() : null);
        allResponse.setSystemArchitecure(Objects.nonNull(systemArchitecure) ?
                systemArchitecure.getIdStringModelList() : null);
        return new Result<>(allResponse);
    }

    /**
     * 查询所有服务分类
     * @return
     */
    @ApiOperation("查询所有服务分类")
    @GetMapping("/service-categories")
    public Result<TypeAllVO> serviceCategories(){
        TypeAllConvertModel model = typeConverter.findServiceCategories();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setServiceCategories(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有服务管理-启动参数类型
     * @return
     */
    @ApiOperation("查询启动参数类型")
    @GetMapping("/service-start-type")
    public Result<TypeAllVO> serviceStartType(){
        TypeAllConvertModel model = typeConverter.findServiceStartType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setServiceStartTypes(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有平台
     * @return
     */
    @ApiOperation("查询所有平台数据")
    @GetMapping("/installer-platforms")
    public Result<TypeAllVO> installerPlatforms(){
        TypeAllConvertModel model = typeConverter.findInstallerPlatforms();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setInstallerPlatforms(model.getIdStringModelList());
        return new Result<>(response);

    }

    @ApiOperation("查询串口配置")
    @GetMapping("/serial-port-list")
    public Result<List<SerialPortVO>> serialPortList(){
        List<SerialPortConvertModel> list = serialPortConverter.list();
        return new Result<>(Lists.newArrayList(new SerialPortVO(CCM_1,CCM_1,list)));
    }

    @ApiOperation("查询交换机部分信息数据")
    @GetMapping("/switch-tree-list")
    public Result<List<SwitchConvertModel>> treeList(){
        return new Result<>(switchConverter.treeList());
    }

    @ApiOperation("查询交换机所有信息")
    @GetMapping("/switch-list")
    public Result<List<Switches>> switchList(){
        return new Result<>(switchConverter.list());
    }
}
