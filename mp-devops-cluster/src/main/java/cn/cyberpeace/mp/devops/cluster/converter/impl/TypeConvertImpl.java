package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.TypeConvert;
import cn.cyberpeace.mp.devops.cluster.converter.model.*;
import cn.cyberpeace.mp.devops.cluster.model.TypeCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TypeInstallerPlatformAndImageTypeCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TypeOfRoleCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.TypeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TypeConvertImpl implements TypeConvert {

    private TypeService typeService;

    @Autowired
    public TypeConvertImpl(TypeService typeService) {
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
    public TypeAllConvertModel findL1AndL2Type() {
        TypeCloudPlatformModel l1AndL2Type = typeService.findL1AndL2Type();
        if (Objects.isNull(l1AndL2Type) || CollectionUtils.isEmpty(l1AndL2Type.getItems())){
            return null;
        }
        return convertModel(l1AndL2Type);
    }

    @Override
    public TypeAllConvertModel findAllRoleType() {
        TypeOfRoleCloudPlatformModel list = typeService.findAllRoleType();
        if (Objects.isNull(list) || CollectionUtils.isEmpty(list.getItems())){
            return null;
        }
        return convertModel(list);
    }

    @Override
    public TypeAllConvertModel findByType(String type) {
        TypeInstallerPlatformAndImageTypeCloudPlatformModel list = typeService.findAllInstallerPlatformAndImageType(type);
        if (Objects.isNull(list) || CollectionUtils.isEmpty(list.getItems())){
            return null;
        }
        return convertModel(list);
    }

    /**
     * 转换TypeOfRoleCloudPlatformModel对象
     * @param list
     * @return
     */
    private TypeAllConvertModel convertModel(TypeInstallerPlatformAndImageTypeCloudPlatformModel list){
        List<TypeIdStringConvertModel> modelList = list.getItems().stream()
                .map(item -> new TypeIdStringConvertModel(item.getId(),item.getName())).collect(Collectors.toList());
        TypeAllConvertModel model =  new TypeAllConvertModel();
        model.setInstallerPlatformAndImageTypeList(modelList);
        return model;
    }

    /**
     * 转换TypeOfRoleCloudPlatformModel对象
     * @param list
     * @return
     */
    private TypeAllConvertModel convertModel(TypeOfRoleCloudPlatformModel list){
        List<TypeIdIntegerConvertModel> modelList = list.getItems().stream()
                .map(item -> new TypeIdIntegerConvertModel(item.getId(),item.getName())).collect(Collectors.toList());
        TypeAllConvertModel model =  new TypeAllConvertModel();
        model.setRoleList(modelList);
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
