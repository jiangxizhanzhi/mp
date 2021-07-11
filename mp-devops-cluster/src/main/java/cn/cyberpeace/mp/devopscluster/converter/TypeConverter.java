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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TypeConverter {

    private final TypeService typeService;

    private final SysConfigService sysConfigService;

    
    public TypeAllConvertModel findL1AndL2AndL3Type() {
        CloudPlatformPageResult<TypeTreeItemModel> l1AndL2AndL3Type = typeService.findL1AndL2AndL3Type(Boolean.FALSE);
        return convertTypeTreeItemModel(l1AndL2AndL3Type);
    }

    
    public TypeAllConvertModel findL1AndL2AndL3Type(Boolean isAll) {
        CloudPlatformPageResult<TypeTreeItemModel> l1AndL2AndL3Type = typeService.findL1AndL2AndL3Type(isAll);
        return convertTypeTreeItemModel(l1AndL2AndL3Type);
    }

    
    public ListResult<TargetType> findL1AndL2Type() {
        CloudPlatformPageResult<TargetType> l1AndL2Type = typeService.findL1AndL2Type();
        return new ListResult<>(l1AndL2Type.getList());
    }

    
    public TypeAllConvertModel findIdIntegerByType(String type) {
        CloudPlatformPageResult<TypeItemModel> list = typeService.findIdIntegerByType(type,Boolean.FALSE);
        return convertModel(list);
    }

    
    public TypeAllConvertModel findIdIntegerByType(String type,Boolean isAll) {
        CloudPlatformPageResult<TypeItemModel> list = typeService.findIdIntegerByType(type,isAll);
        return convertModel(list);
    }

    
    public TypeAllConvertModel findIdStringByType(String type) {
        CloudPlatformPageResult<TypeItemNameModel> list = typeService.findIdStringByType(type,Boolean.FALSE);
        return convertTypeItemNameModel(list);
    }

    
    public TypeAllConvertModel findIdStringByType(String type,Boolean isAll) {
        CloudPlatformPageResult<TypeItemNameModel> list = typeService.findIdStringByType(type,isAll);
        return convertTypeItemNameModel(list);
    }

    
    public TypeAllConvertModel findRoles() {
        return this.findIdIntegerByType("role");
    }

    
    public TypeAllConvertModel findRoles(Boolean isAll) {
        return this.findIdIntegerByType("role",isAll);
    }

    
    public TypeAllConvertModel findEntityRoles() {
        return this.findIdIntegerByType("entity_role");
    }

    
    public TypeAllConvertModel findDeployType() {
        return this.findIdIntegerByType("deploy_type");
    }

    
    public TypeAllConvertModel findImageType() {
        return this.findIdStringByType("image_type");
    }

    
    public TypeAllConvertModel findImageType(Boolean isAll) {
        return this.findIdStringByType("image_type",isAll);
    }

    
    public TypeAllConvertModel findLanguage() {
        return this.findIdStringByType("language");
    }

    
    public TypeAllConvertModel findSystemArchitecure() {
        return this.findIdStringByType("system_architecure");
    }

    
    public TypeAllConvertModel findDiskFormat() {
        return this.findIdStringByType("disk_format");
    }

    
    public TypeAllConvertModel findAccessType() {
        return this.findIdStringByType("access_type");
    }

    
    public TypeAllConvertModel findServiceCategories() {
        return this.findIdStringByType("categories");
    }

    
    public TypeAllConvertModel findInstallerPlatforms() {
        return this.findIdStringByType("installer_platforms");
    }

    
    public TypeAllConvertModel findServiceStartType() {
        return this.findIdStringByType("type");
    }

    
    public TypeAllConvertModel findSnmpVersion() {
        return this.findIdStringByType("snmp_version");
    }

    
    public List<String> findItemsByType(String type){
        CloudPlatformPageResult<String> itemsByType = typeService.findItemsByType(type);
        return itemsByType.getList();
    }

    
    public List<String> findHwDiskBus(){
        return this.findItemsByType("hw_disk_bus");
    }

    
    public List<String> findHwVifModel(){
        return this.findItemsByType("hw_vif_model");
    }

    
    public List<String> findHwVideoModel(){
        return this.findItemsByType("hw_video_model");
    }

    
    private TypeAllConvertModel convertTypeItemNameModel(CloudPlatformPageResult<TypeItemNameModel> list){
        List<TypeIdStringConvertModel> modelList = list.getList().stream()
                .map(item -> new TypeIdStringConvertModel(item.getId(),item.getName())).collect(Collectors.toList());
        TypeAllConvertModel model =  new TypeAllConvertModel();
        model.setIdStringModelList(modelList);
        return model;
    }

    
    private TypeAllConvertModel convertModel(CloudPlatformPageResult<TypeItemModel> list){
        List<TypeIdIntegerConvertModel> modelList = list.getList().stream()
                .map(item -> new TypeIdIntegerConvertModel(item.getId(),item.getName(), item.getParentId(), item.getChild())).collect(Collectors.toList());
        TypeAllConvertModel model =  new TypeAllConvertModel();
        model.setIdIntegerModelList(modelList);
        return model;
    }

    
    private TypeAllConvertModel convertTypeTreeItemModel(CloudPlatformPageResult<TypeTreeItemModel> l1AndL2AndL3Type){
        boolean industriaControl = false;
        SysConfigModel model = sysConfigService.getByKeyName("system_type");
        if (Objects.nonNull(model)) {
            String systemType = model.getValue();
            
            if ("2".equals(systemType)) {
                industriaControl = true;
            }
        }

        List<TypeConvertModel> firstTypeList = new ArrayList<>();
        List<TypeConvertModel> secondTypeList = new ArrayList<>();
        List<TypeConvertModel> thirdTypeList = new ArrayList<>();
        for (TypeTreeItemModel item : l1AndL2AndL3Type.getList()){
            
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
