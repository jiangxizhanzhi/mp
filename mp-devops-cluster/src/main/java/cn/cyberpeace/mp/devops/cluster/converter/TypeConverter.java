package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.TypeService;
import cn.cyberpeace.mp.devops.cluster.converter.model.*;
import cn.cyberpeace.mp.devops.cluster.model.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wanggang
 */
@Service
public class TypeConverter {

    private TypeService typeService;

    @Autowired
    public TypeConverter(TypeService typeService) {
        this.typeService = typeService;
    }

    /**
     * 查询所有类型数据
     * @return
     */
    public TypeAllConvertModel findL1AndL2AndL3Type() {
        TypeCloudPlatformModel l1AndL2AndL3Type = typeService.findL1AndL2AndL3Type();
        if (Objects.isNull(l1AndL2AndL3Type) || CollectionUtils.isEmpty(l1AndL2AndL3Type.getItems())){
            return null;
        }
        return convertModel(l1AndL2AndL3Type);
    }

    /**
     * 查询类型数据，仅返回L1和L2级类型数据
     * @return
     */
    public TypeL2AllConvertModel findL1AndL2Type() {
        TypeL2CloudPlatformModel l1AndL2Type = typeService.findL1AndL2Type();
        if (Objects.isNull(l1AndL2Type) || CollectionUtils.isEmpty(l1AndL2Type.getItems())){
            return null;
        }

        return new TypeL2AllConvertModel(l1AndL2Type.getItems());
    }

    /**
     * 查询ID为数字类型的枚举数据
     * @param type
     * @return
     */
    public TypeAllConvertModel findIdIntegerByType(String type) {
        TypeIdIntegerCloudPlatformModel list = typeService.findIdIntegerByType(type);
        if (Objects.isNull(list) || CollectionUtils.isEmpty(list.getItems())){
            return null;
        }
        return convertModel(list);
    }

    /**
     * 查询ID为字符串类型的枚举数据
     * @param type
     * @return
     */
    public TypeAllConvertModel findIdStringByType(String type) {
        TypeIdStringCloudPlatformModel list = typeService.findIdStringByType(type);
        if (Objects.isNull(list) || CollectionUtils.isEmpty(list.getItems())){
            return null;
        }
        return convertModel(list);
    }

    /**
     * 查询角色枚举值
     * @return
     */
    public TypeAllConvertModel findRoles() {
        return this.findIdIntegerByType("role");
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
        return this.findIdStringByType("system_architecure");
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
        TypeItemsCloudPlatformModel itemsByType = typeService.findItemsByType(type);
        if (Objects.isNull(itemsByType) || !itemsByType.isSuccess()){
            return Lists.newArrayList();
        }
        return itemsByType.getItems();
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
    private TypeAllConvertModel convertModel(TypeIdStringCloudPlatformModel list){
        List<TypeIdStringConvertModel> modelList = list.getItems().stream()
                .map(item -> new TypeIdStringConvertModel(item.getId(),item.getName())).collect(Collectors.toList());
        TypeAllConvertModel model =  new TypeAllConvertModel();
        model.setIdStringModelList(modelList);
        return model;
    }

    /**
     * 转换TypeOfRoleCloudPlatformModel对象
     * @param list
     * @return
     */
    private TypeAllConvertModel convertModel(TypeIdIntegerCloudPlatformModel list){
        List<TypeIdIntegerConvertModel> modelList = list.getItems().stream()
                .map(item -> new TypeIdIntegerConvertModel(item.getId(),item.getName())).collect(Collectors.toList());
        TypeAllConvertModel model =  new TypeAllConvertModel();
        model.setIdIntegerModelList(modelList);
        return model;
    }

    /**
     * 转换Type
     * @param l1AndL2AndL3Type
     * @return
     */
    private TypeAllConvertModel convertModel(TypeCloudPlatformModel l1AndL2AndL3Type){
        List<TypeConvertModel> firstTypeList = new ArrayList<>();
        List<TypeConvertModel> secondTypeList = new ArrayList<>();
        List<TypeConvertModel> thirdTypeList = new ArrayList<>();
        for (TypeCloudPlatformModel.Item item : l1AndL2AndL3Type.getItems()){
            TypeConvertModel typeConvertModel = new TypeConvertModel();
            typeConvertModel.setId(item.getL1Id());
            typeConvertModel.setName(item.getL1Name());
            firstTypeList.add(typeConvertModel);

            if (CollectionUtils.isEmpty(item.getL1Child())){
                continue;
            }
            for (TypeCloudPlatformModel.Item.L1Child l1Child : item.getL1Child()){
                TypeConvertModel second = new TypeConvertModel();
                second.setId(l1Child.getL2Id());
                second.setName(l1Child.getL2Name());
                second.setL1ParentId(item.getL1Id());
                secondTypeList.add(second);

                if (CollectionUtils.isEmpty(l1Child.getL2Child())){
                    continue;
                }

                for (TypeCloudPlatformModel.Item.L1Child.L2Child l2Child : l1Child.getL2Child()){
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
