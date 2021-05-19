package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseSceneElementBO<M extends BaseSceneElementModel, P extends BaseSceneElementPortBO<?>>
        extends BaseSceneNodeBO<M> {
    private Integer type;
    private String file;
    private String systemType;
    private String systemArchitecture;
    private String systemLanguage;
    private String flavor;
    private List<AccessMode> accessModes;
    private List<String> initCommands;
    private List<P> ports;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields, "accessModes", "initCommands"));
        accessModes = StringUtils.isEmpty(obj.getAccessModes()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getAccessModes(), new TypeReference<List<AccessMode>>(){});
        initCommands = StringUtils.isEmpty(obj.getInitCommands()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getInitCommands(), new TypeReference<List<String>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields, "accessModes", "initCommands"));
        obj.setAccessModes(JsonUtils.toJsonString(accessModes));
        obj.setInitCommands(JsonUtils.toJsonString(initCommands));
        return obj;
    }
}
