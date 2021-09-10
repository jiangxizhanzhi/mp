package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.SerialPortConverter;
import cn.cyberpeace.mp.devops.cluster.converter.SwitchConverter;
import cn.cyberpeace.mp.devops.cluster.converter.TypeConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.SerialPortConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.SwitchConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeL2AllConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.Switches;
import cn.cyberpeace.mp.devops.cluster.response.SerialPortResponse;
import cn.cyberpeace.mp.devops.cluster.response.TypeAllResponse;
import cn.cyberpeace.mp.devops.cluster.response.TypeL2AllResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import cn.cyberpeace.mp.devops.cluster.service.TypeService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TypeController {

    private TypeConverter typeConverter;

    private TypeService typeService;

    private SerialPortConverter serialPortConverter;

    private SwitchConverter switchConverter;

    @Autowired
    public TypeController(TypeConverter typeConverter,TypeService typeService,SerialPortConverter serialPortConverter,
                          SwitchConverter switchConverter) {
        this.typeConverter = typeConverter;
        this.typeService = typeService;
        this.serialPortConverter = serialPortConverter;
        this.switchConverter = switchConverter;
    }

    /**
     * 查询所有分类
     * @return
     */
    @ApiOperation("查询所有靶标分类")
    @GetMapping("/target-categories")
    public Result<TypeAllResponse> targetCategories(){
        TypeAllConvertModel roles = typeConverter.findRoles();
        TypeAllConvertModel entityRoles = typeConverter.findEntityRoles();
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();
        TypeAllConvertModel snmpVersion = typeConverter.findSnmpVersion();
        TypeAllConvertModel imageType = typeConverter.findImageType();
        TypeAllResponse allResponse = new TypeAllResponse();
        List<SerialPortConvertModel> serialPortConvertModels = serialPortConverter.list();
        serialPortConvertModels.add(new SerialPortConvertModel(50,100,10,"10.100.0.102","1"));
        allResponse.setSerialPortResponses(Lists.newArrayList(new SerialPortResponse("CCM-1","CCM-1",serialPortConvertModels)));
        allResponse.setSwitchConvertModels(switchConverter.treeList());
        allResponse.setAccessProtocolsPorts(typeService.findAccessProtocolsPort().getItems());
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
    @ApiOperation("查询所有分类")
    @GetMapping("/categories")
    public Result<TypeAllResponse> list(){
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        if (Objects.isNull(l1AndL2AndL3Type)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse(l1AndL2AndL3Type.getFirstTypeList(),
                l1AndL2AndL3Type.getSecondTypeList(),l1AndL2AndL3Type.getThirdTypeList());
        return new Result<>(response);
    }

    /**
     * 查询所有分类
     * @return
     */
    @ApiOperation("查询所有二级分类")
    @GetMapping("/l2-categories")
    public Result<TypeL2AllResponse> l2List(){
        TypeL2AllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2Type();
        if (Objects.isNull(l1AndL2AndL3Type)){
            return new Result<>();
        }
        return new Result<>(new TypeL2AllResponse(l1AndL2AndL3Type.getItems()));
    }


    /**
     * 查询所有角色
     * @return
     */
    @ApiOperation("查询所有角色")
    @GetMapping("/roles")
    public Result<TypeAllResponse>  roles(){
        TypeAllConvertModel model = typeConverter.findRoles();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setRole(model.getIdIntegerModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有部署类型
     * @return
     */
    @ApiOperation("查询所有部署类型")
    @GetMapping("/deploy_type")
    public Result<TypeAllResponse>  deployType(){
        TypeAllConvertModel model = typeConverter.findDeployType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setDeployType(model.getIdIntegerModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有虚拟类型
     * @return
     */
    @ApiOperation("查询所有虚拟类型")
    @GetMapping("/image-types")
    public Result<TypeAllResponse> imageType(){
        TypeAllConvertModel model = typeConverter.findImageType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setImageType(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有语言
     * @return
     */
    @ApiOperation("查询所有语言")
    @GetMapping("/language")
    public Result<TypeAllResponse> language(){
        TypeAllConvertModel model = typeConverter.findLanguage();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setLanguage(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有系统架构
     * @return
     */
    @ApiOperation("查询所有系统架构")
    @GetMapping("/system-architecure")
    public Result<TypeAllResponse> systemArchitecure(){
        TypeAllConvertModel model = typeConverter.findSystemArchitecure();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setSystemArchitecure(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有系统架构
     * @return
     */
    @ApiOperation("查询所有系统架构")
    @GetMapping("/disk-format")
    public Result<TypeAllResponse> diskFormat(){
        TypeAllConvertModel model = typeConverter.findDiskFormat();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setDiskFormat(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有访问类型
     * @return
     */
    @ApiOperation("查询所有访问类型")
    @GetMapping("/access-type")
    public Result<TypeAllResponse> accessType(){
        TypeAllConvertModel model = typeConverter.findAccessType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setAccessType(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有服务分类
     * @return
     */
    @ApiOperation("查询所有服务分类")
    @GetMapping("/service-categories")
    public Result<TypeAllResponse> serviceCategories(){
        TypeAllConvertModel model = typeConverter.findServiceCategories();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setServiceCategories(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有服务管理-启动参数类型
     * @return
     */
    @ApiOperation("查询启动参数类型")
    @GetMapping("/service-start-type")
    public Result<TypeAllResponse> serviceStartType(){
        TypeAllConvertModel model = typeConverter.findServiceStartType();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setServiceStartTypes(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有平台
     * @return
     */
    @ApiOperation("查询所有平台数据")
    @GetMapping("/installer-platforms")
    public Result<TypeAllResponse> installerPlatforms(){
        TypeAllConvertModel model = typeConverter.findInstallerPlatforms();
        if (Objects.isNull(model)){
            return new Result<>();
        }
        TypeAllResponse response = new TypeAllResponse();
        response.setInstallerPlatforms(model.getIdStringModelList());
        return new Result<>(response);

    }

    @ApiOperation("查询串口配置")
    @GetMapping("/serial-port-list")
    public Result<List<SerialPortResponse>> serialPortList(){
        List<SerialPortConvertModel> list = serialPortConverter.list();
        list.add(new SerialPortConvertModel(50,100,10,"10.100.0.102","1"));
        return new Result<>(Lists.newArrayList(new SerialPortResponse("CCM-1","CCM-1",list)));
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
