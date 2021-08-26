package cn.cyberpeace.mp.base.bo;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.SerializableFunction;
import lombok.Data;

import java.util.Arrays;

/**
 * 对应表模型主体的业务模型
 * @param <M> 表模型类型
 */
@SuppressWarnings("java:S1610")
@Data
public abstract class ModelBO<M extends BaseModel, T extends ModelBO<M, T>> {

    /**
     * 从表模型数据对象载入数据
     * @param obj 表模型数据对象
     * @param ignoreFields 忽略字段
     */
    public void fromModel(M obj, String... ignoreFields) {
        BeanUtils.copyPropertiesIgnoreNull(obj, this, ignoreFields);
    }

    /**
     * 创建初始表模型对象
     * @return 初始表模型对象
     */
    public abstract M createModel();

    /**
     * 转换为表模型数据对象
     * @param ignoreFields 忽略字段
     * @return 表模型数据对象
     */
    public M toModel(String... ignoreFields) {
        var obj = createModel();
        BeanUtils.copyPropertiesIgnoreNull(this, obj, ignoreFields);
        return obj;
    }

    /**
     * 从表模型数据对象载入数据，忽略字段支持字段getter方法
     * @param obj 表模型数据对象
     * @param ignoreFieldGetters 忽略字段，支持字段getter方法
     */
    @SafeVarargs
    public final <T> void fromModelIgnore(M obj, SerializableFunction<T>... ignoreFieldGetters) {
        String[] ignoreFields = Arrays.stream(ignoreFieldGetters).map(BeanUtils::getBeanGetterFieldName)
                .toArray(String[]::new);
        fromModel(obj, ignoreFields);
    }

    /**
     * 转换为表模型数据对象，忽略字段支持字段getter方法
     * @param ignoreFieldGetters 忽略字段，支持字段getter方法
     * @return 表模型数据对象
     */
    @SafeVarargs
    public final <T> M toModelIgnore(SerializableFunction<T>... ignoreFieldGetters) {
        String[] ignoreFields = Arrays.stream(ignoreFieldGetters).map(BeanUtils::getBeanGetterFieldName)
                .toArray(String[]::new);
        return toModel(ignoreFields);
    }

    /**
     * 导出为资源数据，去除数据绑定关系
     * @return 资源数据对象
     */
    public T export() {
        return (T) this;
    }

    /**
     * 导出为平行资源数据，去除数据绑定关系
     * @param pClass 平行资源类
     * @param ignoreFields 忽略字段
     * @param <P> 平行资源类型
     * @return 平行资源数据
     */
    public <P> P export(Class<P> pClass, String... ignoreFields) {
        var pObj = ClassUtils.createInstance(pClass);
        BeanUtils.copyPropertiesIgnoreNull(export(), pObj, ignoreFields);
        return pObj;
    }

    /**
     * 导出为平行资源数据，去除数据绑定关系，忽略字段支持字段getter方法
     * @param pClass 平行资源类
     * @param ignoreFieldGetters 忽略字段，支持字段getter方法
     * @param <P> 平行资源类型
     * @return 平行资源数据
     */
    @SafeVarargs
    public final <P> P exportIgnore(Class<P> pClass, SerializableFunction<T>... ignoreFieldGetters) {
        String[] ignoreFields = Arrays.stream(ignoreFieldGetters).map(BeanUtils::getBeanGetterFieldName)
                .toArray(String[]::new);
        return export(pClass, ignoreFields);
    }

}
