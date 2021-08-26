package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

@Data
public class CsceneBO extends BaseCsceneBO<CsceneModel, CsceneRoleModel, CsceneRoleBO, CsceneBO> {
    private Integer type;
    private SceneConfigDataBO sceneConfig;
    private List<CsceneInstanceBO> testScenes;
    private List<CsceneInstanceBO> appScenes;

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
        if (sceneConfig != null) {
            obj.setSceneConfig(sceneConfig.getTopology().getScene().getEid());
        }
        return obj;
    }

    public CsceneInstanceBO exportToCsceneInstance() {
        var csceneInstance = export(CsceneInstanceBO.class, CsceneInstaneRoleBO.class,
                CsceneBO::getType, CsceneBO::getSceneConfig, CsceneBO::getTestScenes, CsceneBO::getAppScenes);
        if (sceneConfig != null) {
            csceneInstance.setScene(sceneConfig.exportToScene());
        }
        return csceneInstance;
    }
}
