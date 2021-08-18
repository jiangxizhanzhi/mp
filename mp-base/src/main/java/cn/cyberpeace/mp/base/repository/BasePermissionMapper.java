package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.annotation.Recommendation;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.auth.BasePermissionModel;
import cn.cyberpeace.mp.base.repository.condition.Condition;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface BasePermissionMapper<P extends BasePermissionModel, T extends BaseModel> extends BaseModelMapper {


    default HumanCondition getCondition() {
        HumanCondition condition = new HumanCondition();
        condition.setCurrentLinkName("t");
        return condition;
    }

    @Recommendation
    default List<T> listTargetModelsByOwner(String owner, HumanCondition condition, Class<P> permClz, Class<T> targetClz) {
        if (Objects.isNull(condition)) {
            condition = getCondition();
        }
        condition.linkAs(permClz, "p");
        condition.linkAs(targetClz, "t");
        condition.filterLink("p", "target", "t", "id");
        Map<String, String> selectFieldsMap = new HashMap<>();
        selectFieldsMap.put("t.*", "");
        condition.setSelectFields(selectFieldsMap);

        List<Map<String, Object>> mapList = listPerm(getTableName(condition), condition.toCondition(), getSelectFields(condition), owner);

        List<T> objs = new ArrayList<>();
        mapList.forEach(map -> {
            var obj = mapToTargetObj(map, targetClz);
            if (obj != null) {
                objs.add(obj);
            }
        });
        return objs;
    }



    @Recommendation
    default List<P> listModelsByOwner(String owner) {

        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("owner", owner);

        return list(humanCondition);
    }


    @Recommendation
    default List<P> listModelsByTarget(String target) {

        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("target", target);

        return list(humanCondition);
    }

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



    List<Map<String, Object>> listPerm(@Param("table") String table, @Param("condition") Condition condition,
                                       @Param("selectFields") String[] selectFields, @Param("owner") String owner);


    private T mapToTargetObj(Map<String, Object> map, Class targetClz) {
        if (map == null) {
            return null;
        }

        Class<T> actualModelClass = targetClz;
        if (actualModelClass == null) {
            return null;
        }

        T obj;
        try {
            obj = actualModelClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Convert {} to {} instance failed: {}", map, actualModelClass, e);
            return null;
        }

        Map<String, Class<?>> fieldTypes = ClassUtils.getBeanClassFieldTypes(actualModelClass);
        final BeanWrapper objWrapper = new BeanWrapperImpl(obj);
        fieldTypes.forEach((fieldName, fieldType) -> {
            String columnName = TextUtils.humpToLine(fieldName);
            if (map.containsKey(columnName)) {
                Object fieldValue = handleRawFieldValue(fieldType, map.get(columnName));
                objWrapper.setPropertyValue(fieldName, fieldValue);
            }
        });

        return obj;
    }


}
