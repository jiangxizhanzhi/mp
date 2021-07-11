package cn.cyberpeace.mp.sceneplugingns3.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugingns3.model.base.BaseGns3PluginSceneElementModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseGns3PluginSceneElementBO<M extends BaseGns3PluginSceneElementModel,
        T extends BaseGns3PluginSceneElementBO<M, T, S>, S extends BaseSceneElementBO<?, ?, S>>
        extends BasePluginSceneElementBO<M, T, S> {

    @ApiModelProperty(value = "镜像")
    private String image;

    @ApiModelProperty(value = "平台")
    private String platform;

    @ApiModelProperty(value = "ram")
    private Integer ram;

    @ApiModelProperty(value = "cpu")
    private Integer cpu;

    @ApiModelProperty(value = "disk")
    private Integer disk;

    
    @ApiModelProperty(value = "虚拟路由静态路由")
    @Valid
    private List<StaticRoute> staticRoutes;

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ignoreFields);
        if (staticRoutes != null) {
            obj.setStaticRoutes(JsonCommon.stringify(staticRoutes));
        }
        return obj;
    }

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ignoreFields);
        staticRoutes = StringUtils.isEmpty(obj.getStaticRoutes()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getStaticRoutes(), new TypeReference<List<StaticRoute>>() {
        });
    }
}
