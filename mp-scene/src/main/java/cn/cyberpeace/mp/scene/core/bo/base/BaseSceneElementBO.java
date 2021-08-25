package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.base.util.SerializableFunction;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public abstract class BaseSceneElementBO<M extends BaseSceneElementModel, P extends BaseSceneElementPortBO<?, P>,
        T extends BaseSceneElementBO<M, P, T>> extends BaseSceneNodeBO<M, T> {
    private Integer type;
    private String file;
    private String systemType;
    private String systemArchitecture;
    private String systemLanguage;
    private String flavor;
    private List<String> initCommands;
    private List<P> ports;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseSceneElementModel::getInitCommands)));
        initCommands = StringUtils.isEmpty(obj.getInitCommands()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getInitCommands(), new TypeReference<List<String>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseSceneElementModel::getInitCommands)));
        if (initCommands != null) {
            obj.setInitCommands(JsonUtils.toJsonString(initCommands));
        }
        return obj;
    }

    @Override
    public T export() {
        if (ports != null) {
            setPorts(ports.stream().map(port -> port.export()).collect(Collectors.toList()));
        }

        return (T) this;
    }

    @Override
    public <P1> P1 export(Class<P1> pClass, String... ignoreFields) {
        return super.export(pClass, ArrayUtils.addAll(ignoreFields, "ports"));
    }

    public <P2 extends BaseSceneElementPortBO<?, P2>, P1 extends BaseSceneElementBO<?, P2, ?>> P1 export(
            Class<P1> pClass, Class<P2> portClass, SerializableFunction<T>... ignoreFieldGetters) {
        var pObj = super.exportIgnore(pClass, ignoreFieldGetters);
        if (ports != null) {
            pObj.setPorts(ports.stream().map(port -> port.export(portClass)).collect(Collectors.toList()));
        }

        return pObj;
    }
}
