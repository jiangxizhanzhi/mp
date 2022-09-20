package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneUpdateDataBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

@ApiModel(description = "场景模板数据")
@Data
public class CsceneUpdateBO extends BaseCsceneBO<CsceneModel, CsceneRoleModel, CsceneRoleBO, CsceneUpdateBO> {
    @ApiModelProperty(value = "场景模板类型")
    private Integer type;
    @ApiModelProperty(value = "场景模板引用类型")
    private Integer referenceType;
    @ApiModelProperty(value = "场景模板引用元数据")
    private String referenceMetaData;
    @ApiModelProperty(value = "场景模板logo")
    private String logo;
    @ApiModelProperty(value = "场景模板拓扑配置")
    private SceneUpdateDataBO sceneConfig;
    @ApiModelProperty(value = "场景上下文属性")
    private CsceneContextBO context;

    @Override
    public CsceneModel createModel() {
        return new CsceneModel();
    }

    @Override
    public void fromModel(CsceneModel obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CsceneModel::getSceneConfig)));
    }

    @Override
    public CsceneModel toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CsceneModel::getSceneConfig)));
        if (sceneConfig != null && sceneConfig.getTopology() != null && sceneConfig.getTopology().getScene() != null) {
            obj.setSceneConfig(sceneConfig.getTopology().getScene().getEid());
        }
        return obj;
    }

    @Override
    public CsceneUpdateBO export() {
        var csceneBO = super.export();
        if (sceneConfig != null) {
            csceneBO.setSceneConfig(sceneConfig.export());
        }
        return this;
    }

    public CsceneInstanceBO exportToCsceneInstance() {
        var csceneInstance = export(CsceneInstanceBO.class, CsceneInstaneRoleBO.class,
                CsceneUpdateBO::getType, CsceneUpdateBO::getSceneConfig);
        if (sceneConfig != null) {
            csceneInstance.setScene(sceneConfig.exportToScene());
        }
        return csceneInstance;
    }

    public CsceneInstanceBO exportToCsceneInstance1() {
        var csceneInstance = export(CsceneInstanceBO.class, CsceneInstaneRoleBO.class,
                CsceneUpdateBO::getType, CsceneUpdateBO::getSceneConfig);
        if (sceneConfig != null) {
            csceneInstance.setScene(sceneConfig.exportToScene());
        }
        return csceneInstance;
    }
}
