package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@ApiModel(description = "场景模板数据")
@Data
public class CsceneUpdateBO extends BaseCsceneBO<CsceneModel, CsceneRoleModel, CsceneRoleBO, CsceneUpdateBO> {
    @ApiModelProperty(value = "场景实例id")
    private String csceneInstanceId;
    @ApiModelProperty(value = "场景配置")
    private SceneDataBO updateScene;

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
        if (updateScene != null && updateScene.getTopology() != null && updateScene.getTopology().getScene() != null) {
            obj.setSceneConfig(updateScene.getTopology().getScene().getEid());
        }
        return obj;
    }

    @Override
    public CsceneUpdateBO export() {
        var csceneBO = super.export();
        if (updateScene != null) {
            csceneBO.setUpdateScene(updateScene.export());
        }
        return this;
    }

    public CsceneInstanceBO exportToCsceneInstance() {
        var csceneInstance = export(CsceneInstanceBO.class, CsceneInstaneRoleBO.class, CsceneUpdateBO::getUpdateScene);
        if (updateScene != null) {
            csceneInstance.setScene(updateScene.export());
        }
        return csceneInstance;
    }

}
