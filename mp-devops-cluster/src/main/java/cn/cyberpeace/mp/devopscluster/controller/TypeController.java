package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Switches;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.TypeService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.SerialPortConverter;
import cn.cyberpeace.mp.devopscluster.converter.SwitchConverter;
import cn.cyberpeace.mp.devopscluster.converter.TypeConverter;
import cn.cyberpeace.mp.devopscluster.converter.model.SerialPortConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.SwitchConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devopscluster.model.common.TargetType;
import cn.cyberpeace.mp.devopscluster.vo.SerialPortVO;
import cn.cyberpeace.mp.devopscluster.vo.TypeAllVO;
import cn.cyberpeace.mp.devopscluster.vo.TypeL2AllVO;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
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
@AuthTarget(TargetEnum.NO_AUTH)
public class TypeController {

    private static final String CCM_1 = "CCM-1";

    private final TypeConverter typeConverter;

    private final TypeService typeService;

    private final SerialPortConverter serialPortConverter;

    private final SwitchConverter switchConverter;

    /**
     * 查询所有分类
     *
     * @return
     */
    @ApiOperation("查询所有靶标分类")
    @GetMapping("/target-categories")
    public Result<TypeAllVO> targetCategories() {
        TypeAllConvertModel roles = typeConverter.findRoles();
        TypeAllConvertModel entityRoles = typeConverter.findEntityRoles();
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();
        TypeAllConvertModel snmpVersion = typeConverter.findSnmpVersion();
        TypeAllConvertModel imageType = typeConverter.findImageType();
        TypeAllVO allResponse = new TypeAllVO();
        List<SerialPortConvertModel> serialPortConvertModels = serialPortConverter.list();
        allResponse.setSerialPortRespons(Lists.newArrayList(new SerialPortVO(CCM_1, CCM_1, serialPortConvertModels)));
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
     *
     * @param isAll
     * @return
     */
    @AuthTarget(TargetEnum.DEVOPS_CATEGORY_GROUP)
    @ApiOperation("查询所有镜像管理分类")
    @ApiImplicitParam(name = "isAll", value = "是否全部", dataTypeClass = Boolean.class)
    @GetMapping("/image-mg-categories")
    public Result<TypeAllVO> imageCategories(Boolean isAll) {
        if (Objects.isNull(isAll)) {
            isAll = Boolean.FALSE;
        }
        TypeAllConvertModel roles = typeConverter.findRoles(isAll);
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type(isAll);
        TypeAllConvertModel imageType = typeConverter.findImageType(isAll);
        TypeAllConvertModel diskFormat = typeConverter.findDiskFormat();
        TypeAllVO allResponse = new TypeAllVO();
        allResponse.setRole(Objects.nonNull(roles) ? roles.getIdIntegerModelList() : null);
        List<TypeConvertModel> firstTypeList = Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getFirstTypeList() : null;
        if (CollectionUtils.isNotEmpty(firstTypeList)) {
            for (int i = 0; i < firstTypeList.size(); i++) {
                if (firstTypeList.get(i).getId() == 5) {
                    firstTypeList.remove(i);
                    break;
                }
            }
        }
        allResponse.setFirstTypeList(firstTypeList);
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
     * 查询所有分类（实装）
     *
     * @param justFirst
     * @return
     */
    @AuthTarget(TargetEnum.REAL_MANAGE)
    @ApiOperation("查询所有分类")
    @ApiImplicitParam(name = "justFirst", value = "获取第一层", dataTypeClass = Boolean.class)
    @GetMapping("/real-equipment-categories")
    public Result<TypeAllVO> realEquipmentCategories(@RequestParam Boolean justFirst) {
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        if (Objects.isNull(l1AndL2AndL3Type)) {
            return new Result<>();
        }

        // 根据产品规划，需要对获取的分类名称进行修改增删
        // 修改为：服务器（原操作系统）/网络设备/防御资源（原安全设备）/攻击资源/硬件外设
        List<TypeConvertModel> newFirst = new ArrayList<>();
        List<TypeConvertModel> newSecond = new ArrayList<>();
        List<TypeConvertModel> newThird = new ArrayList<>();
        List<Integer> l1Ids = new ArrayList<>();
        List<Integer> l2Ids = new ArrayList<>();
        List<Integer> l3Ids = new ArrayList<>();

        // 修改一级分类
        for (TypeConvertModel typeConvertModel : l1AndL2AndL3Type.getFirstTypeList()) {
            if ("操作系统".equals(typeConvertModel.getName())) {
                l1Ids.add(typeConvertModel.getId());
                typeConvertModel.setName("服务器");
                newFirst.add(typeConvertModel);
            } else if ("网络设备".equals(typeConvertModel.getName())) {
                l1Ids.add(typeConvertModel.getId());
                newFirst.add(typeConvertModel);
            } else if ("安全防护设备".equals(typeConvertModel.getName())) {
                l1Ids.add(typeConvertModel.getId());
                typeConvertModel.setName("防御资源");
                newFirst.add(typeConvertModel);
            }
        }
        // 添加新的两个一级分类
        List<Integer> newTypeIds = getNewTypeId(l1Ids, 2);
        TypeConvertModel typeConvertModel1 = new TypeConvertModel();
        typeConvertModel1.setId(newTypeIds.get(0));
        typeConvertModel1.setName("攻击资源");
        TypeConvertModel typeConvertModel2 = new TypeConvertModel();
        typeConvertModel2.setId(newTypeIds.get(1));
        typeConvertModel2.setName("硬件外设");
        newFirst.add(typeConvertModel1);
        newFirst.add(typeConvertModel2);

        // 若只获取一级分类信息，则直接返回
        if (null != justFirst && !justFirst){
            // 修改二级分类
            for (TypeConvertModel typeConvertModel : l1AndL2AndL3Type.getSecondTypeList()) {
                if (l1Ids.contains(typeConvertModel.getL1ParentId())) {
                    newSecond.add(typeConvertModel);
                    l2Ids.add(typeConvertModel.getId());
                }
            }
            // 添加新的两个二级分类
            TypeConvertModel typeConvertModel11 = new TypeConvertModel();
            TypeConvertModel typeConvertModel22 = new TypeConvertModel();
            typeConvertModel11.setName(typeConvertModel1.getName());
            typeConvertModel11.setL1ParentId(typeConvertModel1.getId());
            typeConvertModel22.setName(typeConvertModel2.getName());
            typeConvertModel22.setL1ParentId(typeConvertModel2.getId());
            List<Integer> newTypeIdsFor2 = getNewTypeId(l2Ids, 2);
            typeConvertModel11.setId(newTypeIdsFor2.get(0));
            typeConvertModel22.setId(newTypeIdsFor2.get(1));
            newSecond.add(typeConvertModel11);
            newSecond.add(typeConvertModel22);

            // 修改三级分类
            for (TypeConvertModel typeConvertModel : l1AndL2AndL3Type.getThirdTypeList()) {
                if (l2Ids.contains(typeConvertModel.getL1ParentId())) {
                    newThird.add(typeConvertModel);
                    l3Ids.add(typeConvertModel.getId());
                }
            }
            // 添加新的两个三级分类
            //TypeConvertModel typeConvertModel111 = new TypeConvertModel();
            //TypeConvertModel typeConvertModel222 = new TypeConvertModel();
            //typeConvertModel111.setName(typeConvertModel11.getName());
            //typeConvertModel111.setL1ParentId(typeConvertModel1.getId());
            //typeConvertModel111.setL2ParentId(typeConvertModel11.getId());
            //typeConvertModel222.setName(typeConvertModel22.getName());
            //typeConvertModel222.setL1ParentId(typeConvertModel2.getId());
            //typeConvertModel222.setL2ParentId(typeConvertModel22.getId());
            //List<Integer> newTypeIdsFor3 = getNewTypeId(l3Ids, 2);
            //typeConvertModel111.setId(newTypeIdsFor3.get(0));
            //typeConvertModel222.setId(newTypeIdsFor3.get(1));
            //newThird.add(typeConvertModel111);
            //newThird.add(typeConvertModel222);
        }

        // 拼装返回对象
        return new Result<>(new TypeAllVO(newFirst, newSecond, newThird));
    }

    /**
     * @param l1Ids
     * @param count
     * @return
     */
    private List<Integer> getNewTypeId(List<Integer> l1Ids, int count) {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= l1Ids.size() + count; i++) {
            if (!l1Ids.contains(i)) {
                result.add(i);
                if (result.size() == count) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 查询所有分类
     *
     * @return
     */
    @AuthTarget(TargetEnum.REAL_MANAGE)
    @ApiOperation("查询所有分类")
    @GetMapping("/categories")
    public Result<TypeAllVO> list() {
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        if (Objects.isNull(l1AndL2AndL3Type)) {
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO(l1AndL2AndL3Type.getFirstTypeList(),
                l1AndL2AndL3Type.getSecondTypeList(), l1AndL2AndL3Type.getThirdTypeList());
        return new Result<>(response);
    }

    /**
     * 查询所有分类
     *
     * @param firstTypeId
     * @return
     */
    @AuthTarget(TargetEnum.DEVOPS_CATEGORY_GROUP)
    @ApiOperation("查询所有二级分类")
    @ApiImplicitParam(name = "firstTypeId", value = "一级分类id", dataTypeClass = Integer.class)
    @GetMapping("/l2-categories")
    public Result<TypeL2AllVO> l2List(@RequestParam(value = "firstTypeId", required = false) Integer firstTypeId) {
        ListResult<TargetType> l1AndL2AndL3Type = typeConverter.findL1AndL2Type();
        if (Objects.isNull(l1AndL2AndL3Type)) {
            return new Result<>();
        }
        if (Objects.nonNull(firstTypeId)) {
            List<TargetType> l2AndL3TypeList = l1AndL2AndL3Type.getList();
            List<TargetType> list = new ArrayList<>();
            for (TargetType type : l2AndL3TypeList) {
                if (Objects.equals(firstTypeId, type.getId())) {
                    list.add(type);
                }
            }
            l2AndL3TypeList = list;
            l1AndL2AndL3Type.setList(l2AndL3TypeList);
        }
        return new Result<>(new TypeL2AllVO(l1AndL2AndL3Type.getList()));
    }

    /**
     * 查询所有角色
     *
     * @return
     */
    @ApiOperation("查询所有角色")
    @GetMapping("/roles")
    public Result<TypeAllVO> roles() {
        TypeAllConvertModel model = typeConverter.findRoles();
        if (Objects.isNull(model)) {
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setRole(model.getIdIntegerModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有部署类型
     *
     * @return
     */
    @ApiOperation("查询所有部署类型")
    @GetMapping("/deploy_type")
    public Result<TypeAllVO> deployType() {
        TypeAllConvertModel model = typeConverter.findDeployType();
        if (Objects.isNull(model)) {
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setDeployType(model.getIdIntegerModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有虚拟类型
     *
     * @return
     */
    @ApiOperation("查询所有虚拟类型")
    @GetMapping("/image-types")
    public Result<TypeAllVO> imageType() {
        TypeAllConvertModel model = typeConverter.findImageType();
        if (Objects.isNull(model)) {
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setImageType(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有语言
     *
     * @return
     */
    @ApiOperation("查询所有语言")
    @GetMapping("/language")
    public Result<TypeAllVO> language() {
        TypeAllConvertModel model = typeConverter.findLanguage();
        if (Objects.isNull(model)) {
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setLanguage(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有系统架构
     *
     * @return
     */
    @ApiOperation("查询所有系统架构")
    @GetMapping("/system-architecure")
    public Result<TypeAllVO> systemArchitecure() {
        TypeAllConvertModel model = typeConverter.findSystemArchitecure();
        if (Objects.isNull(model)) {
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setSystemArchitecure(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询支持的磁盘格式
     *
     * @return
     */
    @ApiOperation("查询支持的磁盘格式")
    @GetMapping("/disk-format")
    public Result<TypeAllVO> diskFormat() {
        TypeAllConvertModel model = typeConverter.findDiskFormat();
        if (Objects.isNull(model)) {
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setDiskFormat(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有访问类型
     *
     * @return
     */
    @ApiOperation("查询所有访问类型")
    @GetMapping("/access-type")
    public Result<TypeAllVO> accessType() {
        TypeAllConvertModel model = typeConverter.findAccessType();
        if (Objects.isNull(model)) {
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setAccessType(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有服务分类
     *
     * @return
     */
    @AuthTarget(TargetEnum.SERVICE_MANAGE)
    @ApiOperation("查询所有服务管理分类")
    @GetMapping("/service-mg-categories")
    public Result<TypeAllVO> serviceMgCategories() {
        TypeAllConvertModel serviceCategories = typeConverter.findServiceCategories();
        TypeAllConvertModel deployType = typeConverter.findDeployType();
        TypeAllConvertModel installerPlatforms = typeConverter.findInstallerPlatforms();

        TypeAllVO response = new TypeAllVO();
        response.setDeployType(Objects.nonNull(deployType) ? deployType.getIdIntegerModelList() : null);
        response.setServiceCategories(Objects.nonNull(serviceCategories) ?
                serviceCategories.getIdStringModelList() : null);
        response.setInstallerPlatforms(Objects.nonNull(installerPlatforms) ? installerPlatforms.getIdStringModelList() : null);
        return new Result<>(response);
    }

    /**
     * 查询所有实装管理分类
     *
     * @return
     */
    @ApiOperation("查询所有实装管理分类")
    @GetMapping("/entity-mg-categories")
    public Result<TypeAllVO> entityMgCategories() {
        TypeAllConvertModel entityRoles = typeConverter.findEntityRoles();
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        TypeAllConvertModel language = typeConverter.findLanguage();
        TypeAllConvertModel systemArchitecure = typeConverter.findSystemArchitecure();
        TypeAllConvertModel snmpVersion = typeConverter.findSnmpVersion();
        TypeAllVO allResponse = new TypeAllVO();
        List<SerialPortConvertModel> serialPortConvertModels = serialPortConverter.list();
        allResponse.setSerialPortRespons(Lists.newArrayList(new SerialPortVO(CCM_1, CCM_1, serialPortConvertModels)));
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
     *
     * @return
     */
    @AuthTarget(TargetEnum.IMAGE_MANAGE)
    @ApiOperation("查询所有服务分类")
    @GetMapping("/service-categories")
    public Result<TypeAllVO> serviceCategories() {
        TypeAllConvertModel model = typeConverter.findServiceCategories();
        if (Objects.isNull(model)) {
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setServiceCategories(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有服务管理-启动参数类型
     *
     * @return
     */
    @ApiOperation("查询启动参数类型")
    @GetMapping("/service-start-type")
    public Result<TypeAllVO> serviceStartType() {
        TypeAllConvertModel model = typeConverter.findServiceStartType();
        if (Objects.isNull(model)) {
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setServiceStartTypes(model.getIdStringModelList());
        return new Result<>(response);
    }

    /**
     * 查询所有平台
     *
     * @return
     */
    @ApiOperation("查询所有平台数据")
    @GetMapping("/installer-platforms")
    public Result<TypeAllVO> installerPlatforms() {
        TypeAllConvertModel model = typeConverter.findInstallerPlatforms();
        if (Objects.isNull(model)) {
            return new Result<>();
        }
        TypeAllVO response = new TypeAllVO();
        response.setInstallerPlatforms(model.getIdStringModelList());
        return new Result<>(response);

    }

    /**
     * 查询串口配置
     *
     * @return
     */
    @AuthTarget({TargetEnum.REAL_EQUIPMENT_MANAGEMENT, TargetEnum.REAL_MANAGE})
    @ApiOperation("查询串口配置")
    @GetMapping("/serial-port-list")
    public Result<List<SerialPortVO>> serialPortList() {
        return new Result<>(Collections.emptyList());
    }

    /**
     * 查询交换机部分信息数据
     *
     * @return
     */
    @AuthTarget(TargetEnum.REAL_MANAGE)
    @ApiOperation("查询交换机部分信息数据")
    @GetMapping("/switch-tree-list")
    public Result<List<SwitchConvertModel>> treeList() {
        return new Result<>(switchConverter.treeList());
    }

    /**
     * 查询交换机所有信息
     *
     * @return
     */
    @AuthTarget(TargetEnum.REAL_EQUIPMENT_MANAGEMENT)
    @ApiOperation("查询交换机所有信息")
    @GetMapping("/switch-list")
    public Result<List<Switches>> switchList() {
        return new Result<>(switchConverter.list());
    }
}
