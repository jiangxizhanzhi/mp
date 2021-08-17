package cn.cyberpeace.mp.base.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 定义对model业务处理字段，实现类只需要具体定义对应的静态字段
 */
public abstract class BaseFields {
    /** 全部字段 */
    public static final DynamicFields allFields = null;
    /** 业务可见字段 */
    public static final DynamicFields availableFields = null;
    /** 业务隐藏字段 */
    public static final DynamicFields hiddenFields = null;
    /** 业务只读字段 */
    public static final DynamicFields readonlyFields = null;
    /** 业务可搜索字段 */
    public static final DynamicFields searchFields = null;
    /** 业务可排序字段 */
    public static final DynamicFields orderFields = null;

    private DynamicFields getDefinedFields(String fieldName) {
        return ClassUtils.getStaticFieldValue(getClass(), fieldName,
                DynamicFields.class);
    }

    private DynamicFields getIntersectionFields(String fieldName, String[] uncertainFields) {
        DynamicFields intersectionFields = null;
        if (uncertainFields != null) {
            DynamicFields actualAvailableFields = getDefinedFields(fieldName);
            if (actualAvailableFields != null) {
                intersectionFields = actualAvailableFields.intersect(uncertainFields);
                if (!intersectionFields.hasValidFields()) {
                    intersectionFields = null;
                }
            }
        }

        return intersectionFields;
    }

    public DynamicFields getAllFields() {
        return getDefinedFields("allFields");
    }

    public DynamicFields getAvailableFields() {
        return getDefinedFields("availableFields");
    }

    public DynamicFields getHiddenFields() {
        return getDefinedFields("hiddenFields");
    }

    public DynamicFields getReadonlyFields() {
        return getDefinedFields("readonlyFields");
    }

    public DynamicFields getSearchFields() {
        return getDefinedFields("searchFields");
    }

    public DynamicFields getOrderFields() {
        return getDefinedFields("orderFields");
    }

    /**
     * 获取预期的有效字段
     * @param uncertainFields 预期字段
     * @return 有效字段
     */
    public DynamicFields getValidFields(String[] uncertainFields) {
        return getIntersectionFields("availableFields", uncertainFields);
    }

    /**
     * 获取预期的有效搜索字段
     * @param uncertainFields 预期字段
     * @return 有效字段
     */
    public DynamicFields getValidSearchFields(String[] uncertainFields) {
        return getIntersectionFields("searchFields", uncertainFields);
    }

    /**
     * 获取预期的有效排序字段
     * @param uncertainFields 预期字段
     * @return 有效字段
     */
    public DynamicFields getValidOrderFields(String[] uncertainFields) {
        return getIntersectionFields("orderFields", uncertainFields);
    }

    /**
     * 获取预期的有效排序字段
     * @param uncertainOrders 预期字段
     * @return 有效字段
     */
    public String[] getValidOrders(String[] uncertainOrders) {
        String[] validOrders = null;
        if (uncertainOrders != null && uncertainOrders.length > 0) {
            Map<String, String> fieldNameOrderMap = new LinkedHashMap<>();
            for (String order: uncertainOrders) {
                if (order.startsWith("-")) {
                    fieldNameOrderMap.put(order.substring(1), order);
                } else {
                    fieldNameOrderMap.put(order, order);
                }
            }
            String[] uncertainOrderFields = fieldNameOrderMap.keySet().toArray(new String[0]);
            DynamicFields validOrderFields = getValidOrderFields(uncertainOrderFields);
            if (validOrderFields != null) {
                List<String> validOrderList = new ArrayList<>();
                fieldNameOrderMap.forEach((fieldName, order) -> {
                    if (validOrderFields.hasField(fieldName)) {
                        validOrderList.add(order);
                    }
                });
                validOrders = validOrderList.toArray(new String[0]);
            }

        }

        return validOrders;
    }

    /**
     * 清理只读字段值
     * @param obj 数据对象
     */
    public void clearNotWritableFields(Object obj) {
        DynamicFields actualReadonlyFields = ClassUtils.getStaticFieldValue(getClass(), "readonlyFields",
                DynamicFields.class);
        if (actualReadonlyFields != null) {
            final BeanWrapper objWrapper = new BeanWrapperImpl(obj);
            for (String fieldName: actualReadonlyFields.getFields()) {
                objWrapper.setPropertyValue(fieldName, null);
            }
        }
    }
}
