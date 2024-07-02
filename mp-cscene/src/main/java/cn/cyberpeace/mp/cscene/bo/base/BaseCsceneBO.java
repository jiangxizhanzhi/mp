package cn.cyberpeace.mp.cscene.bo.base;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public abstract class BaseCsceneBO<
        M extends BaseCsceneModel,
        R0 extends BaseCsceneRoleModel,
        R extends BaseCsceneRoleBO<R0, R>,
        T extends BaseCsceneBO<M, R0, R, T>> extends ModelBO<M, T> {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "创建用户")
    private String createUser;
    @ApiModelProperty(value = "创建用户名")
    private String createUsername;
    @ApiModelProperty(value = "创建用户姓名")
    private String createName;
    @ApiModelProperty(value = "角色信息", hidden = true)
    @Deprecated
    private List<R> roles;
    @ApiModelProperty(value = "场景名称")
    private String name;
    @ApiModelProperty(value = "是否开启录屏")
    private Boolean enableRecording;
    @ApiModelProperty(value = "是否只是部分启动")
    private Boolean supportPartialStart;

    /**
     * export
     *
     * @return
     */
    @Override
    public T export() {
        setId(null);
        setCreateUser(null);
        setCreateUsername(null);
        return (T) this;
    }

    /**
     * export
     *
     * @param pClass
     * @param ignoreFields
     * @return
     */
    @Override
    public <P1> P1 export(Class<P1> pClass, String... ignoreFields) {
        return super.export(pClass, ArrayUtils.addAll(ignoreFields, "roles"));
    }

    /**
     * export
     *
     * @param pClass
     * @param roleClass
     * @param ignoreFieldGetters
     * @return
     */
    @SafeVarargs
    public final <R9 extends BaseCsceneRoleBO<?, R9>, P extends BaseCsceneBO<?, ?, R9, P>> P export(
            Class<P> pClass, Class<R9> roleClass, SerializableFunction<T>... ignoreFieldGetters) {
        P pObj = super.exportIgnore(pClass, ignoreFieldGetters);
        if (roles != null) {
            List<R9> list = new ArrayList<>();
            for (R role : roles) {
                R9 r9 = role.exportIgnore2(roleClass);
                list.add(r9);
            }
            pObj.setRoles(list);
        }
        return pObj;
    }
}
