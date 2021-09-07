package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.TypeConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.*;
import cn.cyberpeace.mp.devops.cluster.model.TypeCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TypeIdIntegerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TypeIdStringCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TypeL2CloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.TypeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TypeConverterImpl implements TypeConverter {

    private TypeService typeService;

    @Autowired
    public TypeConverterImpl(TypeService typeService) {
        this.typeService = typeService;
    }

    @Override
    public TypeAllConvertModel findL1AndL2AndL3Type() {
        TypeCloudPlatformModel l1AndL2AndL3Type = typeService.findL1AndL2AndL3Type();
        if (Objects.isNull(l1AndL2AndL3Type) || CollectionUtils.isEmpty(l1AndL2AndL3Type.getItems())){
            return null;
        }
        return convertModel(l1AndL2AndL3Type);
    }

    @Override
    public TypeL2AllConvertModel findL1AndL2Type() {
        TypeL2CloudPlatformModel l1AndL2Type = typeService.findL1AndL2Type();
        if (Objects.isNull(l1AndL2Type) || CollectionUtils.isEmpty(l1AndL2Type.getItems())){
            return null;
        }

        return new TypeL2AllConvertModel(l1AndL2Type.getItems());
    }

    @Override
    public TypeAllConvertModel findIdIntegerByType(String type) {
        TypeIdIntegerCloudPlatformModel list = typeService.findIdIntegerByType(type);
        if (Objects.isNull(list) || CollectionUtils.isEmpty(list.getItems())){
            return null;
        }
        return convertModel(list);
    }

    @Override
    public TypeAllConvertModel findIdStringByType(String type) {
        TypeIdStringCloudPlatformModel list = typeService.findIdStringByType(type);
        if (Objects.isNull(list) || CollectionUtils.isEmpty(list.getItems())){
            return null;
        }
        return convertModel(list);
    }

    @Override
    public TypeAllConvertModel findRoles() {
        return this.findIdIntegerByType("role");
    }

    @Override
    public TypeAllConvertModel findDeployType() {
        return this.findIdIntegerByType("deploy_type");
    }

    @Override
    public TypeAllConvertModel findImageType() {
        return this.findIdStringByType("image_type");
    }

    @Override
    public TypeAllConvertModel findLanguage() {
        return this.findIdStringByType("language");
    }

    @Override
    public TypeAllConvertModel findSystemArchitecure() {
        return this.findIdStringByType("system_architecure");
    }

    @Override
    public TypeAllConvertModel findDiskFormat() {
        return this.findIdStringByType("disk_format");
    }

    @Override
    public TypeAllConvertModel findAccessType() {
        return this.findIdStringByType("access_type");
    }

    @Override
    public TypeAllConvertModel findServiceCategories() {
        return this.findIdStringByType("categories");
    }

    @Override
    public TypeAllConvertModel findInstallerPlatforms() {
        return this.findIdStringByType("installer_platforms");
    }

    @Override
    public TypeAllConvertModel findServiceStartType() {
        return this.findIdStringByType("type");
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
