package cn.cyberpeace.mp.auth.base.repository;

import cn.cyberpeace.mp.auth.base.repository.stereotype.PermissionModelClass;
import cn.cyberpeace.mp.base.annotation.Recommendation;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.auth.BasePermissionModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BasePermissionMapper<P extends BasePermissionModel, T extends BaseModel> extends BaseModelMapper {


    default HumanCondition getCondition(String owner) {
        HumanCondition condition = new HumanCondition();

        Class<P> permClz = getPermissionClass();
        Class<T> targetClz = getTargetClass();
        condition.link(targetClz);
        condition.link(permClz);
        condition.filterLink(permClz, "target", targetClz, "id");

        condition.filterEqual("owner", owner);
        condition.currentLink(targetClz);

        String targetAlias = condition.getLinkedModelNames().get(targetClz);
        Map<String, String> selectFieldsMap = new HashMap<>();
        selectFieldsMap.put(targetAlias + ".*", "");
        condition.setSelectFields(selectFieldsMap);

        return condition;
    }

    @Recommendation
    default List<T> listTargetsByOwner(HumanCondition condition) {
        return list(condition);
    }

    /*
    @Recommendation
    default List<P> listPermissionsByOwner(String owner) {

        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("owner", owner);

        return list(humanCondition);
    }


    @Recommendation
    default List<P> listPermissionsByTarget(String target) {

        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("target", target);

        return list(humanCondition);
    }
    */

    @Recommendation
    default int deleteByOwner(String owner) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("owner", owner);

        return filterDelete(humanCondition);
    }


    @Recommendation
    default int deleteByTarget(String target) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("target", target);

        return filterDelete(humanCondition);
    }



    private Class<P> getPermissionClass() {
        Class<?> actualModelClassSource;
        Class<?> mapperInterface = getMapperInterface();
        var modelClassAnnotation = mapperInterface.getAnnotation(PermissionModelClass.class);
        if (modelClassAnnotation != null) {
            actualModelClassSource = modelClassAnnotation.permissionClass();
        } else {
            throw new MpRuntimeException("PermissionClass not found in annotation!");
        }

        Class<P> actualModelClass;
        try {
            actualModelClass = (Class<P>) actualModelClassSource;
        } catch (Exception e) {
            log.error("Invalid model class[{}] for {}", actualModelClassSource, getClass());
            return null;
        }

        return actualModelClass;
    }


    private Class<T> getTargetClass() {
        Class<?> actualModelClassSource;
        Class<?> mapperInterface = getMapperInterface();
        var modelClassAnnotation = mapperInterface.getAnnotation(PermissionModelClass.class);
        if (modelClassAnnotation != null) {
            actualModelClassSource = modelClassAnnotation.targetClass();
        } else {
            throw new MpRuntimeException("TargetClass not found in annotation!");
        }

        Class<T> actualModelClass;
        try {
            actualModelClass = (Class<T>) actualModelClassSource;
        } catch (Exception e) {
            log.error("Invalid model class[{}] for {}", actualModelClassSource, getClass());
            return null;
        }

        return actualModelClass;
    }


}
