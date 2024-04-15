package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.devopscluster.cloud.platform.service.TypeService;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeIdIntegerConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeIdStringConvertModel;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.TargetType;
import cn.cyberpeace.mp.devopscluster.model.common.TypeItemModel;
import cn.cyberpeace.mp.devopscluster.model.common.TypeItemNameModel;
import cn.cyberpeace.mp.devopscluster.model.common.TypeTreeItemModel;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wanggang
 */
@Service
@RequiredArgsConstructor
public class TypeConverter {

    private static final Integer INDUSTRY_CONTROL_EQUIPMENT = 6;

    private final TypeService typeService;

    private final SysConfigService sysConfigService;

    /**
     * 查询所有类型数据
     * @return
     */
    public TypeAllConvertModel findL1AndL2AndL3Type() {
        CloudPlatformPageResult<TypeTreeItemModel> l1AndL2AndL3Type = typeService.findL1AndL2AndL3Type(Boolean.FALSE);
        return convertTypeTreeItemModel(l1AndL2AndL3Type);
    }

    /**
     * 查询所有类型数据
     * @return
     */
    public TypeAllConvertModel findL1AndL2AndL3Type(Boolean isAll) {
        CloudPlatformPageResult<TypeTreeItemModel> l1AndL2AndL3Type = typeService.findL1AndL2AndL3Type(isAll);
        return convertTypeTreeItemModel(l1AndL2AndL3Type);
    }

    /**
     * 查询类型数据，仅返回L1和L2级类型数据
     * @return
     */
    public ListResult<TargetType> findL1AndL2Type() {
        CloudPlatformPageResult<TargetType> l1AndL2Type = typeService.findL1AndL2Type();

        List<TargetType> targetTypes = Optional.ofNullable(l1AndL2Type).map(CloudPlatformPageResult::getList).orElse(Collections.emptyList());
        // task-view-12214 底层增加了id为6的工控设备, 但是目前基线版本，前台不需要展示。
        if (CollectionUtils.isNotEmpty(targetTypes)) {
            List<TargetType> list = new ArrayList<>();
            for (TargetType type : targetTypes) {
                if (!INDUSTRY_CONTROL_EQUIPMENT.equals(type.getId())) {
                    list.add(type);
                }
            }
            targetTypes = list;
        }

        return new ListResult<>(targetTypes);
    }

    /**
     * 查询ID为数字类型的枚举数据
     * @param type
     * @return
     */
    public TypeAllConvertModel findIdIntegerByType(String type) {
        CloudPlatformPageResult<TypeItemModel> list = typeService.findIdIntegerByType(type,Boolean.FALSE);
        return convertModel(list);
    }

    /**
     * 查询ID为数字类型的枚举数据
     * @param type
     * @return
     */
    public TypeAllConvertModel findIdIntegerByType(String type,Boolean isAll) {
        CloudPlatformPageResult<TypeItemModel> list = typeService.findIdIntegerByType(type,isAll);
        return convertModel(list);
    }

    /**
     * 查询ID为字符串类型的枚举数据
     * @param type
     * @return
     */
    public TypeAllConvertModel findIdStringByType(String type) {
        CloudPlatformPageResult<TypeItemNameModel> list = typeService.findIdStringByType(type,Boolean.FALSE);
        return convertTypeItemNameModel(list);
    }

    /**
     * 查询ID为字符串类型的枚举数据
     * @param type
     * @return
     */
    public TypeAllConvertModel findIdStringByType(String type,Boolean isAll) {
        CloudPlatformPageResult<TypeItemNameModel> list = typeService.findIdStringByType(type,isAll);
        return convertTypeItemNameModel(list);
    }

    /**
     * 查询角色枚举值
     * @return
     */
    public TypeAllConvertModel findRoles() {
        return this.findIdIntegerByType("role");
    }

    /**
     * 查询角色枚举值
     * @return
     */
    public TypeAllConvertModel findRoles(Boolean isAll) {
        return this.findIdIntegerByType("role",isAll);
    }

    /**
     * 查询实体设备角色枚举值
     * @return
     */
    public TypeAllConvertModel findEntityRoles() {
        return this.findIdIntegerByType("entity_role");
    }

    /**
     * 查询部署类型枚举值
     * @return
     */
    public TypeAllConvertModel findDeployType() {
        return this.findIdIntegerByType("deploy_type");
    }

    /**
     * 查询镜像类型枚举值
     * @return
     */
    public TypeAllConvertModel findImageType() {
        return this.findIdStringByType("image_type");
    }

    /**
     * 查询镜像类型枚举值
     * @return
     */
    public TypeAllConvertModel findImageType(Boolean isAll) {
        return this.findIdStringByType("image_type",isAll);
    }

    /**
     * 查询语言枚举值
     * @return
     */
    public TypeAllConvertModel findLanguage() {
        return this.findIdStringByType("language");
    }

    /**
     * 查询系统架构枚举值
     * @return
     */
    public TypeAllConvertModel findSystemArchitecure() {
        // 底层更新了字段名称，中台其他地方还是使用system_architecure字段；
        return this.findIdStringByType("system_architecture");
    }

    /**
     * 查询磁盘格式枚举值
     * @return
     */
    public TypeAllConvertModel findDiskFormat() {
        return this.findIdStringByType("disk_format");
    }

    /**
     * 查询访问类型
     * @return
     */
    public TypeAllConvertModel findAccessType() {
        return this.findIdStringByType("access_type");
    }

    /**
     * 查询服务分类
     * @return
     */
    public TypeAllConvertModel findServiceCategories() {
        return this.findIdStringByType("categories");
    }

    /**
     * 查询平台
     * @return
     */
    public TypeAllConvertModel findInstallerPlatforms() {
        return this.findIdStringByType("installer_platforms");
    }

    /**
     * 查询服务管理-docker服务：启动参数类型
     * @return
     */
    public TypeAllConvertModel findServiceStartType() {
        return this.findIdStringByType("type");
    }

    /**
     * 查询SNMP版本
     * @return
     */
    public TypeAllConvertModel findSnmpVersion() {
        return this.findIdStringByType("snmp_version");
    }

    /**
     *
     * @param type
     * @return
     */
    public List<String> findItemsByType(String type){
        CloudPlatformPageResult<String> itemsByType = typeService.findItemsByType(type);
        return itemsByType.getList();
    }

    /**
     * 查询磁盘控制器类型
     * @return
     */
    public List<String> findHwDiskBus(){
        return this.findItemsByType("hw_disk_bus");
    }

    /**
     * 查询虚拟网卡型号
     * @return
     */
    public List<String> findHwVifModel(){
        return this.findItemsByType("hw_vif_model");
    }

    /**
     * 查询视频图像驱动
     * @return
     */
    public List<String> findHwVideoModel(){
        return this.findItemsByType("hw_video_model");
    }

    /**
     * 转换TypeOfRoleCloudPlatformModel对象
     * @param list
     * @return
     */
    private TypeAllConvertModel convertTypeItemNameModel(CloudPlatformPageResult<TypeItemNameModel> list){
        List<TypeIdStringConvertModel> modelList = new ArrayList<>();
        for (TypeItemNameModel item : list.getList()) {
            TypeIdStringConvertModel typeIdStringConvertModel = new TypeIdStringConvertModel(item.getId(), item.getName());
            modelList.add(typeIdStringConvertModel);
        }
        TypeAllConvertModel model =  new TypeAllConvertModel();
        model.setIdStringModelList(modelList);
        return model;
    }

    /**
     * 转换TypeOfRoleCloudPlatformModel对象
     * @param list
     * @return
     */
    private TypeAllConvertModel convertModel(CloudPlatformPageResult<TypeItemModel> list){
        List<TypeIdIntegerConvertModel> modelList = new ArrayList<>();
        for (TypeItemModel item : list.getList()) {
            TypeIdIntegerConvertModel typeIdIntegerConvertModel = new TypeIdIntegerConvertModel(item.getId(), item.getName(), item.getParentId(), item.getChild());
            modelList.add(typeIdIntegerConvertModel);
        }
        TypeAllConvertModel model =  new TypeAllConvertModel();
        model.setIdIntegerModelList(modelList);
        return model;
    }

    /**
     * 转换Type
     * @param l1AndL2AndL3Type
     * @return
     */
    private TypeAllConvertModel convertTypeTreeItemModel(CloudPlatformPageResult<TypeTreeItemModel> l1AndL2AndL3Type){
        boolean industriaControl = false;
        SysConfigModel model = sysConfigService.getByKeyName("system_type");
        if (Objects.nonNull(model)) {
            String systemType = model.getValue();
            // 工控:2
            if ("2".equals(systemType)) {
                industriaControl = true;
            }
        }

        List<TypeConvertModel> firstTypeList = new ArrayList<>();
        List<TypeConvertModel> secondTypeList = new ArrayList<>();
        List<TypeConvertModel> thirdTypeList = new ArrayList<>();
        for (TypeTreeItemModel item : l1AndL2AndL3Type.getList()){
            // 非工控类型，过滤工控设备
            if (!industriaControl && item.getL1Id() == 6) {
                continue;
            }

            TypeConvertModel typeConvertModel = new TypeConvertModel();
            typeConvertModel.setId(item.getL1Id());
            typeConvertModel.setName(item.getL1Name());
            firstTypeList.add(typeConvertModel);

            if (CollectionUtils.isEmpty(item.getL1Child())){
                continue;
            }
            for (TypeTreeItemModel.L1Child l1Child : item.getL1Child()){
                TypeConvertModel second = new TypeConvertModel();
                second.setId(l1Child.getL2Id());
                second.setName(l1Child.getL2Name());
                second.setL1ParentId(item.getL1Id());
                secondTypeList.add(second);

                if (CollectionUtils.isEmpty(l1Child.getL2Child())){
                    continue;
                }

                for (TypeTreeItemModel.L1Child.L2Child l2Child : l1Child.getL2Child()){
                    TypeConvertModel third = new TypeConvertModel();
                    third.setId(l2Child.getL3Id());
                    third.setName(l2Child.getL3Name());
                    third.setL1ParentId(item.getL1Id());
                    third.setL2ParentId(l1Child.getL2Id());
                    thirdTypeList.add(third);
                }
            }
        }
        return new TypeAllConvertModel(firstTypeList,secondTypeList,thirdTypeList);
    }
}
