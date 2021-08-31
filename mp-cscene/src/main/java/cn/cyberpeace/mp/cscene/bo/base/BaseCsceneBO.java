package cn.cyberpeace.mp.cscene.bo.base;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.base.util.SerializableFunction;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public abstract class BaseCsceneBO<
        M extends BaseCsceneModel,
        R0 extends BaseCsceneRoleModel,
        R extends BaseCsceneRoleBO<R0, R>,
        T extends BaseCsceneBO<M, R0, R, T>> extends ModelBO<M, T> {
    private String id;
    private List<R> roles;

    @Override
    public T export() {
        setId(null);
        return (T) this;
    }

    @Override
    public <P1> P1 export(Class<P1> pClass, String... ignoreFields) {
        return super.export(pClass, ArrayUtils.addAll(ignoreFields, "roles"));
    }

    @SafeVarargs
    public final <R9 extends BaseCsceneRoleBO<?, R9>, P extends BaseCsceneBO<?, ?, R9, P>> P export(
            Class<P> pClass, Class<R9> roleClass, SerializableFunction<T>... ignoreFieldGetters) {
        var pObj = super.exportIgnore(pClass, ignoreFieldGetters);
        if (roles != null) {
            pObj.setRoles(roles.stream().map(role -> role.exportIgnore2(roleClass)).collect(Collectors.toList()));
        }
        return pObj;
    }
}
